package com.example.ahmedsayed.movietracker;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import android.widget.ListView;

import java.util.ArrayList;

import android.content.Loader;

import android.support.v4.app.LoaderManager;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopRatedFragment extends Fragment {


    public TopRatedFragment() {
        // Required empty public constructor
    }

    private static final String TOP_RATED_REQUEST_URL = HelperMethods.BASE_REQUEST_URL + "/movie/top_rated" + HelperMethods.API_KEY;

    /* When we get to the onPostExecute() method, we need to update the ListView.
           The only way to update the contents of the list is to update the data set within the EarthquakeAdapter.
           To access and modify the instance of the EarthquakeAdapter,
           we need to make it a global variable in the TopRated Fragment.*/


    private MovieAdapter mAdapter;
    private TextView emptyTextView;
    private ProgressBar loadingIndicator;
    private ConnectivityManager connectivityManager;

    private static final int List_item_LOADER_ID = 31;
    private static final int Trailer_Link_LOADER_ID = 32;
    private LoaderManager loaderManager;
    private ArrayList<String> trailers_List = new ArrayList<String>();
    private ArrayList<Integer> movies_IDs_list = new ArrayList<Integer>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.movies_list, container, false);


        loadingIndicator = (ProgressBar) rootView.findViewById(R.id.loading_spinner);
        emptyTextView = (TextView) rootView.findViewById(R.id.emptytextView);


        // check the network state
        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // if the network status is active then it will intialize the loader as usual and fetch data
        // else , it will show " No Network Connection " message

        if (HelperMethods.Check_network_status(connectivityManager)) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            loaderManager = getActivity().getSupportLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above
            // pass in null for the bundle.
            // Pass in "this" activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(List_item_LOADER_ID, null, List_Item_loaderCallbacks);

        } else {
            loadingIndicator.setVisibility(View.GONE);
            emptyTextView.setText("No Network Access ");
        }


        // Find a reference to the {@link ListView} in the layout
        ListView moviesListView = (ListView) rootView.findViewById(R.id.list);
        moviesListView.setEmptyView(emptyTextView);


        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());


        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        moviesListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website dislaying the  movie trailer or videos have been added to the selected  movie.

        moviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int index, long l) {

                String movie_name = mAdapter.getItem(index).getTitle();
                String trailer_Link = trailers_List.get(index);
                if (trailer_Link != "-1") //// -1 means that there is no Trailer link for this movie
                {
                    Toast.makeText(getActivity(), "Play Trailer Video for " + movie_name, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer_Link)));
                } else
                    Toast.makeText(getActivity(), "there is no Trailer link for this movie " + movie_name, Toast.LENGTH_SHORT).show();

            }
        });
        return rootView;
    }


    private android.support.v4.app.LoaderManager.LoaderCallbacks<List<Movie>> List_Item_loaderCallbacks
            = new android.support.v4.app.LoaderManager.LoaderCallbacks<List<Movie>>() {


        /*
              We need onCreateLoader(),
              for when the LoaderManager has determined that the loader with our specified ID isn't running,
              so we should create a new one.
              */
        @Override
        public android.support.v4.content.Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {

            return new MovieLoader(getActivity(), TOP_RATED_REQUEST_URL);
        }

        /*
         * We need onLoadFinished(), where we'll do exactly what we did in onPostExecute(),
         * and use the Movies data to update our UI - by updating the dataset in the adapter.
         * */
        @Override
        public void onLoadFinished(android.support.v4.content.Loader<List<Movie>> loader, List<Movie> data) {
            loadingIndicator.setVisibility(View.GONE);
            emptyTextView.setText("Empty Movies List ");

            // Clear the adapter of previous Movies data
            mAdapter.clear();
            trailers_List.clear();
            movies_IDs_list.clear();

            // If there is a valid list of {@link Movie}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {

                int size = data.size();
                for (int i = 0; i < size; i++) {
                    movies_IDs_list.add(data.get(i).getMovie_id());
                }
                mAdapter.addAll(data);

                // call the "TrailerLinkLoader" to get trailers Links for Retrieved movies
                loaderManager.initLoader(Trailer_Link_LOADER_ID, null, Trailer_loaderCallbacks);
            }
        }


    /*
         And we need onLoaderReset(), we're we're being informed that the data from our loader
         is no longer valid.
         This isn't actually a case that's going to come up with our simple loader,
         but the correct thing to do is to remove all the Movies data
         from our UI by clearing out the adapter’s data set.*/

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<List<Movie>> loader) {
            mAdapter.clear();
            movies_IDs_list.clear();
            trailers_List.clear();
        }
    };


    private android.support.v4.app.LoaderManager.LoaderCallbacks<List<String>> Trailer_loaderCallbacks
            = new android.support.v4.app.LoaderManager.LoaderCallbacks<List<String>>() {


        @Override
        public android.support.v4.content.Loader<List<String>> onCreateLoader(int i, Bundle bundle) {

            return new TrailerLinkLoader(getActivity(), movies_IDs_list);
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<List<String>> loader, List<String> data) {


            // If there is a valid list of  Movies trailers , then add them to the trailers_List
            if (data != null && !data.isEmpty()) {

                trailers_List.addAll(data);
            }
        }


        @Override
        public void onLoaderReset(android.support.v4.content.Loader<List<String>> loader) {
            //movies_IDs_list.clear();
            //trailers_List.clear();

        }
    };
}
