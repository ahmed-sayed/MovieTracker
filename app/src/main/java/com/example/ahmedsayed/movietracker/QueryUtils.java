package com.example.ahmedsayed.movietracker;

import android.media.Image;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {


    private QueryUtils() {

        /**
         * Create a private constructor because no one should ever create a {@link QueryUtils} object.
         * This class is only meant to hold static variables and methods, which can be accessed
         * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
         */
    }

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static URL CreateUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }


    public static String MakeHttpRequest(URL url) throws IOException {
        String Jsonresponse = "";

        if (url == null) {
            return Jsonresponse;
        }
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000 /* milliseconds */);
            httpURLConnection.setConnectTimeout(15000 /* milliseconds */);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
                Jsonresponse = ReadFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + httpURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Movies JSON results.", e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return Jsonresponse;
    }

    private static String ReadFromStream(InputStream inputStream) throws IOException {

        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }

        }
        return output.toString();
    }

    public static String fetchTrailerLink(int movie_ID) {


        StringBuilder requestUrl = new StringBuilder("https://api.themoviedb.org/3/movie/"
                + Integer.toString(movie_ID)
                + "/videos" + HelperMethods.API_KEY);

        // retrieve information about movie trailer link
        // Create Url Object
        URL url = CreateUrl(requestUrl.toString());

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = MakeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        String trailer_Key = extract_Trailer_Info_FromJson(jsonResponse);

        return trailer_Key;

    }

    public static ArrayList<Movie> fetchMoviesData(String requestUrl) {


        // Create Url Object
        URL url = CreateUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = MakeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        // Extract relevant fields from the JSON response and create an movies list
        ArrayList<Movie> moviesList = extractFeatureFromJson(jsonResponse);

        return moviesList;
    }

    /**
     * Return a movie trailer key  that has been built up from
     * parsing a JSON response.
     */
    public static String extract_Trailer_Info_FromJson(String jsonResponse) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        String trailer_key = "";
        try {
            JSONObject rootJSONObject = new JSONObject(jsonResponse);

            JSONArray movies_arrray = rootJSONObject.optJSONArray("results");

            if (movies_arrray.length() != 0) {
                JSONObject trailer_info = movies_arrray.optJSONObject(0);
                trailer_key = trailer_info.optString("key");
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the Trailer Video JSON results", e);
        }

        // Return the video Key to display on Youtube
        return trailer_key;

    }

    private static HashMap<Integer, String> genres_Ids = getGenres_list();

    private static HashMap<Integer, String> getGenres_list() {

        //Get the list of official genres for movies.
        HashMap<Integer, String> genres_list = new HashMap<>();

        genres_list.put(28, "Action");
        genres_list.put(12, "Adventure");
        genres_list.put(16, "Animation");
        genres_list.put(35, "Comedy");
        genres_list.put(80, "Crime");
        genres_list.put(99, "Documentary");
        genres_list.put(18, "Drama");
        genres_list.put(10751, "Family");
        genres_list.put(14, "Fantasy");
        genres_list.put(36, "History");
        genres_list.put(27, "Horror");
        genres_list.put(10402, "Music");
        genres_list.put(9648, "Mystery");
        genres_list.put(10749, "Romance");
        genres_list.put(878, "Science Fiction");
        genres_list.put(10770, "TV Movie");
        genres_list.put(53, "Thriller");
        genres_list.put(10752, "War");
        genres_list.put(37, "Western");

        return genres_list;
    }


    /**
     * Return a list of {@link Movie} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Movie> extractFeatureFromJson(String jsonResponse) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding Movies to
        ArrayList<Movie> moviesList = new ArrayList<>();

        try {
            JSONObject rootJSONObject = new JSONObject(jsonResponse);

            JSONArray movies_arrray = rootJSONObject.optJSONArray("results");

            for (int i = 0; i < movies_arrray.length(); i++) {

                JSONObject movie = movies_arrray.optJSONObject(i);

                //JSONObject properties = movie.optJSONObject("properties");

                // Extract the value for the key called "title"
                String title = movie.optString("title");

                // Extract the value for the key called "poster_path"
                String poster_path = movie.optString("poster_path");

                // Extract the value for the key called "release_date"
                String release_date = movie.optString("release_date");

                // Extract the value for the key called "vote_average"
                double vote_average = movie.optDouble("vote_average");

                // Extract the value for the key called "overview"
                String overview = movie.optString("overview");

                // Extract the value for the key called "id"
                int movie_id = movie.optInt("id");

                // Extract the value for the key called "video"
                boolean video = movie.optBoolean("video");

                //Extract the value for the key called genre_ids
                JSONArray movie_genres_ids = movie.optJSONArray("genre_ids");
                StringBuilder movie_genres_names = new StringBuilder();
                int length = movie_genres_ids.length();
                for (int j = 0; j < length; j++) {

                    // get the Correct Genre name for each genre id
                    int genre_Key = (Integer) movie_genres_ids.get(j);
                    if (genres_Ids.containsKey(genre_Key)) {
                        movie_genres_names.append(genres_Ids.get(genre_Key));
                        if (j != length - 1) {
                            // append " , " after each category except tha last one
                            movie_genres_names.append(", ");
                        }
                    }
                }
                moviesList.add(new Movie(movie_id, poster_path, title, release_date, overview, vote_average, video, movie_genres_names));
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the Movie JSON results", e);
        }

        // Return the list of earthquakes
        return moviesList;
    }


}