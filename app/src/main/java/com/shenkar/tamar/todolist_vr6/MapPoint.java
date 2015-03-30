package com.shenkar.tamar.todolist_vr6;

import java.io.Serializable;

public class MapPoint implements Serializable{

    private double lat,lng;

    public MapPoint(double lat,double lng) {
        // TODO Auto-generated constructor stub
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }



}