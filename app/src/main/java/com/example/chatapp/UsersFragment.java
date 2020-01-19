package com.example.chatapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UsersFragment extends Fragment {

    private View mMainView;
    private String mCurrentUserID;
    private RecyclerView mUsersList;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mUsersDatabase;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_users, container, false);

        mUsersList = mMainView.findViewById(R.id.users_list);
        mFirebaseAuth = FirebaseAuth.getInstance();

        mCurrentUserID = mFirebaseAuth.getCurrentUser().getUid();
        //Retrieve all the users from the database
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        //Enable offline function
        mUsersDatabase.keepSynced(true);

        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Users, UsersViewHolder>
                        (Users.class, R.layout.user_single_layout, UsersViewHolder.class, mUsersDatabase) {
                    @Override
                    //Method to assign values to the RecycleView items
                    protected void populateViewHolder(UsersViewHolder usersViewHolder, Users users, int i) {
                        usersViewHolder.setUsername(users.getName());
                        usersViewHolder.setStatus(users.getStatus());
                        final String listUserID = getRef(i).getKey();

                        usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[]  = new CharSequence[]{"Send message"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("user_id", listUserID);
                                            startActivity(chatIntent);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }
                };

        //Set the Adapter to the RecyclerView
        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        //Necessary to set the OnClickListener of the RecycleViewItems
        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        //Set the username to the TextView
        public void setUsername(String username) {
            TextView mUsername = mView.findViewById(R.id.user_single_name);
            mUsername.setText(username);
        }

        //Set the user status to the TextView
        public void setStatus(String status) {
            TextView mUserStatus = mView.findViewById(R.id.user_single_status);
            mUserStatus.setText(status);
        }
    }
}