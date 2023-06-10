package com.example.counsellingapp;

public class User {

    private String name;
    private String id;
    private String userType;

    public User(){}

    public User(String name, String id, String userType) {
        this.name = name;
        this.id = id;
        this.userType = userType;
    }

    public User(String id, String userType) {
        this.id = id;
        this.userType = userType;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUserType() {
        return userType;
    }
}
