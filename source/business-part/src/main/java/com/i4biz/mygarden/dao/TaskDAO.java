package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.task.Task;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository()
public class TaskDAO extends GenericDAOImpl<Task, Task, Long> implements ITaskDAO {

    @Override
    public Task findSystemTaskByNameIgnoreCase(String taskName) {
        return (Task) getSession()
                .createQuery("from Task where lower(name)=:n and ownerId is null")
                .setString("n", taskName.toLowerCase())
                .uniqueResult();
    }

    @Override
    public Task findTaskByNameIgnoreCase(String taskName) {
        return (Task) getSession()
                .createQuery("from Task where lower(name)=:n")
                .setString("n", taskName.toLowerCase())
                .uniqueResult();
    }

    @Override
    public List<Task> findAvailableTaskInPatternsForMonth(int monthNum) {
        return getSession()
                .createSQLQuery("select " +
                        "    distinct" +
                        "    t.* " +
                        "from user_task ut" +
                        "  inner join user_work uw on uw.id=ut.user_work_id" +
                        "  inner join task t on t.id=ut.task_id " +
                        "where " +
                        "  ut.owner_id is null" +
                        "  and uw.pattern=true" +
                        "  and uw.user_id is null" +
                        "  and :monthNum between extract(month from ut.start_date) and extract(month from ut.end_date) " +
                        "order by t.name ")
                .addEntity("t", Task.class)
                .setInteger("monthNum", monthNum)
                .list();
    }

    @Override
    public int deleteTask(long taskId, long ownerId) {
        return getSession()
                .createQuery("delete from Task where id=:id and ownerId=:oid")
                .setLong("id", taskId)
                .setLong("oid", ownerId)
                .executeUpdate();
    }
}
