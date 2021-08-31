package com.i4biz.mygarden.service;


import com.i4biz.mygarden.domain.task.Task;

import java.util.List;

public interface ITaskService extends IService<Task, Task, Long> {
    Task findOrCreateTaskByName(String taskName);

    void deleteTask(long taskId, long userId);

    List<Task> findAvailableTaskInPatternsForMonth(int monthNum);
}
