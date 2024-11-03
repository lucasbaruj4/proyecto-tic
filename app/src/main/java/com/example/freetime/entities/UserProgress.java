package com.example.freetime.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

@Entity(tableName = "user_progress", foreignKeys = @ForeignKey(entity = User.class,
        parentColumns = "id",
        childColumns = "userId",
        onDelete = ForeignKey.CASCADE))
public class UserProgress {
    @PrimaryKey(autoGenerate = true)
    public int progressId;

    public int userId;
    public int activityId;
    public String date;
    public boolean completedOnTime;
    public boolean wasNotified;

    // Nuevos campos para el tracking detallado
    public String startTime;  // Hora en que se inició la actividad
    public String endTime;    // Hora en que se finalizó la actividad
    public String status;     // Estado de la actividad: PENDIENTE, INICIADA, NO_REALIZADA, FINALIZADA

    // Constructor actualizado con valores predeterminados
    public UserProgress(int userId, int activityId, String date, boolean completedOnTime, boolean wasNotified,
                        String startTime, String endTime, String status) {
        this.userId = userId;
        this.activityId = activityId;
        this.date = date;
        this.completedOnTime = completedOnTime;
        this.wasNotified = wasNotified;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }
}
