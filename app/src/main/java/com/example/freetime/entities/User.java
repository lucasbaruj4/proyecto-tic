package com.example.freetime.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "start_hour")
    public int startHour;

    @ColumnInfo(name = "start_minute")
    public int startMinute;

    @ColumnInfo(name = "end_hour")
    public int endHour;

    @ColumnInfo(name = "end_minute")
    public int endMinute;

    @ColumnInfo(name = "password")
    public String password;

    // Constructor que inicializa todos los campos
    public User(String name, String email, String password,
                int startHour, int startMinute, int endHour, int endMinute) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    // Método getter para el nombre
    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }


}
