package com.example.chatapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ChatFragment extends Fragment {

    private View mMainView;
    private RecyclerView mConvList;
    private String mCurrentUserID;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mConvDatabase;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mMessageDatabase;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_chat, container, false);

        mConvList = mMainView.findViewById(R.id.fragment_messages_list);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mFirebaseAuth.getCurrentUser().getUid();

        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrentUserID);
        mConvDatabase.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("Messages").child(mCurrentUserID);
        mUsersDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);

        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Users, ChatViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Users, ChatViewHolder>
                        (Users.class, R.layout.user_single_layout, ChatViewHolder.class, mUsersDatabase) {

                    @Override
                    protected void populateViewHolder(final ChatViewHolder chatViewHolder, Users users, int i) {

                        final String list_user_id = getRef(i).getKey();
                        Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);

                        lastMessageQuery.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                String lastMessage = dataSnapshot.child("Messages").getValue().toString();
                                chatViewHolder.setLastMessage(lastMessage);
                            }
                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });

                        mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final String username = dataSnapshot.child("name").getValue().toString();
                                chatViewHolder.setUsername(username);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                };
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ChatViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        //Set the username to the TextView
        public void setUsername(String username) {
            TextView mUsername = mView.findViewById(R.id.user_single_name);
            mUsername.setText(username);
        }

        //Set the username to the TextView
        public void setLastMessage(String lastMessage) {
            TextView mLastMessage = mView.findViewById(R.id.user_single_status);
            mLastMessage.setText(lastMessage);
        }
    }
}