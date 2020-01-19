package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseRef;

    private TextView mUsername;
    private TextView mStatus;
    private Button mChangeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mUsername = findViewById(R.id.settings_display_name);
        mStatus = findViewById(R.id.settings_display_status);
        mChangeStatus = findViewById(R.id.setting_button_change_status);

        mChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeStatusPopUp();
            }
        });

        //Get the current user and the user ID
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String UID = mCurrentUser.getUid();

        //Get the database reference with the child "Users"
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
        //Enable database sync for this activity
        mDatabaseRef.keepSynced(true);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Retrieve the user values from the database
                String username = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();

                //Set the TextView to the database text
                mUsername.setText(username);
                mStatus.setText(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showChangeStatusPopUp() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Change Status");
        alert.setMessage("Enter a new status");

        // Set an EditText view to get user input
        final EditText changeStatus = new EditText(this);
        alert.setView(changeStatus);

        alert.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Set the new status to the TextView
                String newStatus = changeStatus.getText().toString();
                if (newStatus.isEmpty()) {
                    newStatus = "Using AppChat";
                }
                mStatus.setText(newStatus);

                //Update the firebase register
                mDatabaseRef.child("status").setValue(newStatus);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }
}