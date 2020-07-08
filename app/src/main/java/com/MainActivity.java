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

    // These 4 lines are for MyList Fragment
    public static ArrayList<Occasion> mOccasionListReal = new ArrayList<>();
    public static ArrayList<Occasion> mOccasionListRealFull = new ArrayList<>();
    public static ArrayList<Integer> mJiosIDs = new ArrayList<>();
    public static ArrayList<Integer> mEventsIDs = new ArrayList<>();

    // For User Information on MyList Fragment
    public static String display_name;
    public static String display_residential;



//    private static ArrayList<JiosItem> jiosListMain = new ArrayList<>();
//    private static ArrayList<JiosItem> jiosListMainCopy;
//
//    private static ArrayList<EventsItem> eventsListMain = new ArrayList<>();
//    private static ArrayList<EventsItem> eventsListMainCopy;
//
//    private static ArrayList<MktplaceItem> mktplaceItemList = new ArrayList<>();
//
//    ValueEventListener mValueEventListenerEvents;
//    ValueEventListener mValueEventListenerJios;

    private String mUsername;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReferenceUsers;

//    private DatabaseReference mReferenceJios;
//    private DatabaseReference mReferenceEvents;
//    private DatabaseReference mReferenceMktplace;

    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load saved My List
        // loadMyListData();

        // Initialize Firebase components
        initializeFirebase();

//        // Add all items in Jios to jiosListMain
//        initializeJiosListMain();
//
//        //Add all items in Mktplace to mktplaceItemList
//        initializeMktplace();
//
//        // Sending jiosListMain to JiosFragment
//        JiosFragment.setJiosItemList(jiosListMain);
//
//        // Add all items in Events to eventsListMain
//        initializeEventsListMain();
//
//        // Sending eventsListMain to EventsFragment
//        EventsFragment.setEventsItemList(eventsListMain);
//
//        //Sending mktplaceItemList to MktplaceFragment
//        MktplaceFragment.setMktplaceItemList(mktplaceItemList);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        if (savedInstanceState == null) {
            navView.setSelectedItemId(R.id.navigation_dashboard);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new DashboardFragment()).commit();
        }

        navView.setOnNavigationItemSelectedListener(navListener);

        initializeLogin();


        //initializeUser();

    }

    private void initializeUser() {
        final String fbUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Toast.makeText(getActivity(), fbUID, Toast.LENGTH_LONG).show();
        mDatabaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserItem selected = snapshot.getValue(UserItem.class);
                    String id = selected.getId();

                    if (fbUID.equals(id)) {
                        currUser = selected;
                    }
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
        mReferenceUsers = mFirebaseDatabase.getReference("Users");
//        mReferenceJios = mFirebaseDatabase.getReference().child("Jios");
//        mReferenceEvents = mFirebaseDatabase.getReference().child("Events");
//        mReferenceMktplace = mFirebaseDatabase.getReference().child("mktplace uploads");
    }

//    private void initializeJiosListMain() {
//        mValueEventListenerJios = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                jiosListMain.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    jiosListMain.add(snapshot.getValue(JiosItem.class));
//                }
//                jiosListMainCopy = new ArrayList<>(jiosListMain);
//                sort(jiosListMain);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//        mReferenceJios.addValueEventListener(mValueEventListenerJios);
//    }
//
//    private void initializeEventsListMain(){
//        mValueEventListenerEvents = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                eventsListMain.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    eventsListMain.add(snapshot.getValue(EventsItem.class));
//                }
//                eventsListMainCopy = new ArrayList<>(eventsListMain);
//                sort(eventsListMain);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//        mReferenceEvents.addValueEventListener(mValueEventListenerEvents);
//    }
//
//    private void initializeMktplace() {
//        mReferenceMktplace.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    MktplaceItem upload = postSnapshot.getValue(MktplaceItem.class);
//                    mktplaceItemList.add(upload);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//    }

    private void initializeLogin() {
//        editTextName = findViewById(R.id.editTextLoginName);
//        editTextEmail = findViewById(R.id.editTextLoginEmail);
//        editTextPassword = findViewById(R.id.editTextLoginPassword);
//
//        final String name = editTextName.getText().toString().trim();
//        final String email = editTextEmail.getText().toString().trim();
//        final String password = editTextPassword.getText().toString().trim();

//        if (mAuth.getCurrentUser() != null) {
//            // handle already login user
//        } else {
//            // start login activity
//            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//            startActivity(intent);
//            // registerUser();
//        }

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

    @Override
    protected void onStart() {
//        Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
//        initializeLogin();
        super.onStart();
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
            initializeUser();
            if (!existInFirebase()){
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
            }

            // Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
        } else if (resultCode == RESULT_CANCELED) {
            // Sign in was canceled by the user, finish the activity
            Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public boolean existInFirebase() {
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

//    //This unsorted copy is sent to JiosAdapter
//    public static ArrayList<JiosItem> getJiosListCopy() {
//        return jiosListMainCopy;
//    }
//
//    //This is sorted and sent to MylistFragment
//    public static ArrayList<JiosItem> getJiosListReal() {
//        return jiosListMain;
//    }
//
//    //This unsorted copy is sent to EventsAdapter
//    public static ArrayList<EventsItem> getEventsListCopy() {
//        return eventsListMainCopy;
//    }
//
//    //This is sorted and sent to MylistFragment
//    public static ArrayList<EventsItem> getEventsListReal() {
//        return eventsListMain;
//    }




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
    protected void onPause() {
        super.onPause();
        // saveMyListData();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        // saveMyListData();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // saveMyListData();
        super.onDestroy();
    }




}
