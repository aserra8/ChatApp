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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Toolbar mToolbar;
    private Button mCreateAccBtn;
    private TextInputLayout mEmail;
    private TextInputLayout mUsername;
    private TextInputLayout mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Set the activity Toolbar
        mToolbar = findViewById(R.id.register_activity_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Chat App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        //Initializes the activity elements
        mEmail = findViewById(R.id.reg_email);
        mUsername = findViewById(R.id.reg_username);
        mPassword = findViewById(R.id.reg_password);
        mCreateAccBtn = findViewById(R.id.reg_create_acc_btn);

        //Sets button listener for creating a new account
        mCreateAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Gets the text from the TextViews
                String email = mEmail.getEditText().getText().toString();
                String username = mUsername.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                //Checks fields are not empty
                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(username) || !TextUtils.isEmpty(password)) {
                    register_user(email, username, password);
                }
            }
        });
    }

    //Function used to register a new user to Firebase
    private void register_user(String email, final String username, String password) {
        //Firebase method to create a new user provided the email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //In case the task is succesfull
                        if (task.isSuccessful()) {
                            //Get the current user Firebase UID
                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String UID = current_user.getUid();

                            //Get the dataase reference and assign its childs
                            mDatabase = FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(UID);

                            //Initialize a HashMap with the user values for the databse
                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name", username);
                            userMap.put("status", "Using ChatApp");
                            userMap.put("image", "default");
                            userMap.put("thumb_image", "default");

                            //Send the values to the Firebase Database
                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //Open the MainActivity
                                        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                        //Clear all previous Activities before opening the new one
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Something went wrong. Try again.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}