/*
 * Nombre del proyecto: FreeTime
 * Autores: Lucas Baruja, Leonardo Duarte, Ezequiel Arce, Iván Samudio
 * Descripción: Interfaz DAO para las operaciones de base de datos relacionadas con la entidad UserProgress. Incluye métodos para manejar el progreso de usuario en diversas actividades.
 * Fecha de creación: 04/11/2024
 * Forma de utilizar: Usado para ejecutar operaciones CRUD en la entidad UserProgress en la base de datos.
 */

package com.example.freetime.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.freetime.entities.UserProgress;

import java.util.List;

@Dao
public interface UserProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertProgress(UserProgress progress);

    // Método para actualizar un registro específico de progreso
    @Update
    void updateProgress(UserProgress progress);

    @Query("SELECT * FROM user_progress WHERE userId = :userId AND activityId = :activityId AND date = :date")
    UserProgress getProgressByUserAndActivity(int userId, int activityId, String date);

    @Query("SELECT * FROM user_progress WHERE userId = :userId")
    List<UserProgress> getProgressByUser(int userId);

    @Query("UPDATE user_progress SET startTime = :startTime, status = 'INICIADA' WHERE progressId = :progressId")
    void markActivityStarted(int progressId, String startTime);

    @Query("UPDATE user_progress SET endTime = :endTime, status = 'FINALIZADA' WHERE progressId = :progressId")
    void markActivityCompleted(int progressId, String endTime);

    @Query("SELECT * FROM user_progress")
    List<UserProgress> getAllProgressRecords();

    // Nuevos métodos:

    // Marcar actividad como no realizada
    @Query("UPDATE user_progress SET status = 'NO_REALIZADA' WHERE progressId = :progressId")
    void markActivityNotDone(int progressId);

    // Obtener actividades notificadas pero no completadas para un usuario en una fecha específica
    @Query("SELECT * FROM user_progress WHERE userId = :userId AND date = :date AND wasNotified = 1 AND status != 'FINALIZADA'")
    List<UserProgress> getNotifiedPendingActivities(int userId, String date);

    // Obtener actividades iniciadas pero no finalizadas
    @Query("SELECT * FROM user_progress WHERE userId = :userId AND status = 'INICIADA'")
    List<UserProgress> getOngoingActivities(int userId);

    // Obtener progreso diario (actividades finalizadas, no realizadas y pendientes)
    @Query("SELECT * FROM user_progress WHERE userId = :userId AND date = :date")
    List<UserProgress> getDailyProgress(int userId, String date);
}
