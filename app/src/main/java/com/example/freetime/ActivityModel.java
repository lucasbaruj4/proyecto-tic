/*
 * Nombre del proyecto: FreeTime
 * Autores: Lucas Baruja, Leonardo Duarte, Ezequiel Arce, Iván Samudio
 * Descripción: Modelo de datos para representar una actividad. Contiene propiedades y métodos relacionados con una actividad específica.
 * Fecha de creación: 04/11/2024
 * Forma de utilizar: Utilizado para almacenar y manipular datos de actividades en la aplicación.
 */

package com.example.freetime;

import java.util.List;

public class ActivityModel {
    private String name;
    private List<String> days;
    private String startTime;
    private String endTime;
    private boolean isFixed;

    public ActivityModel(String name, List<String> days, String startTime, String endTime, boolean isFixed) {
        this.name = name;
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isFixed = isFixed;
    }

    public String getName() {
        return name;
    }

    public List<String> getDays() {
        return days;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public boolean isFixed() {
        return isFixed;
    }
}
