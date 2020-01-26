package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private String mUserID;
    private String mUsername;
    private String mCurrentUserID;
    private final List<Message> messagesList = new ArrayList<>();

    private Toolbar mChatToolbar;
    private EditText mMessageText;
    private TextView mUsernameText;
    private MessageAdapter mAdapter;
    private ImageButton mSendMessageBtn;
    private RecyclerView mMessagesList;
    private LinearLayoutManager mLayoutManager;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mUsersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Initialize the toolbar
        mChatToolbar = findViewById(R.id.chat_activity_layout);
        setSupportActionBar(mChatToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //Necessary to add custom view to bar
        actionBar.setDisplayShowCustomEnabled(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mFirebaseAuth.getCurrentUser().getUid();

        //Get the user ID from the Intent
        mUserID = getIntent().getStringExtra("user_id");

        //Set the custom view to the ActionBar
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.chat_toolbar, null);
        actionBar.setCustomView(actionBarView);

        mMessageText = findViewById(R.id.chat_activity_message);
        mSendMessageBtn = findViewById(R.id.chat_activity_send);

        mAdapter = new MessageAdapter(messagesList);

        mMessagesList = findViewById(R.id.chat_activity_messages);
        mLayoutManager = new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLayoutManager);
        mMessagesList.setAdapter(mAdapter);

        loadMessages();

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

    //Function to retrieve messages from database and show them through adapter
    private void loadMessages() {
        mUsersDatabase.child("Messages").child(mCurrentUserID).child(mUserID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Retrieve the message from the database
                Message message = dataSnapshot.getValue(Message.class);

                //Add the message to the list and refresh adapter
                messagesList.add(message);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        String content = mMessageText.getText().toString();

        if (!content.isEmpty()) {
            String current_user_ref = "Messages/" + mCurrentUserID + "/" + mUserID;
            String chat_user_ref = "Messages/" + mUserID + "/" + mCurrentUserID;

            DatabaseReference user_message_push = mUsersDatabase.child("Messages").
                    child(mCurrentUserID).child(mUserID).push();

            String message_id = user_message_push.getKey();

            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("content", content);
            messageMap.put("from", mCurrentUserID);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + message_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + message_id, messageMap);

            //Set the writing bar to blank
            mMessageText.setText("");

            mUsersDatabase.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                }
            });
        }
    }
}