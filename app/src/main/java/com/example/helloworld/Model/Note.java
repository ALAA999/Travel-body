package com.example.helloworld.Model;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "note")
public class Note {
    @PrimaryKey(autoGenerate = true)
    int id;
    String title;
    double lat;
    double lng;
    String time;
    String description;

    @Ignore
    public Note(String title, double lat, double lng, String time, String description) {
        this.title = title;
        this.lat = lat;
        this.lng = lng;
        this.time = time;
        this.description = description;
    }

    public Note(int id, String title, double lat, double lng, String time, String description) {
        this.id = id;
        this.title = title;
        this.lat = lat;
        this.lng = lng;
        this.time = time;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}