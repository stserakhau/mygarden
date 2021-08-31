package com.i4biz.mygarden.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.domain.user.task.UserTask;
import com.i4biz.mygarden.domain.user.task.UserTaskView;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository()
public class UserTaskDAO extends GenericDAOImpl<UserTaskView, UserTask, Long> implements IUserTaskDAO {
    @Override
    public List<UserTask> findUserTasksByUserWork(long ownerId, long userWorkId) {
        return getSession()
                .createQuery("from UserTask where ownerId=:oid and userWorkId=:uwid order by startDate asc")
                .setLong("oid", ownerId)
                .setLong("uwid", userWorkId)
                .list();
    }

    @Override
    public List<UserTask> findUserTasksByUserWork(long userWorkId) {
        return getSession()
                .createQuery("from UserTask where userWorkId=:uwid order by startDate asc")
                .setLong("uwid", userWorkId)
                .list();
    }

    @Override
    public List<UserTask> findUserTasksByUserWorkOrderedByStartDate(long userWorkId) {
        return getSession()
                .createQuery("from UserTask where userWorkId=:uwid order by startDate asc")
                .setLong("uwid", userWorkId)
                .list();
    }

    @Override
    public List<UserTask> findPatternUserTasksByTaskId(long taskId) {
        return getSession()
                .createQuery("select ut from UserTask ut " +
                        "inner join UserWork uw on uw.id=ut.userWorkId " +
                        "where uw.pattern=true and ut.taskId=:tid")
                .setLong("tid", taskId)
                .list();
    }

    @Override
    public void updateNotificationSend(long userTaskId, boolean notificqationSent) {
        getSession()
                .createQuery("update UserTask set notificationSent=:val where id=:id")
                .setBoolean("val", notificqationSent)
                .setLong("id", userTaskId)
                .executeUpdate();
    }

    @Override
    public List<UserTaskView> findTasksReadyForSendingNotification() {
        return getSession().createSQLQuery("SELECT utw.* FROM user_task_view utw " +
                "INNER JOIN user_profile up ON up.user_id=utw.owner_id " +
                "WHERE " +
                "    utw.notification_ready = TRUE" +
                "    AND utw.notification_sent IS NULL" +
                "    AND up.job_notification_days_before IS NOT NULL" +
                "    AND utw.pattern=FALSE" +
                "    AND utw.calculated_date < now() + INTERVAL '1 day' * up.job_notification_days_before " +
                "ORDER BY owner_id, calculated_date")
                .addEntity(UserTaskView.class)
                .setMaxResults(100)
                .list();
    }

    @Override
    public List<UserTask> findUserPatterns(long ownerId) {
        return getSession()
                .createQuery("select ut from UserTask ut " +
                        "inner join UserWork uw on uw.id=ut.userWorkId " +
                        "where uw.pattern=true and ut.ownerId=:oid")
                .setLong("oid", ownerId)
                .list();
    }

    @Override
    public void updateFertilizers(long taskId, List<Item> fertilizers) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(fertilizers);
            getSession()
                    .createSQLQuery("update user_task set fertilizers=cast(:val as json) where id=:id")
                    .setLong("id", taskId)
                    .setString("val", content)
                    .executeUpdate();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateUserPatternTaskName(long taskId, String taskName) {
        getSession()
                .createSQLQuery("update user_task as ut set task_name=:name from user_work as uw " +
                        "where ut.task_id=:tid and uw.id=ut.user_work_id and uw.pattern=true")
                .setLong("tid", taskId)
                .setString("name", taskName)
                .executeUpdate();
    }

    @Override
    public void updateWeatherTasksCalculationDate(long profileCityId) {
        getSession()
                .createSQLQuery("WITH" +
                        "  cityTempSumPrev27 AS (" +
                        "    SELECT" +
                        "      sum(temperature) AS temp27" +
                        "    FROM (" +
                        "      SELECT city_id, temperature" +
                        "      FROM weather_log" +
                        "      WHERE city_id=:cid" +
                        "      ORDER BY updated DESC" +
                        "      LIMIT 27" +
                        "    ) x" +
                        "  )," +
                        "  dataForProcessing AS (" +
                        "    SELECT" +
                        "      ut.id AS user_task_id," +
                        "      CASE" +
                        "        WHEN up.job_notification_days_before=1 THEN ((SELECT temp27 FROM cityTempSumPrev27)+w.day1)/28" +
                        "        WHEN up.job_notification_days_before=2 THEN ((SELECT temp27 FROM cityTempSumPrev27)+w.day1+w.day2)/29" +
                        "        WHEN up.job_notification_days_before=3 THEN ((SELECT temp27 FROM cityTempSumPrev27)+w.day1+w.day2+w.day3)/30" +
                        "      END AS avgTemp," +
                        "      up.job_notification_days_before," +
                        "      ut.avg_temperature," +
                        "      up.user_id," +
                        "      up.city_id," +
                        "      ut.start_date," +
                        "      ut.calculated_date," +
                        "      ut.end_date," +
                        "      ut.notification_ready" +
                        "    FROM user_task ut" +
                        "      INNER JOIN user_work uw" +
                        "          INNER JOIN user_profile up" +
                        "              INNER JOIN weather w ON w.net_city_id=up.city_id AND w.city_id = :cid" +
                        "          ON up.user_id = uw.user_id" +
                        "      ON ut.user_work_id = uw.id AND uw.pattern = FALSE" +
                        "    WHERE" +
                        "      ut.planing_type='planByWeather'" +
                        "      AND now() + INTERVAL '1 days' * up.job_notification_days_before >= ut.start_date" +
                        "      AND now() < ut.end_date" +
                        "      AND ut.notification_ready = FALSE" +
                        "  )" +
                        "  UPDATE user_task ut" +
                        "  SET" +
                        "      calculated_date = now() + INTERVAL '1 days' * dfp.job_notification_days_before," +
                        "      notification_ready = TRUE" +
                        "  FROM dataForProcessing dfp" +
                        "  WHERE " +
                        "      ut.id = dfp.user_task_id" +
                        "      and abs(dfp.avgtemp) > abs(dfp.avg_temperature)")
                .setLong("cid", profileCityId)
                .executeUpdate();
    }
}
