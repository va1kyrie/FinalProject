package hu.ait.android.helen.finalproject.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import hu.ait.android.helen.finalproject.fragments.MainPage;
import hu.ait.android.helen.finalproject.fragments.SettingsPage;


/**
 * Created by Helen on 5/11/2015.
 */
public class FragAdapter extends FragmentPagerAdapter {
    public FragAdapter(FragmentManager fm){
        super(fm);
    }//FragAdapter

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                return new SettingsPage();
            case 1:
                return new MainPage();
            default:
                return new MainPage();
        }//switch
    }//getItem

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "Settings";
            case 1:
                return "Summary";
            default:
                return "Summary";
        }//switch
    }//getPageTitle

    @Override
    public int getCount() {
        return 2;
    }
}//class FragAdapter
