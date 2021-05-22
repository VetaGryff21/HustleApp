package com.example.myapp;

public class DancerUnit {
    private int code;
    private String fullname;
    private String rating;

    public int getCode() {
        return code;
    }

    public String getFullname() {
        return fullname;
    }

    public String getRating() {
        return rating;
    }

    public DancerUnit(int code, String fullname, String rating) {
        this.code = code;
        this.fullname = fullname;
        this.rating = rating;
    }
}
