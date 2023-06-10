package com.example.counsellingapp;

public class Message {

    private String message;
    private String senderID;

    public Message() {}

    public Message(String message, String senderID) {
        this.message = message;
        this. senderID = senderID;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderID() {
        return senderID;
    }
}
