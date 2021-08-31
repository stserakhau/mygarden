package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.task.Task;

import java.util.List;

public interface ITaskDAO extends GenericDAO<Task, Task, Long> {

    Task findSystemTaskByNameIgnoreCase(String taskName);

    Task findTaskByNameIgnoreCase(String taskName);

    List<Task> findAvailableTaskInPatternsForMonth(int monthNum);

    int deleteTask(long taskId, long ownerId);
}
