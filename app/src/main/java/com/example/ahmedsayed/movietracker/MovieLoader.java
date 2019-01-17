package com.example.ahmedsayed.movietracker;

import android.content.Context;
import java.util.List;


import android.support.v4.content.AsyncTaskLoader;

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {
    private String urls;

    public MovieLoader(Context context, String urls) {
        super(context);
        this.urls = urls;
    }


    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        // Don't perform the request if there are no URLs, or the first URL is null.
        if (urls == null) {
            return null;
        }
        List<Movie> movies_result = QueryUtils.fetchMoviesData(urls);
        return movies_result;
    }
}
