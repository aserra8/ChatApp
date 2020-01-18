package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Toolbar mToolbar;
    private Button mLoginBtn;
    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Set the Activity toolbar
        mToolbar = findViewById(R.id.login_activity_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Chat App");
        //Adds the return functionality to the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        //Initializes the activity elements
        mLoginEmail = findViewById(R.id.login_email);
        mLoginPassword = findViewById(R.id.login_password);
        mLoginBtn = findViewById(R.id.login_button);

        //Sets listener to the login button
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Gets the text from the TextViews
                String email = mLoginEmail.getEditText().getText().toString();
                String password = mLoginPassword.getEditText().getText().toString();

                //Checks fields are not empty
                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    loginUser(email, password);
                }
            }
        });
    }

    //Function used to login an existing user to Firebase
    private void loginUser(String email, String password) {
        //Firebase method to login an existing user
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //In case the user information is succesfull
                if (task.isSuccessful()) {
                    //Open the MainActivity
                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    //Clear all previous Activities before opening the new one
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();
                } else {
                    //Show error message for incorrect login
                    Toast.makeText(LoginActivity.this, "Login was incorrect", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}