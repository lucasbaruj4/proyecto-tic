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
