/*
 * Nombre del proyecto: FreeTime
 * Autores: Lucas Baruja, Leonardo Duarte, Ezequiel Arce, Iván Samudio
 * Descripción: Configura y maneja la base de datos de la aplicación, incluyendo la inicialización y las interacciones con la base de datos.
 * Fecha de creación: 04/11/2024
 * Forma de utilizar: Centraliza el acceso a la base de datos, creando instancias de DAOs para consultas.
 */

package com.example.freetime;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.freetime.dao.ActivityDao;
import com.example.freetime.dao.UserDao;
import com.example.freetime.dao.UserProgressDao;
import com.example.freetime.entities.Activity;
import com.example.freetime.entities.User;
import com.example.freetime.entities.UserProgress;

@Database(entities = {User.class, Activity.class, UserProgress.class}, version = 4, exportSchema = false)
@TypeConverters({Converters.class})  // Añadimos el TypeConverter aquí
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract ActivityDao activityDao();
    public abstract UserDao userDao();
    public abstract UserProgressDao userProgressDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "freetime_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
