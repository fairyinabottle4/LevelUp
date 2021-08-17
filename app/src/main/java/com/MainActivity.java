package com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.example.LevelUp.ui.Occasion;
import com.example.LevelUp.ui.dashboard.DashboardFragment;
import com.example.LevelUp.ui.events.EventsFragment;
import com.example.LevelUp.ui.jios.JiosFragment;
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
    private static UserItem currUser;
    private static FirebaseAuth auth;
    // For User Information on MyList Fragment
    private static String displayName;

    private static String displayResidential;

    private static String displayTelegram = "default";

    private static long displayPhone = 0;

    private static final String TAG = "MainActivity";

    private static String currUserProfilePicture;
    private static final ArrayList<String> eventIDs = new ArrayList<>();
    private static final ArrayList<String> jioIDs = new ArrayList<>();

    private static final ArrayList<String> likeEventIDs = new ArrayList<>();

    private static final ArrayList<String> likeJioIDs = new ArrayList<>();

    private static final ArrayList<String> likeMktplaceIDs = new ArrayList<>();


    private static final int RC_SIGN_IN = 1;
    private DatabaseReference databaseReferenceUser;

    private FirebaseAuth.AuthStateListener authStateListener;

    // For things in MyList
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReferenceActivityEvent;
    private DatabaseReference databaseReferenceActivityJio;
    private DatabaseReference databaseReferenceLikeEvent;
    private DatabaseReference databaseReferenceLikeJio;
    private DatabaseReference databaseReferenceLikeMktplace;

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
                    selected = new EventsFragment();
                    fragTag = "EventsFragment";
                    break;
                case R.id.navigation_myList:
                    selected = new MylistFragment();
                    fragTag = "MylistFragment";
                    break;
                default:
                }
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, selected, fragTag)
                    .addToBackStack(fragTag)
                    .commit();
                return true;
            }
        };


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
        final String fbUidFinal = currUser.getId();

        // pulling activityevent with my userID
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceActivityEvent = firebaseDatabase.getReference().child("ActivityEvent");
        databaseReferenceActivityEvent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventIDs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                        String selectedUserID = selected.getUserID();
                        if (selectedUserID.equals(fbUidFinal)) {
                            // it is my event so I add EventID into arraylist
                            eventIDs.add(selected.getOccasionID());
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
        databaseReferenceActivityJio = firebaseDatabase.getReference().child("ActivityJio");
        databaseReferenceActivityJio.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jioIDs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                        String selectedUserID = selected.getUserID();
                        if (selectedUserID.equals(fbUidFinal)) {
                            jioIDs.add(selected.getOccasionID());
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
        final String fbUidFinal = currUser.getId();

        // pulling activityevent with my userID
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceLikeEvent = firebaseDatabase.getReference().child("LikeEvent");
        databaseReferenceLikeEvent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likeEventIDs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                        String selectedUserID = selected.getUserID();
                        if (selectedUserID.equals(fbUidFinal)) {
                            // it is my LikeEvent so I add EventID into arraylist
                            likeEventIDs.add(selected.getOccasionID());
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
        databaseReferenceLikeJio = firebaseDatabase.getReference().child("LikeJio");
        databaseReferenceLikeJio.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likeJioIDs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                        String selectedUserID = selected.getUserID();
                        if (selectedUserID.equals(fbUidFinal)) {
                            likeJioIDs.add(selected.getOccasionID());
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
        databaseReferenceLikeMktplace = firebaseDatabase.getReference().child("LikeMktplace");
        databaseReferenceLikeMktplace.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likeMktplaceIDs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                        String selectedUserID = selected.getUserID();
                        if (selectedUserID.equals(fbUidFinal)) {
                            likeMktplaceIDs.add(selected.getOccasionID());
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
        databaseReferenceUser = firebaseDatabase.getReference().child("Users");
        databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
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
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void initializeLogin() {

        authStateListener = new FirebaseAuth.AuthStateListener() {
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
        auth.addAuthStateListener(authStateListener);
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
        auth.removeAuthStateListener(authStateListener);
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

    public static UserItem getCurrUser() {
        return currUser;
    }

    public static void setCurrUser(UserItem user) {
        currUser = user;
    }
    public static ArrayList<String> getEventIDs() {
        return eventIDs;
    }
    public static ArrayList<String> getJioIds() {
        return jioIDs;
    }
    public static ArrayList<String> getLikeEventIDs() {
        return likeEventIDs;
    }
    public static ArrayList<String> getLikeJioIDs() {
        return likeJioIDs;
    }
    public static ArrayList<String> getLikeMktplaceIDs() {
        return likeMktplaceIDs;
    }

}
