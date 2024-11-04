/*
 * Nombre del proyecto: FreeTime
 * Autores: Lucas Baruja, Leonardo Duarte, Ezequiel Arce, Iván Samudio
 * Descripción: Interfaz DAO (Data Access Object) para gestionar las operaciones de base de datos relacionadas con la entidad Activity. Incluye métodos para insertar, actualizar, eliminar y consultar actividades.
 * Fecha de creación: 04/11/2024
 * Forma de utilizar: Utilizado por el repositorio o el controlador de la aplicación para realizar operaciones CRUD en la entidad Activity.
 */

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
