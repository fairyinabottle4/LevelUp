package com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.example.LevelUp.ui.Occasion;
import com.example.LevelUp.ui.events.EventsFragment;
import com.example.LevelUp.ui.jios.JiosFragment;
import com.example.LevelUp.ui.dashboard.DashboardFragment;
import com.example.LevelUp.ui.mktplace.MktplaceFragment;
import com.example.LevelUp.ui.mylist.MylistFragment;
import com.example.tryone.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {
    // For Login
    public static UserItem currUser;
    public static FirebaseAuth mAuth;
    // For User Information on MyList Fragment
    private static String displayName;

    private static String displayResidential;

    private static String displayTelegram = "default";

    private static long displayPhone = 0;

    private static final String TAG = "MainActivity";

    private static String currUserProfilePicture;
    private DatabaseReference mDatabaseReferenceUser;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    // For things in MyList
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReferenceActivityEvent;
    private DatabaseReference mDatabaseReferenceActivityJio;
    private DatabaseReference mDatabaseReferenceLikeEvent;
    private DatabaseReference mDatabaseReferenceLikeJio;
    private DatabaseReference mDatabaseReferenceLikeMktplace;

    public static final ArrayList<String> mEventIDs = new ArrayList<>();
    public static final ArrayList<String> mJioIDs = new ArrayList<>();

    public static final ArrayList<String> mLikeEventIDs = new ArrayList<>();
    public static final ArrayList<String> mLikeJioIDs = new ArrayList<>();
    public static final ArrayList<String > mLikeMktplaceIDs = new ArrayList<>();


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
                    try {
                        ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                        String selectedUserID = selected.getUserID();
                        if (selectedUserID.equals(fbUIDFinal)) {
                            // it is my event so I add EventID into arraylist
                            mEventIDs.add(selected.getOccasionID());
                        }
                    } catch (Exception e) {
                        System.out.println(e);
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
                    try {
                        ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                        String selectedUserID = selected.getUserID();
                        if (selectedUserID.equals(fbUIDFinal)) {
                            mJioIDs.add(selected.getOccasionID());
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeMyLikes() {
        // final String fbUIDFinal = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String fbUIDFinal = currUser.getId();

        // pulling activityevent with my userID
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReferenceLikeEvent = mFirebaseDatabase.getReference().child("LikeEvent");
        mDatabaseReferenceLikeEvent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mLikeEventIDs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                        String selectedUserID = selected.getUserID();
                        if (selectedUserID.equals(fbUIDFinal)) {
                            // it is my LikeEvent so I add EventID into arraylist
                            mLikeEventIDs.add(selected.getOccasionID());
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mDatabaseReferenceLikeJio = mFirebaseDatabase.getReference().child("LikeJio");
        mDatabaseReferenceLikeJio.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mLikeJioIDs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                        String selectedUserID = selected.getUserID();
                        if (selectedUserID.equals(fbUIDFinal)) {
                            mLikeJioIDs.add(selected.getOccasionID());
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mDatabaseReferenceLikeMktplace = mFirebaseDatabase.getReference().child("LikeMktplace");
        mDatabaseReferenceLikeMktplace.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mLikeMktplaceIDs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                        String selectedUserID = selected.getUserID();
                        if (selectedUserID.equals(fbUIDFinal)) {
                            mLikeMktplaceIDs.add(selected.getOccasionID());
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeUser() {
        final String fbUidFinal = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseReferenceUser = mFirebaseDatabase.getReference().child("Users");
        mDatabaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        UserItem selected = snapshot.getValue(UserItem.class);
                        String id = selected.getId();
                        if (fbUidFinal.equals(id)) {
                            currUser = selected;
                            currUser.setProfilePictureUri(currUserProfilePicture);
                            initializeMyList();
                            initializeMyLikes();
                            break;
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
                if (!existInFirebase()) {
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
                    initializeUser();
                } else { // first time sign in
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

    /**
     * Sorts a given list of Occasion items by date and time
     *
     * @param list List of Occasion items to be sorted
     */
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

    public static UserItem getCurrentUser() {
        return currUser;
    }

    public static String getDisplayName() {
        return displayName;
    }
    public static void setDisplayName(String name) {
        displayName = name;
    }
    public static String getDisplayResidential() {
        return displayResidential;
    }
    public static void setDisplayResidential(String display) {
        displayResidential = display;
    }

    public static String getDisplayTelegram() {
        return displayTelegram;
    }
    public static void setDisplayTelegram(String display) {
        displayTelegram = display;
    }
    public static long getDisplayPhone() {
        return displayPhone;
    }

    public static void setDisplayPhone(long display) {
        displayPhone = display;
    }


}
