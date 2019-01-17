package com.example.ahmedsayed.movietracker;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter(Context context, List<Movie> movieList) {
        super(context, 0, movieList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        //get the "context" to use in picasso library
        Context context = parent.getContext();

        // Check if an existing view is being reused, otherwise inflate the view
        //inflate mean create new view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_view, parent, false);
        }

        Movie current_Movie = getItem(position);

        String poster_path = current_Movie.getPoster_path();
        String title = current_Movie.getTitle();
        String release_date = current_Movie.getRelease_date();
        String overview = current_Movie.getOverview();
        StringBuilder genres_names = current_Movie.getGenres_names();
        double vote_avg = current_Movie.getVote_average();



        // set the poster image
        ImageView poster_imageView = (ImageView) listItemView.findViewById(R.id.section_2_1_Movie_Poster);
        if (poster_path != null) {
            String ImageURL = "https://image.tmdb.org/t/p/w200" + poster_path;
            Picasso.with(context)
                    .load(ImageURL)
                    .into((ImageView) poster_imageView);
        }


        TextView title_txtView = (TextView) listItemView.findViewById(R.id.section_1_2_Movie_Name);
        title_txtView.setText(title);

        TextView release_date_txtView = (TextView) listItemView.findViewById(R.id.section_1_3_Production_Year);
        release_date_txtView.setText(release_date);

        TextView overview_txtView = (TextView) listItemView.findViewById(R.id.section_2_2_Movie_OverView);
        overview_txtView.setText(overview);

        TextView rating_txtView = (TextView) listItemView.findViewById(R.id.section_1_3_Rate_Number);
        rating_txtView.setText(Double.toString(vote_avg));

        TextView category_txtView= (TextView)listItemView.findViewById(R.id.section_1_3_Movie_Category);
        category_txtView.setText(genres_names);

        return listItemView;
    }
}
