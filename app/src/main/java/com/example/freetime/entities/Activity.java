/*
 * Nombre del proyecto: FreeTime
 * Autores: Lucas Baruja, Leonardo Duarte, Ezequiel Arce, Iván Samudio
 * Descripción: Entidad que representa una actividad en la aplicación. Almacena datos como el nombre, descripción y otros atributos relevantes de la actividad.
 * Fecha de creación: 04/11/2024
 * Forma de utilizar: Utilizada para almacenar y gestionar la información de cada actividad en la base de datos.
 */


package com.example.freetime.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.ColumnInfo;
import java.util.List;

@Entity(tableName = "activity",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE))
public class Activity {
    @PrimaryKey(autoGenerate = true)
    public int activityId;

    public int userId;  // Foreign Key que apunta a la columna "id" de la tabla User
    public String name;

    // Representa los días de la semana para actividades recurrentes (Lunes, Martes, etc.)
    public List<String> days;

    // Almacena la fecha específica de una instancia de la actividad para el calendario
    @ColumnInfo(name = "date")
    public String date;

    public String startTime;
    public String endTime;
    public boolean isFixed;

    // Constructor actualizado para incluir tanto `days` como `date`
    public Activity(int userId, String name, List<String> days, String startTime, String endTime, boolean isFixed, String date) {
        this.userId = userId;
        this.name = name;
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isFixed = isFixed;
        this.date = date;
    }
}
