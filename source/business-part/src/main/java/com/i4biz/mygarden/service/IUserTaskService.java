package com.i4biz.mygarden.service;

import com.i4biz.mygarden.domain.user.task.UserTask;
import com.i4biz.mygarden.domain.user.task.UserTaskView;

import java.util.Collection;

public interface IUserTaskService extends IService<UserTaskView, UserTask, Long> {
    void deleteUserTask(long userId, long userTaskId);

    Collection<UserTask> findUserTasksByUserWork(long ownerId, long userWorkId);

    Collection<UserTask> findSystemUserTasksByUserWork(long userWorkId);
}
