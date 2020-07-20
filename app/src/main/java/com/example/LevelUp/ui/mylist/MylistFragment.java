package com.example.LevelUp.ui.mylist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ActivityOccasionItem;
import com.Events.LevelUp.ui.events.EventsItem;
import com.Jios.LevelUp.ui.jios.JiosItem;
import com.MainActivity;
import com.Mylist.LevelUp.ui.mylist.CreatedFragment;
import com.Mylist.LevelUp.ui.mylist.EditUserInfoActivity;
import com.Mylist.LevelUp.ui.mylist.HistoryEventsFragment;
import com.Mylist.LevelUp.ui.mylist.HistoryFragment;
import com.Mylist.LevelUp.ui.mylist.MylistAdapter;
import com.UserItem;
import com.example.LevelUp.ui.Occasion;
import com.example.tryone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MylistFragment extends Fragment {
//    ArrayList<Occasion> mOccasionListEventsInitial = new ArrayList<>();
//    ArrayList<Occasion> mOccasionListJiosInitial = new ArrayList<>();
    // ArrayList<Occasion> mOccasionListReal = new ArrayList<>();

    ArrayList<Occasion> mOccasionAll = new ArrayList<>();
    ArrayList<Occasion> mOccasionEvents = new ArrayList<>();
    ArrayList<Occasion> mOccasionJios = new ArrayList<>();
    ArrayList<String> mEventIDs = new ArrayList<>();
    ArrayList<String> mJioIDs = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MylistAdapter mAdapter;
    private View rootView;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReferenceEvents;
    private DatabaseReference mDatabaseReferenceJios;
    private DatabaseReference mDatabaseReferenceActivityEvent;
    private DatabaseReference mDatabaseReferenceActivityJio;
    private DatabaseReference mDatabaseReferenceUser;

    ValueEventListener mValueEventListenerEvents;
    ValueEventListener mValueEventListenerJios;

    private ImageButton editUserInfoBtn;

    private StorageReference mProfileStorageRef;

    private UserItem user;
    private String residence_name;

    private TextView name;
    private TextView resi;
    private ImageView profilePic;
    private String fbUID;

    private static boolean refreshUserDetails;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mylist, container, false);
        name = rootView.findViewById(R.id.user_display_name);
        resi = rootView.findViewById(R.id.user_display_resi);
        profilePic = rootView.findViewById(R.id.user_display_picture);

        final String fbUIDFinal = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fbUID = fbUIDFinal;

        if (MainActivity.display_name != null) {
            name.setText(MainActivity.display_name);
        }
        if (MainActivity.display_residential != null) {
            resi.setText(MainActivity.display_residential);
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        initializeUserDetails();

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

        mDatabaseReferenceEvents = mFirebaseDatabase.getReference().child("Events");
        mDatabaseReferenceJios = mFirebaseDatabase.getReference().child("Jios");

        mOccasionAll.clear();

        mDatabaseReferenceEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mOccasionEvents.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Occasion selected = snapshot.getValue(EventsItem.class);
                    String eventID = selected.getOccasionID();
                    if (mEventIDs.contains(eventID)) {
                        if (selected.getTimeInfo().length() > 4) {
                            continue;
                        }

                        int hour = Integer.parseInt(selected.getTimeInfo().substring(0,2));
                        int min = Integer.parseInt(selected.getTimeInfo().substring(2));

                        Date eventDateZero = selected.getDateInfo();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(eventDateZero);
                        cal.set(Calendar.HOUR_OF_DAY, hour);
                        cal.set(Calendar.MINUTE, min);
                        Date eventDate = cal.getTime();


                        Date currentDate = new Date();
                        if (eventDate.compareTo(currentDate) >= 0) {
                            mOccasionEvents.add(selected);
                        }
                    }
                }
                mOccasionEvents.addAll(mOccasionJios);
                mOccasionAll = mOccasionEvents;
                MainActivity.sort(mOccasionAll);
                MylistAdapter myListAdapter = new MylistAdapter(mOccasionAll);
                mAdapter = myListAdapter;
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseReferenceJios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mOccasionJios.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Occasion selected = snapshot.getValue(JiosItem.class);
                    String jioID = selected.getOccasionID();
                    if (mJioIDs.contains(jioID)) {
                        if (selected.getTimeInfo().length() > 4) {
                            continue;
                        }

                        int hour = Integer.parseInt(selected.getTimeInfo().substring(0,2));
                        int min = Integer.parseInt(selected.getTimeInfo().substring(2));

                        Date eventDateZero = selected.getDateInfo();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(eventDateZero);
                        cal.set(Calendar.HOUR_OF_DAY, hour);
                        cal.set(Calendar.MINUTE, min);
                        Date eventDate = cal.getTime();


                        Date currentDate = new Date();
                        if (eventDate.compareTo(currentDate) >= 0) {
                            mOccasionJios.add(selected);
                        }
                    }
                }
                mOccasionJios.addAll(mOccasionEvents);
                mOccasionAll = mOccasionJios;

                MainActivity.sort(mOccasionAll);

                MylistAdapter myListAdapter = new MylistAdapter(mOccasionAll);
                mAdapter = myListAdapter;
                mRecyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // setting up toolbar
        setHasOptionsMenu(true);
        Toolbar toolbar = rootView.findViewById(R.id.mylist_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.setSupportActionBar(toolbar);

        buildRecyclerView();
        return rootView;
    }

    public void initializeUserDetails() {
        final String fbUIDFinal = fbUID;
        final TextView nameFinal = name;
        final TextView resiFinal = resi;

        mProfileStorageRef = FirebaseStorage.getInstance().getReference("profile picture uploads");
        mProfileStorageRef = mProfileStorageRef.child(fbUID);

        mProfileStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profilePic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                profilePic.setImageResource(R.drawable.fake_user_dp);
            }
        });

        // Pulling user from Firebase
        mDatabaseReferenceUser = mFirebaseDatabase.getReference().child("Users");
        mDatabaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserItem selected = snapshot.getValue(UserItem.class);
                    String id = selected.getId();

                    if (fbUIDFinal.equals(id)) {
                        user = selected;
                        String disp_name = user.getName();
                        MainActivity.display_name = disp_name;
                        nameFinal.setText(disp_name);

                        intToRes(user.getResidential());
                        resiFinal.setText(residence_name);
                        MainActivity.display_residential = residence_name;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        editUserInfoBtn = (ImageButton) getView().findViewById(R.id.edit_user_info_btn);

        editUserInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditUserInfoActivity.class);
                startActivity(intent);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    public void intToRes(int x) {
        if (x == 0) {
            residence_name = "Off Campus";
        }
        if (x == 1) {
            residence_name = "Cinnamon";
        }
        if (x == 2) {
            residence_name = "Tembusu";
        }
        if (x == 3) {
            residence_name = "CAPT";
        }
        if (x == 4) {
            residence_name = "RC4";
        }
        if (x == 5) {
            residence_name = "RVRC";
        }
        if (x == 6) {
            residence_name = "Eusoff";
        }
        if (x == 7) {
            residence_name = "Kent Ridge";
        }
        if (x == 8) {
            residence_name = "King Edward VII";
        }
        if (x == 9) {
            residence_name = "Raffles";
        }
        if (x == 10) {
            residence_name = "Sheares";
        }
        if (x == 11) {
            residence_name = "Temasek";
        }
        if (x == 12) {
            residence_name = "PGP House";
        }
        if (x == 13) {
            residence_name = "PGP Residences";
        }
        if (x == 14) {
            residence_name = "UTown Residence";
        }

    }

    public void buildRecyclerView() {
        mRecyclerView = rootView.findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new MylistAdapter(mOccasionAll);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

//    @Override
//    public void onResume() {
//        if (refresh) {
//            refresh = false;
//            getActivity().getSupportFragmentManager().beginTransaction()
//                    .detach(MylistFragment.this)
//                    .attach(MylistFragment.this)
//                    .commit();
//        }
//        super.onResume();
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_search:

                MenuItem searchItem = item;
                SearchView searchView = (SearchView) searchItem.getActionView();
                // searchView.setQueryHint("Search");
                // searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                searchItem.setActionView(searchView);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        mAdapter.getFilter().filter(newText);
                        return false;
                    }
                });

                break;
            case R.id.action_createdoccasions:
                CreatedFragment nextFrag= new CreatedFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, nextFrag)
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.action_history:
                HistoryFragment nextFrag2= new HistoryFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, nextFrag2)
                        .addToBackStack(null)
                        .commit();
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.mylist_top_menu, menu);

        // setting the search function UI
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search");

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // reset list
                mAdapter = mAdapter.resetAdapter();
                mRecyclerView.setAdapter(mAdapter);
                return true;
            }
        });

        // ???
        // searchItem.setOnMenuItemClickListener()

        super.onCreateOptionsMenu(menu, inflater);
    }

    public static void setRefreshUserDetails(boolean toSet) {
        refreshUserDetails = toSet;
    }

    @Override
    public void onResume() {
        if (refreshUserDetails) {
            initializeUserDetails();
        }
        super.onResume();
    }
}
