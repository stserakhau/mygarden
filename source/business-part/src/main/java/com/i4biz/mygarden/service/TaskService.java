package com.i4biz.mygarden.service;

import com.i4biz.mygarden.dao.GenericDAO;
import com.i4biz.mygarden.dao.ISpeciesTaskFertilizerDAO;
import com.i4biz.mygarden.dao.ITaskDAO;
import com.i4biz.mygarden.dao.IUserTaskDAO;
import com.i4biz.mygarden.domain.task.Task;
import com.i4biz.mygarden.domain.user.task.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService extends AbstractService<Task, Task, Long> implements ITaskService {
    @Autowired
    private ITaskDAO taskDAO;

    @Autowired
    private IUserTaskDAO userTaskDAO;

    @Autowired
    private ISpeciesTaskFertilizerDAO speciesTaskFertilizerDAO;

    @Autowired
    private IUserTaskService userTaskService;

    @Override
    protected GenericDAO<Task, Task, Long> getServiceDAO() {
        return taskDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Task findOrCreateTaskByName(String taskName) {
        Task task = taskDAO.findTaskByNameIgnoreCase(taskName);
        if (task == null) {
            task = new Task(taskName);
            taskDAO.insert(task);
        }
        return task;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteTask(long taskId, long userId) {
        List<UserTask> userPatternTasks = userTaskDAO.findPatternUserTasksByTaskId(taskId);

        for (UserTask patternTask : userPatternTasks) {
            userTaskService.deleteUserTask(userId, patternTask.getId());
        }

        speciesTaskFertilizerDAO.deleteByTaskId(taskId);
        int total = taskDAO.deleteTask(taskId, userId);
        if (total != 1) {
            throw new RuntimeException("Can't delete user task. taskId=" + taskId + " ownerId=" + userId);
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Task> findAvailableTaskInPatternsForMonth(int monthNum) {
        return taskDAO.findAvailableTaskInPatternsForMonth(monthNum);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Task saveOrUpdate(Task task) {
        if (task.getId() != null) {
            userTaskDAO.updateUserPatternTaskName(task.getId(), task.getName());
        } else {
            Task systemTask = taskDAO.findSystemTaskByNameIgnoreCase(task.getName());
            if(systemTask != null) {
                throw new RuntimeException("error.systemWork.exists");
            }
        }
        return super.saveOrUpdate(task);
    }
}
