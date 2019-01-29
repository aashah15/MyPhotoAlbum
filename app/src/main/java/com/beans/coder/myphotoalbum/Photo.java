package com.beans.coder.myphotoalbum;

import com.google.firebase.database.Exclude;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Photo {
    private String id;
    private String name;
    private String date;
    private String place;
    private String event;
    private String imageUri;
    private String mKey;
    private DateFormat f = new SimpleDateFormat("dd/mm/yyyy");
    public Photo(){

    }
    public Photo(String id, String name, String date, String place, String event, String imageUri) {
        if(name.trim().equals("")){
            name = "No Name";
        }
        this.id = id;
        this.name = name;
        this.date = date;
        this.place = place;
        this.event = event;
        this.imageUri = imageUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    @Exclude
    public String getKey() {
        return mKey;
    }
    @Exclude
    public void setKey(String key) {
        mKey = key;
    }
}
