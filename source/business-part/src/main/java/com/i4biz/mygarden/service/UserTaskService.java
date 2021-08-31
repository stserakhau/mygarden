package com.i4biz.mygarden.service;

import com.i4biz.mygarden.dao.GenericDAO;
import com.i4biz.mygarden.dao.IUserTaskDAO;
import com.i4biz.mygarden.domain.user.task.TaskPlaningType;
import com.i4biz.mygarden.domain.user.task.UserTask;
import com.i4biz.mygarden.domain.user.task.UserTaskView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class UserTaskService extends AbstractService<UserTaskView, UserTask, Long> implements IUserTaskService {
    @Autowired
    private IUserTaskDAO userTaskDAO;

    @Autowired
    private IUserWorkService userWorkService;

    @Override
    protected GenericDAO<UserTaskView, UserTask, Long> getServiceDAO() {
        return userTaskDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UserTask saveOrUpdate(UserTask userTask) {
        if (userTask.getPlaningType() == TaskPlaningType.planByWeather) {
            userTask.setNotificationReady(false);
        } else {
            userTask.setNotificationReady(true);
        }
        UserTask ut = super.saveOrUpdate(userTask);
        userWorkService.rebuildDates(userTask.getUserWorkId(), null);
        return ut;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserTask(long userId, long userTaskId) {
        UserTask ut = userTaskDAO.findById(userTaskId);
        if (ut.getOwnerId() != userId) {
            throw new RuntimeException("Invalid access. userId=" + userId + " userTaskId=" + userTaskId);
        }
        userWorkService.rebuildDates(ut.getUserWorkId(), ut.getId());
        userTaskDAO.delete(ut);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Collection<UserTask> findUserTasksByUserWork(long ownerId, long userWorkId) {
        return userTaskDAO.findUserTasksByUserWork(ownerId, userWorkId);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Collection<UserTask> findSystemUserTasksByUserWork(long userWorkId) {
        return userTaskDAO.findUserTasksByUserWork(userWorkId);
    }
}

