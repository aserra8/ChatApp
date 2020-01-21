package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private String mUserID;
    private String mUsername;
    private String mCurrentUserID;

    private Toolbar mChatToolbar;
    private EditText mMessageText;
    private TextView mUsernameText;
    private RecyclerView mMessageList;
    private ImageButton mSendMessageBtn;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mUsersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatToolbar = findViewById(R.id.chat_activity_layout);
        setSupportActionBar(mChatToolbar);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //Necessary to add custom view to bar
        actionBar.setDisplayShowCustomEnabled(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mCurrentUserID = mFirebaseAuth.getCurrentUser().getUid();

        mMessageText = findViewById(R.id.chat_activity_message);
        mSendMessageBtn = findViewById(R.id.chat_activity_send);
        mMessageList = findViewById(R.id.chat_activity_messages);

        //Get the user ID from the Intent
        mUserID = getIntent().getStringExtra("user_id");

        //Set the custom view to the ActionBar
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.chat_toolbar, null);
        actionBar.setCustomView(actionBarView);

        mUsersDatabase.child("Users").child(mUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsername = dataSnapshot.child("name").getValue().toString();
                mUsernameText = findViewById(R.id.chat_toolbar_username);
                mUsernameText.setText(mUsername);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mUsersDatabase.child("Chat").child(mCurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Create database value if it doens't exist yet
                if (!dataSnapshot.hasChild(mUserID)) {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + mCurrentUserID + "/" + mUserID, chatAddMap);
                    chatUserMap.put("Chat/" + mUserID + "/" + mCurrentUserID, chatAddMap);

                    mUsersDatabase.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Set onClickListener for send message button
        mSendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String message = mMessageText.getText().toString();

        if (!message.isEmpty()) {
            String current_user_ref = "Messages/" + mCurrentUserID + "/" + mUserID;
            String chat_user_ref = "Messages/" + mUserID + "/" + mCurrentUserID;

            DatabaseReference user_message_push = mUsersDatabase.child("Messages").
                    child(mCurrentUserID).child(mUserID).push();

            String message_id = user_message_push.getKey();

            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("message_content", message);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + message_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + message_id, messageMap);

            mUsersDatabase.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                }
            });
        }
    }
}