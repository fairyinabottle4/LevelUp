package com;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.LevelUp.ui.Occasion;
import com.example.LevelUp.ui.events.EventsFragment;
import com.example.LevelUp.ui.jios.JiosFragment;
import com.example.LevelUp.ui.mylist.MylistFragment;
import com.example.tryone.R;
import com.example.LevelUp.ui.dashboard.DashboardFragment;
import com.example.LevelUp.ui.mktplace.MktplaceFragment;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final String TAG = "MainActivity";

    // These 4 lines are for MyList Fragment
    public static ArrayList<Occasion> mOccasionListReal = new ArrayList<>();
    public static ArrayList<Occasion> mOccasionListRealFull = new ArrayList<>();
    public static ArrayList<Integer> mJiosIDs = new ArrayList<>();
    public static ArrayList<Integer> mEventsIDs = new ArrayList<>();

    private static ArrayList<JiosItem> jiosListMain = new ArrayList<>();
    private static ArrayList<JiosItem> jiosListMainCopy;

    private static ArrayList<EventsItem> eventsListMain = new ArrayList<>();
    private static ArrayList<EventsItem> eventsListMainCopy;

    ValueEventListener mValueEventListenerEvents;
    ValueEventListener mValueEventListenerJios;

    private String mUsername;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReferenceJios;
    private DatabaseReference mReferenceEvents;


    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // loadMyListData();

        //Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReferenceJios = mFirebaseDatabase.getReference().child("Jios");

        //this will add all items in Jios to jiosListMain
        mValueEventListenerJios = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jiosListMain.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    jiosListMain.add(snapshot.getValue(JiosItem.class));
                }
                jiosListMainCopy = new ArrayList<>(jiosListMain);
                sort(jiosListMain);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mReferenceJios.addValueEventListener(mValueEventListenerJios);

        //Sending the JiosItemList to JiosFragment
        JiosFragment.setJiosItemList(jiosListMain);


        //doing the same for EventsFragment
        mReferenceEvents = mFirebaseDatabase.getReference().child("Events");
        mValueEventListenerEvents = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventsListMain.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    eventsListMain.add(snapshot.getValue(EventsItem.class));
                }
                eventsListMainCopy = new ArrayList<>(eventsListMain);
                sort(eventsListMain);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mReferenceEvents.addValueEventListener(mValueEventListenerEvents);
        EventsFragment.setEventsItemList(eventsListMain);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        if (savedInstanceState == null) {
            navView.setSelectedItemId(R.id.navigation_dashboard);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new DashboardFragment()).commit();
        }



        /*
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_mktPlace, R.id.navigation_dashboard, R.id.navigation_jios)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

         */

        navView.setOnNavigationItemSelectedListener(navListener);
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //onSignedInInitialize(user.getDisplayName());
                } else {
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
                }
            }
        };

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selected = null;

                    switch (item.getItemId()) {
                        case R.id.navigation_mktPlace:
                            selected = new MktplaceFragment();
                            break;
                        case R.id.navigation_jios:
                            selected = new JiosFragment();
                            break;
                        case R.id.navigation_dashboard:
                            selected = new DashboardFragment();
                            break;
                        case R.id.navigation_events:
                            // Toolbar eventsToolbar = (Toolbar) findViewById(R.id.events_toolbar);
                            // setSupportActionBar(eventsToolbar);
                            selected = new EventsFragment();
                            break;
                        case R.id.navigation_myList:
                            selected = new MylistFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment, selected).commit();
                    return true;
                }
            };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            // Sign-in succeeded, set up the UI
            Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
        } else if (resultCode == RESULT_CANCELED) {
            // Sign in was canceled by the user, finish the activity
            Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
            finish();
        }
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

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    public void sort(ArrayList<? extends Occasion> list) {
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

    //This unsorted copy is sent to JiosAdapter
    public static ArrayList<JiosItem> getJiosListCopy() {
        return jiosListMainCopy;
    }

    //This is sorted and sent to MylistFragment
    public static ArrayList<JiosItem> getJiosListReal() {
        return jiosListMain;
    }

    //This unsorted copy is sent to EventsAdapter
    public static ArrayList<EventsItem> getEventsListCopy() {
        return eventsListMainCopy;
    }

    //This is sorted and sent to MylistFragment
    public static ArrayList<EventsItem> getEventsListReal() {
        return eventsListMain;
    }

    /*
    public void saveMyListData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json1 = gson.toJson(mJiosIDs);
        String json2 = gson.toJson(mEventsIDs);
        editor.putString("My List Jios", json1);
        editor.putString("My List Events", json2);
        // editor.clear().apply();
        editor.apply();
    }

    public void loadMyListData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonJios = sharedPreferences.getString("My List Jios", null);
        String jsonEvents = sharedPreferences.getString("My List Events", null);
        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
        mJiosIDs = gson.fromJson(jsonJios, type);
        mEventsIDs = gson.fromJson(jsonEvents, type);

        if (mJiosIDs == null) {
            mJiosIDs = new ArrayList<>();
        }
        if (mEventsIDs == null) {
            mEventsIDs = new ArrayList<>();
        }
    }

    @Override
    protected void onStop() {
        saveMyListData();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        saveMyListData();
        super.onDestroy();
    }

     */

}
