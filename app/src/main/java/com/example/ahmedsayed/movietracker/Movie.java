package com.example.ahmedsayed.movietracker;

import android.media.Image;

public class Movie {

    int movie_id;
    boolean has_video;
    private String poster_path, title, release_date, overview ;
    StringBuilder genres_names;
    double vote_average;

    public Movie(int movie_id, String poster_path, String title, String release_date, String overview, double vote_average, boolean has_video,StringBuilder genres_names) {
        this.movie_id = movie_id;
        this.poster_path = poster_path;
        this.title = title;
        this.release_date = release_date;
        this.overview = overview;
        this.vote_average = vote_average;
        this.has_video = has_video;
        this.genres_names=genres_names;
    }

    public StringBuilder getGenres_names() {
        return genres_names;
    }

    public void setGenres_names(StringBuilder genres_names) {
        this.genres_names = genres_names;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }

    public boolean isHas_video() {
        return has_video;
    }

    public void setHas_video(boolean has_video) {
        this.has_video = has_video;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }
}
