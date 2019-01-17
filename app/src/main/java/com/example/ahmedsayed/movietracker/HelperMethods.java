package com.example.ahmedsayed.movietracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.regex.Pattern;

public class HelperMethods {
    private HelperMethods() {
    }

    public static final String API_KEY = "?api_key=b37046904512d6682e3db2bd0b364c74";
    public static final String BASE_REQUEST_URL = "https://api.themoviedb.org/3";
    private static String inputSearch = "";

    public static String getInputSearch() {
        return inputSearch;
    }

    public static void setInputSearch(String inputSaarch) {

        StringBuilder final_input_search = new StringBuilder(inputSaarch);
        int size = final_input_search.length();
        for (int i = 0; i < size; i++) {
            if (final_input_search.charAt(i) == ' ')
            {
                final_input_search.setCharAt(i, '+');
            }
        }
        HelperMethods.inputSearch = final_input_search.toString();
    }


    public static boolean Check_network_status(ConnectivityManager connectivityManager) {
        // check the network status
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean access_network_state = ((networkInfo != null) && networkInfo.isConnectedOrConnecting());
        return access_network_state;
    }


    private static boolean isValidEmailForm(Context mContext, String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(mContext, " Empty Email, " + mContext.getString(R.string.error_field_required), Toast.LENGTH_SHORT).show();

            return false;
        }
        return pat.matcher(email).matches();
    }


    public static boolean validateUserInput(Context mContext, User mUser) {
        boolean valid = true;

        // if email is null Or inValid then return false
        String email = mUser.getmEmail();
        if (HelperMethods.isValidEmailForm(mContext, email) == false) {
            Toast.makeText(mContext, mContext.getString(R.string.error_invalid_email), Toast.LENGTH_SHORT).show();
            valid = false;
        }

        // Check Password is not null && password.length() > 6 characters
        String password = mUser.getmPassword();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(mContext, " Empty Password, " + mContext.getString(R.string.error_field_required), Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (password.length() <= 6) {
            Toast.makeText(mContext, mContext.getString(R.string.error_invalid_password), Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }


    public static boolean Check_Confirm_password(Context mContext, String password, String confirm_password) {
        boolean valid = true;

        // Check confirm Password is not  null && confirm Password == password
        if (TextUtils.isEmpty(confirm_password)) {
            Toast.makeText(mContext, " Empty Confirm Password, " + mContext.getString(R.string.error_field_required), Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (TextUtils.equals(password, confirm_password) == false) {
            Toast.makeText(mContext, mContext.getString(R.string.error_incorrect_confirm_password), Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    public static void showProgressDialog(ProgressDialog mProgressDialog, Context mContext) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage(mContext.getString(R.string.loading));
            mProgressDialog.setIndeterminate(true); // "loading amount" is not measured
        }

        mProgressDialog.show();
    }

    public static void hideProgressDialog(ProgressDialog mProgressDialog) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
