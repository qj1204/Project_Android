package com.xiaoxin.project_android.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class Attend implements Serializable {
    private final Integer attendId;
    private final String courseId;
    private final Timestamp startTime;
    private final Timestamp endTime;
    private final Double longitude;
    private final Double latitude;
    private final String location;
    private final String state;
    private final Integer type;
    private String gesture;

    public Attend(Integer attendId, String courseId, Timestamp startTime, Timestamp endTime, Double longitude, Double latitude, String location, String state, Integer type) {
        this.attendId = attendId;
        this.courseId = courseId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.location = location;
        this.state = state;
        this.type = type;
    }

    public String getGesture() {
        return gesture;
    }

    public void setGesture(String gesture) {
        this.gesture = gesture;
    }

    public Integer getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public Integer getAttendId() {
        return attendId;
    }

    public String getCourseId() {
        return courseId;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public String getState() {
        return state;
    }
}
