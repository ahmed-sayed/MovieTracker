package com.example.ahmedsayed.movietracker;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TrailerLinkLoader extends AsyncTaskLoader<List<String>> {
    private List<Integer> movies_IDs_list;

    public TrailerLinkLoader(Context context, List<Integer> movies_IDs) {
        super(context);
        this.movies_IDs_list = movies_IDs;
    }


    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<String> loadInBackground() {
        // Don't perform the request if there are no items in movies_IDs_list
        if (movies_IDs_list.size() == 0) {
            return null;
        }
        int size = movies_IDs_list.size();


        ArrayList<String> movies_Trailers  = new ArrayList<String>();

        for (int i = 0; i < size; i++) {
            String trailer_Key = QueryUtils.fetchTrailerLink(movies_IDs_list.get(i));
            if (trailer_Key.isEmpty() )
            {
                movies_Trailers.add("-1"); // -1 means that there is no Trailer link for this movie
            }
            else {
                String trailer_Full_Link = "https://www.youtube.com/watch?v=" + trailer_Key;
                movies_Trailers.add(trailer_Full_Link);
            }
        }
        // return a list that contains "trailer Link" for every movie
        return movies_Trailers;
    }
}
