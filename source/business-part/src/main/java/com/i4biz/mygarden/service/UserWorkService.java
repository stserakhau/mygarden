package com.i4biz.mygarden.service;

import com.i4biz.mygarden.dao.*;
import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.domain.user.PatternAuthor;
import com.i4biz.mygarden.domain.user.UserWork;
import com.i4biz.mygarden.domain.user.UserWorkView;
import com.i4biz.mygarden.domain.user.task.TaskPlaningType;
import com.i4biz.mygarden.domain.user.task.UserTask;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.*;

@Service
public class UserWorkService extends AbstractService<UserWorkView, UserWork, Long> implements IUserWorkService {
    @Override
    protected GenericDAO<UserWorkView, UserWork, Long> getServiceDAO() {
        return userWorkDAO;
    }

    @Autowired
    private IUserWorkDAO userWorkDAO;

    @Autowired
    private IUserTaskDAO userTaskDAO;

    @Autowired
    private IItemDAO itemDAO;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UserWork saveOrUpdate(UserWork entity) {
        Item species = itemDAO.findById(entity.getSpeciesId());
        entity.setSpeciesName(species.getName());
        return super.saveOrUpdate(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserWork(long userId, long userWorkId) {
        UserWork uw = userWorkDAO.findById(userWorkId);
        if (uw.getUserId() != userId) {
            throw new RuntimeException("Invalid access for user " + userId + " to user work " + userWorkId);
        }

        userWorkDAO.delete(uw);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public UserWorkView findUserWorkViewById(long userWorkId, Long userId) {
        return userWorkDAO.findUserWorkViewById(userWorkId, userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void rebuildDates(long userWorkId, Long deleteUserTaskId) {
        List<UserTask> tasks = userTaskDAO.findUserTasksByUserWorkOrderedByStartDate(userWorkId);
        List<UserTask> newList = new ArrayList<>();
        Map<Long, UserTask> taskMap = new HashMap<>(tasks.size());
        Map<Long, Long> taskLink = new HashMap<Long, Long>();
        for (UserTask ut : tasks) {
            Long prevTaskId = ut.getDependsFromTaskId();
            if (prevTaskId == null) {
                newList.add(ut);
            }
            taskMap.put(ut.getId(), ut);
            taskLink.put(ut.getId(), prevTaskId);
        }
        while (!taskLink.isEmpty()) {
            for (Map.Entry<Long, Long> e : taskLink.entrySet()) {
                if (taskLink.containsKey(e.getValue())) {
                    continue;
                }
                newList.add(taskMap.get(e.getKey()));
                taskLink.remove(e.getKey());
                break;
            }
        }
        for (UserTask ut : newList) {
            switch (ut.getPlaningType()) {
                case planByWeather: {
                    Date calculatedDate = ut.getCalculatedDate();
                    if (calculatedDate == null) {
                        ut.setCalculatedDate(ut.getStartDate());
                        ut.setNotificationReady(false);
                    }
                    break;
                }
                case planByAnotherTask: {
                    Long prevTaskId = ut.getDependsFromTaskId();
                    if (!prevTaskId.equals(deleteUserTaskId)) {
                        UserTask prevTask = taskMap.get(prevTaskId);
                        int daysShift = ut.getCountDaysAfterPrevEvent();
                        Date calculatedDate = new Date(DateUtils.addDays(prevTask.getCalculatedDate(), daysShift).getTime());
                        ut.setStartDate(calculatedDate);
                        ut.setCalculatedDate(calculatedDate);
                        ut.setEndDate(calculatedDate);
                        ut.setNotificationReady(true);
                        break;
                    } else {//if previous task was removed convert task to type 'planByDate'
                        ut.setPlaningType(TaskPlaningType.planByDate);
                        ut.setCountDaysAfterPrevEvent(null);
                        ut.setDependsFromTaskId(null);
                        ut.setNotificationReady(true);
                    }
                }
                case planByDate: {
                    Date startDate = ut.getStartDate();
                    ut.setEndDate(startDate);
                    ut.setCalculatedDate(startDate);
                    ut.setNotificationReady(true);
                    break;
                }
            }

            List<Item> newList1 = new ArrayList<>();
            for (Item fertilizer : ut.getFertilizers()) {
                if (fertilizer.getId() == null) {
                    continue;
                }
                if (fertilizer.getName() == null) {
                    Item _fertilizer = itemDAO.findById(fertilizer.getId());
                    newList1.add(_fertilizer);
                } else {
                    newList1.add(fertilizer);
                }
            }
            ut.setFertilizers(newList1);

            userTaskDAO.update(ut);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Long cloneUserWorkAsPattern(UserWork userWork) {
        try {
            Long originalId = userWork.getId();
            userWork.setId(null);
            userWorkDAO.insert(userWork);

            Long cloneId = userWork.getId();

            Long userId = userWork.getUserId();

            List<UserTask> tasks;
//            if (userId == null) {
//                tasks = userTaskDAO.findUserTasksByUserWork(originalId);
//            } else {
            tasks = userTaskDAO.findUserTasksByUserWork(userId, originalId);
//            }
            Map<Long, Long> originalClonedIdsMap = new HashMap<>();
            for (int i = 0; i < tasks.size(); i++) {
                UserTask ut = tasks.get(i);
                UserTask utc = (UserTask) BeanUtils.cloneBean(ut);
                utc.setId(null);
                utc.setOwnerId(userWork.getUserId());
                utc.setUserWorkId(cloneId);
                if (utc.getPlaningType() == TaskPlaningType.planByAnotherTask) {
                    Long depFromTaskId = utc.getDependsFromTaskId();
                    Long clonedDependsFromTaskId = originalClonedIdsMap.get(depFromTaskId);
                    utc.setDependsFromTaskId(clonedDependsFromTaskId);
                }
                userTaskDAO.insert(utc);

                originalClonedIdsMap.put(ut.getId(), utc.getId());
            }

            return cloneId;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Long copyPatternToUser(long userWorkId, long speciesId, long userId, boolean asPattern) {
        try {
            UserWork pattern = userWorkDAO.findPatternById(userWorkId, userId);
            List<UserTask> patternTasks = userTaskDAO.findUserTasksByUserWork(userWorkId);

            UserWork userPattern = (UserWork) BeanUtils.cloneBean(pattern);
            userPattern.setId(null);
            userPattern.setUserId(userId);
            userPattern.setSpeciesId(speciesId);
            userPattern.setPattern(asPattern);
            userWorkDAO.insert(userPattern);

            Long userPatternId = userPattern.getId();

            Map<Long, Long> originalClonedIdsMap = new HashMap<>();
            for (int i = 0; i < patternTasks.size(); i++) {
                UserTask sysTask = patternTasks.get(i);
                UserTask userTask = (UserTask) BeanUtils.cloneBean(sysTask);
                userTask.setId(null);
                userTask.setUserWorkId(userPatternId);
                userTask.setOwnerId(userId);
                if (userTask.getPlaningType() == TaskPlaningType.planByAnotherTask) {
                    Long depFromTaskId = userTask.getDependsFromTaskId();
                    Long clonedDependsFromTaskId = originalClonedIdsMap.get(depFromTaskId);
                    userTask.setDependsFromTaskId(clonedDependsFromTaskId);
                }
                userTaskDAO.insert(userTask);

                originalClonedIdsMap.put(sysTask.getId(), userTask.getId());
            }

            return userPatternId;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Long initUserWork(UserWork pattern) {
        pattern.setId(null);
        pattern.setPattern(false);
        Item species = itemDAO.findById(pattern.getSpeciesId());
        pattern.setSpeciesName(species.getName());
        userWorkDAO.insert(pattern);
        return pattern.getId();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<UserWork> allSystemSpeciesPatterns(long systemSpeciesId) {
        return userWorkDAO.findAllSystemSpeciesPatterns(systemSpeciesId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSpeciesPatterns(long speciesId, long ownerId) {
        userWorkDAO.deleteSpeciesPatterns(speciesId, ownerId);
    }

    @Autowired
    IPatternAuthorDAO patternAuthorDAO;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Collection<PatternAuthor> getAllAuthors() {
        return patternAuthorDAO.findAll();
    }

    private static long diffInDays(Date date1, Date date2) {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.setTime(date1);
        end.setTime(date2);
        java.util.Date startDate = start.getTime();
        java.util.Date endDate = end.getTime();
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        long diffTime = endTime - startTime;
        long diffDays = diffTime / (1000 * 60 * 60 * 24);
        return diffDays;
    }

    private static Date addDays(Date date, int days) {
        return new Date(DateUtils.addDays(date, days).getTime());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void quickBuildUserWorkFromPattern(long userId, long patternId, Date startDate) {
        UserWork uw = userWorkDAO.findPatternById(patternId, userId);
        Long userWorkId = copyPatternToUser(patternId, uw.getSpeciesId(), userId, false);
        List<UserTask> ut = userTaskDAO.findUserTasksByUserWorkOrderedByStartDate(userWorkId);
        Date firstDate = ut.get(0).getStartDate();

        int daysDelta = (int) diffInDays(firstDate, startDate);
        ut.forEach(task -> {
            if (TaskPlaningType.planByDate == task.getPlaningType()) {
                Date newDate = addDays(task.getStartDate(), daysDelta);
                task.setStartDate(newDate);
                task.setEndDate(newDate);
                task.setCalculatedDate(newDate);
            }
            if (TaskPlaningType.planByWeather == task.getPlaningType()) {
                task.setStartDate(addDays(task.getStartDate(), daysDelta));
                task.setEndDate(addDays(task.getEndDate(), daysDelta));
                task.setCalculatedDate(addDays(task.getCalculatedDate(), daysDelta));
            }
        });
        rebuildDates(userWorkId, null);
    }
}

