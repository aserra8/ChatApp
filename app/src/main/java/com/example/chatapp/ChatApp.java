package com.example.chatapp;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class ChatApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Enable persistence for the project
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
