package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.domain.user.task.UserTask;
import com.i4biz.mygarden.domain.user.task.UserTaskView;

import java.util.List;

public interface IUserTaskDAO extends GenericDAO<UserTaskView, UserTask, Long> {
    List<UserTask> findUserTasksByUserWork(long ownerId, long userWorkId);

    List<UserTask> findUserTasksByUserWork(long userWorkId);

    List<UserTask> findUserTasksByUserWorkOrderedByStartDate(long userWorkId);

    List<UserTask> findPatternUserTasksByTaskId(long taskId);

    void updateNotificationSend(long userTaskId, boolean notificqationSent);

    List<UserTaskView> findTasksReadyForSendingNotification();

    List<UserTask> findUserPatterns(long ownerId);

    void updateFertilizers(long taskId, List<Item> fertilizers);

    void updateUserPatternTaskName(long taksId, String taskName);

    void updateWeatherTasksCalculationDate(long profileCityId);
}
