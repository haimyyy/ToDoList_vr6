package com.shenkar.tamar.todolist_vr6;

import java.io.Serializable;

/**
 * Created by tamar on 3/20/15.
 */
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    public long id;
    public String taskTitle;
    public String taskDescription;
    public String taskDateReminder;
    public String taskHourReminder;
    private Boolean hasLocation = false;
    private MapPoint location;
    public boolean isDeleted;
    public int isDone;


    public Task() {
        super();
    }

    public Task(long idInDB, String title, String description, String dateReminder, String hourReminder, int isDone) {
        super();
        this.id = idInDB;
        this.taskTitle = title;
        this.taskDescription = description;
        this.taskDateReminder = dateReminder;
        this.taskHourReminder = hourReminder;

        this.isDone = isDone;
    }

    //geters and seters
    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public void setTaskDateReminder(String taskDate) {
        this.taskDateReminder = taskDate;
    }

    public void setTaskHourReminder(String taskHour) {
        this.taskHourReminder = taskHour;
    }

    public void setTaskIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setTaskIsDone(int isDone) {
        this.isDone = isDone;
    }

    public String getTaskTitle() {
        return this.taskTitle;
    }

    public String getTaskDescription() {
        return this.taskDescription;
    }

    public String getTaskDateReminder() {
        return this.taskDateReminder;
    }

    public String getTaskHourReminder() {
        return this.taskHourReminder;
    }

    public boolean getTaskIsDeleted() {
        return this.isDeleted;
    }

    public int getTaskIsDone() {
        return this.isDone;
    }

    public Boolean getHasLocation() {
        return hasLocation;
    }
    public void setHasLocation(Boolean hasLocation) {
        this.hasLocation = hasLocation;
    }

    public MapPoint getLocation() {
        return location;
    }

    public void setLocation(MapPoint location) {
        this.location = location;
    }

    public void setLocation(Double lat,Double lng) {
        if(lat==null || lng==null || lat==0.0 || lng==0.0)
        {
            setHasLocation(false);
            return;
        }
        this.location = new MapPoint(lat, lng);
        setHasLocation(true);
    }

}
