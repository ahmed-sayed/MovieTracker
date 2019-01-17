package com.example.ahmedsayed.movietracker;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {


    private Context mContext;

    public SimpleFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public int getCount() {
        return 4;
    }

    /*
    Upcoming
 Latest
 Now Playing
 Top Rated
 Popular

*/
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.category_Upcoming);
        } else if (position == 1) {
            return mContext.getString(R.string.category_NowPlaying);
        } else if (position == 2) {
            return mContext.getString(R.string.category_TopRated);
        } else
            return mContext.getString(R.string.category_Popular);


    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        if (position == 0) {
            return new UpComingFragment();
        }  else if (position == 1) {
            return new NowPlayingFragment();
        } else if (position == 2) {
            return new TopRatedFragment();
        } else
            return new PopularFragment();
    }
}