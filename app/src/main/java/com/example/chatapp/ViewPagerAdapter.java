package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

//Class used as a ViewPager Adapter extending from FragmentPagerAdapter
public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    //Method that returns the selected fragment
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                //Initialize and return a request tab fragment
                return new RequestFragment();
            case 1:
                //Initialize and return a chat tab fragment
                return new ChatFragment();
                //Initialize and return a friend tab fragment
            case 2:
                return new FriendFragment();
            default:
                return null;
        }
    }

    @Override
    //Returns 3 since the App will have 3 tabs
    public int getCount() {
        return 3;
    }

    //Overriden method used to set the page title
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                //Initialize and return a request tab fragment
                return "REQUESTS";
            case 1:
                //Initialize and return a chat tab fragment
                return "CHATS";
            //Initialize and return a friend tab fragment
            case 2:
                return "FRIENDS";
            default:
                return null;
        }
    }
}
