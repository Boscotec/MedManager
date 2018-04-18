package com.boscotec.medmanager.model;

import com.boscotec.medmanager.interfaces.RecyclerItem;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Johnbosco on 24-Mar-18.
 */

public class User implements Serializable {
    private long id;
    private String email, name, address, password, gender, thumbnail;
    private int phone;

    public User(){}

    //getter
    public long getId() {
        return id;
    }
    public String getEmail() { return email; }
    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }
    public int getPhone() {
        return phone;
    }
    public String getGender() {
        return gender;
    }
    public String getPassword() {
        return password;
    }
    public String getThumbnail() { return thumbnail; }

    //setter
    public void setId(long id) {
        this.id = id;
    }
    public void setEmail(String email) {this.email = email;}
    public void setName(String name) {this.name = name;}
    public void setAddress(String address) {this.address = address;}
    public void setPhone(int phone) {this.phone = phone;}
    public void setGender(String gender) {this.gender = gender;}
    public void setPassword(String password) {this.password = password;}
    public void setThumbnail(String thumbnail) {this.thumbnail = thumbnail;}

}
