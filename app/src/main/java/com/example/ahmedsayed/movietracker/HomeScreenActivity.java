package com.example.ahmedsayed.movietracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class HomeScreenActivity extends AppCompatActivity {


    public static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 1;

    // Firebase instance Variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mfFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        // initialize Firebase Components
        mUsername = ANONYMOUS;
        mFirebaseDatabase = FirebaseDatabase.getInstance();  // establish the mFirebaseDatabase object which is the main access point for our Database.
        mfFirebaseAuth = FirebaseAuth.getInstance();


// initialize " mAuthStateListener "
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                // Remember : before implementing the " mAuthStateListener "and start write any code inside it ,
                // You Have to attach it( " mAuthStateListener" ) to the " mFirebaseAuth " instance in the " OnResume() "
                // And also detach it on the "onPause()"


                // implementation of "mAuthStateListener"

                /* what we want to do, is to actually check to see if the user is logged in or not ,
                And then if they're not logged in, you're going to show them that " log in flow " screen.

                Now, in this " onAuthStateChanged " method, you're given this parameter here, this "firebaseAuth" parameter.
                And the difference between this " firebaseAuth" parameter and the one that you initialized up here,
                is that this one is guaranteed to contain whether at that moment, the user is authenticated or Not.

                And this variable has two states, it's either signed in or it's signed out.
                Okay, so I'm going to use this variable to get something called a FirebaseUser,
                and then I can check if the user's actually logged in,*/

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // user is signed in
                    String username = user.getDisplayName(); // get the username
                    onSignedInInitialize(username);
                } else {

                    // tear down the Ui by calling " onSignedOutCleanUp " method
                    onSignedOutCleanUp();

                    // then we need to redirect the user to the " log in flow " Screen as the user now is sinned out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.PhoneBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                    // RC_SIGN_IN : "RC" stands for  request code
                }
            }
        };

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager mviewpager = (ViewPager) findViewById(R.id.homeScreen_section_2_viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        SimpleFragmentPagerAdapter mFragmentAdapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        mviewpager.setAdapter(mFragmentAdapter);

        //Load all the fragments together. for this use
        // pager.setOffscreenPageLimit(numberOfFragments); immediate after setting the adapter to viepager.
        //"OffscreenPage" says it all. It means how many invisible fragments you want to keep alive
        mviewpager.setOffscreenPageLimit(3); // this line to prevent losing data when swiping between fragments

        //////////////////////////////
        //   TODO  :  // Connect the tab layout with the view pager. This will
        // 1. Update the tab layout when the view pager is swiped
        // 2. Update the view pager when a tab is selected
        // 3. Set the tab layout's tab names with the view pager's adapter's titles
        // by calling onPageTitle()
        ////////////////////////////

        // find the tab layout that shows the tabs
        android.support.design.widget.TabLayout tabLayout = (android.support.design.widget.TabLayout) findViewById(R.id.homeScreen_section_2_sliding_tabs);
        tabLayout.setupWithViewPager(mviewpager);
        tabLayout.setTabMode(tabLayout.MODE_SCROLLABLE);


        // Search Movie

        ImageButton search_btn = (ImageButton) findViewById(R.id.homeScreen_section_1_secrch_icon);
        final EditText search_input_EditText = (EditText) findViewById(R.id.homeScreen_section_1_Search_EditText);


        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = search_input_EditText.getText().toString();
                if (TextUtils.isEmpty(input)) {
                    Toast.makeText(HomeScreenActivity.this, "Please ,Enter Your Search Text", Toast.LENGTH_SHORT).show();
                } else {
                    HelperMethods.setInputSearch(input);
                    Toast.makeText(HomeScreenActivity.this, "Searching for Movie :" + HelperMethods.getInputSearch(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomeScreenActivity.this, SearchResultActivity.class);
                    intent.putExtra("search_input", HelperMethods.getInputSearch());
                    startActivity(intent);
                }
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Handling Cancelled " Sign In "
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    private void onSignedInInitialize(String username) {
        mUsername = username; // set the "mUsername" to the name of the current signed in user

        // attach the ReadMessagesListener  from the database only at this point ,
        // cause users are allowed to read form the database only if the they're actually " logged in "

        //attachDatabaseReadListener();
    }

    private void onSignedOutCleanUp() {
        // Now in the "signed Out " we are going to do the opposite of the "Sign in "

        // tear down the Ui
        // 1- unset the username
        // 2- clear the messages list --> we doing that cause a user who is no longer signed in, shouldn't be abel to see those messages
        // 3- detach the Database Read Listener

        mUsername = ANONYMOUS;
        //detachDatabaseReadListener();

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Removing  the " mAuthStateListener " as the activity in no longer in the foreground
        if (mAuthStateListener != null) {
            mfFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        // And also detach the DatabaseReadListener and Clear the " messages list" Adapter
        //  detachDatabaseReadListener();
        // mMessageAdapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //adding the " mAuthStateListener " as the activity in the foreground
        mfFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handling " sign out "
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                // Sign out
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
