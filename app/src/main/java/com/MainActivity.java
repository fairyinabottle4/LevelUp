package com;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;


import com.Dashboard.LevelUp.ui.dashboard.TrendingFragment;
import com.Events.LevelUp.ui.events.EventsItem;
import com.Jios.LevelUp.ui.jios.JiosItem;
import com.Mktplace.LevelUp.ui.mktplace.MktplaceAdapter;
import com.Mktplace.LevelUp.ui.mktplace.MktplaceItem;
import com.Mylist.LevelUp.ui.mylist.MylistAdapter;
import com.example.LevelUp.ui.Occasion;
import com.example.LevelUp.ui.events.EventsFragment;
import com.example.LevelUp.ui.jios.JiosFragment;
import com.example.LevelUp.ui.mylist.MylistFragment;
import com.example.tryone.R;
import com.example.LevelUp.ui.dashboard.DashboardFragment;
import com.example.LevelUp.ui.mktplace.MktplaceFragment;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    // For Login
    private EditText editTextName, editTextEmail, editTextPassword;
    public static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;
    private static final String TAG = "MainActivity";

    public static UserItem currUser;
    private DatabaseReference mDatabaseReferenceUser;

    // For User Information on MyList Fragment
    public static String display_name;
    public static String display_residential;
    private String fbUID;

    // For things in MyList
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReferenceActivityEvent;
    private DatabaseReference mDatabaseReferenceActivityJio;

    public static ArrayList<Occasion> mOccasionEvents = new ArrayList<>();
    public static ArrayList<Occasion> mOccasionJios = new ArrayList<>();
    public static ArrayList<String> mEventIDs = new ArrayList<>();
    public static ArrayList<String> mJioIDs = new ArrayList<>();

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

        initializeMyList();

    }

    private void initializeMyList() {
        final String fbUIDFinal = fbUID;
        // pulling activityevent with my userID
        mDatabaseReferenceActivityEvent = mFirebaseDatabase.getReference().child("ActivityEvent");
        mDatabaseReferenceActivityEvent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
        DatabaseReference mDatabaseReferenceEvents = mFirebaseDatabase.getReference().child("Events");
        DatabaseReference mDatabaseReferenceJios = mFirebaseDatabase.getReference().child("Jios");

        mDatabaseReferenceEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Occasion selected = snapshot.getValue(EventsItem.class);
                    String eventID = selected.getOccasionID();
                    if (mEventIDs.contains(eventID)) {
                        mOccasionEvents.add(selected);

                    }
                }
//                MylistAdapter myListAdapter = new MylistAdapter(mOccasionAll);
//                mAdapter = myListAdapter;
//                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseReferenceJios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Occasion selected = snapshot.getValue(JiosItem.class);
                    String jioID = selected.getOccasionID();
                    if (mJioIDs.contains(jioID)) {
                        mOccasionJios.add(selected);
                    }
                }
//                MylistAdapter myListAdapter = new MylistAdapter(mOccasionAll);
//                mAdapter = myListAdapter;
//                mRecyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static ArrayList<Occasion> getmOccasionEvents() {
        return mOccasionEvents;
    }

    public static ArrayList<Occasion> getmOccasionJios() {
        return mOccasionJios;
    }

    private void initializeUser() {
        final String fbUIDfinal = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fbUID = fbUIDfinal;
        mDatabaseReferenceUser = mFirebaseDatabase.getReference().child("Users");
        mDatabaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserItem selected = snapshot.getValue(UserItem.class);
                    String id = selected.getId();
                    if (fbUIDfinal.equals(id)) {
                        currUser = selected;
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
                mUser = user;
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

}
