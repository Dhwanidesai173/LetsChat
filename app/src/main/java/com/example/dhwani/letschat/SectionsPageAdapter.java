package com.example.dhwani.letschat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class SectionsPageAdapter extends FragmentPagerAdapter {
    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;

            case 1:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;

            case 2:
                FriendListFragment friendListFragment = new FriendListFragment();
                return friendListFragment;

                default:
                    return null;
        }
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
            return "Requests";

            case 1:
                return "Chat";

            case 2:
                return "Friend List";

            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return 3;
    }
}
