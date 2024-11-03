package com.example.freetime.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.freetime.entities.Activity;

import java.util.List;

@Dao
public interface ActivityDao {
    @Insert
    void insertActivity(Activity activity);

    @Query("SELECT * FROM activity WHERE userId = :userId")
    List<Activity> getActivitiesByUser(int userId);

    @Query("SELECT * FROM activity WHERE days LIKE '%' || :day || '%' AND userId = :userId")
    List<Activity> getActivitiesForDay(String day, int userId);


    @Query("SELECT * FROM activity")
    List<Activity> getAllActivities();

    @Query("SELECT * FROM activity WHERE date = :date AND userId = :userId")
    List<Activity> getActivitiesForDate(String date, int userId);

    @Query("DELETE FROM activity")
    void deleteAllActivities();
}
