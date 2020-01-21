package com.example.chatapp;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ChatApp extends Application {

    private FirebaseAuth mFirebaseAuth;

    @Override
    public void onCreate() {
        super.onCreate();
        //Enable persistence for the project
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mFirebaseAuth = FirebaseAuth.getInstance();
    }
}
