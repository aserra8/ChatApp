package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        //Set the activity toolbar
        mToolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Chat App");

        //Set the app fragments and ViewPager adapter
        mViewPager = findViewById(R.id.main_activity_view_pager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);

        //Set up the ViewPager with the toolbar
        mTabLayout = findViewById(R.id.main_activity_tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
        //Set the tab text color to white
        mTabLayout.setTabTextColors(-5, -5);
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //Start "StartActivity" if user is not logged in
        if (currentUser == null) {
            sendStartActivity();
        }
    }

    //Function used to start new Activity
    private void sendStartActivity() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    //Sets the Activity menu
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    //Listener for the option items
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        //If item selected equals the logout button
        if (item.getItemId() == R.id.main_logout_btn) {
            // Log out current user from the application
            FirebaseAuth.getInstance().signOut();
            sendStartActivity();
        }

        return true;
    }
}
