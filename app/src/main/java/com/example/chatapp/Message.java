package com.example.chatapp;

public class Message {

    private String content;
    private String from;

    public Message() { }

    public Message(String content, String from) {
        this.content = content;
        this.from = from;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
