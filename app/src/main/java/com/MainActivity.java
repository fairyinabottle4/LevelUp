package com;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.Mylist.LevelUp.ui.mylist.MylistAdapter;
import com.example.LevelUp.ui.Occasion;
import com.example.LevelUp.ui.events.EventsFragment;
import com.example.LevelUp.ui.jios.JiosFragment;
import com.example.LevelUp.ui.mylist.MylistFragment;
import com.example.tryone.R;
import com.example.LevelUp.ui.dashboard.DashboardFragment;
import com.example.LevelUp.ui.mktplace.MktplaceFragment;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    // For Login
    public static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final String TAG = "MainActivity";

    public static UserItem currUser;
    private static String currUserProfilePicture;
    private DatabaseReference mDatabaseReferenceUser;

    // For User Information on MyList Fragment
    public static String display_name;
    public static String display_residential;

    // For things in MyList
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReferenceActivityEvent;
    private DatabaseReference mDatabaseReferenceActivityJio;

    public static final ArrayList<String> mEventIDs = new ArrayList<>();
    public static final ArrayList<String> mJioIDs = new ArrayList<>();

    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase components
        initializeFirebase();

        BottomNavigationView navView = findViewById(R.id.nav_view);

        if (savedInstanceState == null) {
            navView.setSelectedItemId(R.id.navigation_dashboard);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new DashboardFragment()).commit();
        }

        navView.setOnNavigationItemSelectedListener(navListener);

        initializeLogin();

        // initializeMyList();

        // Toast.makeText(MainActivity.this, mJioIDs.toString(), Toast.LENGTH_SHORT).show();

    }

    private void initializeMyList() {
        // final String fbUIDFinal = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String fbUIDFinal = currUser.getId();
        // pulling activityevent with my userID
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReferenceActivityEvent = mFirebaseDatabase.getReference().child("ActivityEvent");
        mDatabaseReferenceActivityEvent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mEventIDs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                    String selectedUserID = selected.getUserID();
                    if (selectedUserID.equals(fbUIDFinal)) {
                        // it is my event so I add EventID into arraylist
                        mEventIDs.add(selected.getOccasionID());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mDatabaseReferenceActivityJio = mFirebaseDatabase.getReference().child("ActivityJio");
        mDatabaseReferenceActivityJio.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mJioIDs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                    String selectedUserID = selected.getUserID();
                    if (selectedUserID.equals(fbUIDFinal)) {
                        mJioIDs.add(selected.getOccasionID());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeUser() {
        final String fbUIDfinal = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseReferenceUser = mFirebaseDatabase.getReference().child("Users");
        mDatabaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserItem selected = snapshot.getValue(UserItem.class);
                    String id = selected.getId();
                    if (fbUIDfinal.equals(id)) {
                        currUser = selected;
                        currUser.setProfilePictureUri(currUserProfilePicture);
                        initializeMyList();
                        break;
                    }
                }
                if (!existInFirebase()){
                    // start registration of details
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeFirebase() {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        mFirebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void initializeLogin() {

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //onSignedInInitialize(user.getDisplayName());
                    // mReferenceUsers.setValue(user);
                    initializeUser();
                } else { // first time sign in
                    //onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
//                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                        startActivity(intent);

                }
            }
        };
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selected = null;
                    String fragTag = null;

                    switch (item.getItemId()) {
                        case R.id.navigation_mktPlace:
                            selected = new MktplaceFragment();
                            fragTag = "MktplaceFragment";
                            break;
                        case R.id.navigation_jios:
                            selected = new JiosFragment();
                            fragTag = "JiosFragment";
                            break;
                        case R.id.navigation_dashboard:
                            selected = new DashboardFragment();
                            fragTag = "DashboardFragment";
                            break;
                        case R.id.navigation_events:
                            // Toolbar eventsToolbar = (Toolbar) findViewById(R.id.events_toolbar);
                            // setSupportActionBar(eventsToolbar);
                            selected = new EventsFragment();
                            fragTag = "EventsFragment";
                            break;
                        case R.id.navigation_myList:
                            selected = new MylistFragment();
                            fragTag = "MylistFragment";
                            break;
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment, selected, fragTag)
                            .addToBackStack(fragTag)
                            .commit();
                    return true;
                }
            };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            // Sign-in succeeded, set up the Registration page
            initializeUser(); // if user is already in database, set currUser to the user
        } else if (resultCode == RESULT_CANCELED) {
            // Sign in was canceled by the user, finish the activity
            Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean existInFirebase() {
        // return true if user is already in Users table
        return (currUser != null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    public static void sort(ArrayList<? extends Occasion> list) {
        Collections.sort(list, new Comparator<Occasion>() {
            @Override
            public int compare(Occasion o1, Occasion o2) {
                int compareDate = 0;
                compareDate = o1.getDateInfo().compareTo(o2.getDateInfo());
                if (compareDate == 0) {
                    int compareHour = 0;
                    compareHour = o1.getHourOfDay() - o2.getHourOfDay();
                    if (compareHour == 0) {
                        int compareMinute = 0;
                        compareMinute = o1.getMinute() - o2.getMinute();
                        return compareMinute;
                    } else {
                        return compareHour;
                    }
                } else {
                    return compareDate;
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    public static void setCurrUserProfilePicture(String currUserProfilePicture) {
        MainActivity.currUserProfilePicture = currUserProfilePicture;
    }
}
