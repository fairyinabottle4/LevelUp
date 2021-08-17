package com.example.LevelUp.ui.mylist;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.ActivityOccasionItem;
import com.Events.LevelUp.ui.events.EventsItem;
import com.Jios.LevelUp.ui.jios.JiosItem;
import com.MainActivity;
import com.Mylist.LevelUp.ui.mylist.CreatedFragment;
import com.Mylist.LevelUp.ui.mylist.EditUserInfoActivity;
import com.Mylist.LevelUp.ui.mylist.HistoryFragment;
import com.Mylist.LevelUp.ui.mylist.MylistAdapter;
import com.UserItem;
import com.bumptech.glide.Glide;
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

public class MylistFragment extends Fragment {

    private static boolean refreshList;
    private static boolean refreshUserDetails;

    private ArrayList<Occasion> occasionAll = new ArrayList<>();
    private ArrayList<Occasion> occasionEvents = new ArrayList<>();
    private ArrayList<Occasion> occasionJios = new ArrayList<>();
    private ArrayList<String> eventIDs = new ArrayList<>();
    private ArrayList<String> jioIDs = new ArrayList<>();

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MylistAdapter adapter;
    private View rootView;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReferenceEvents;
    private DatabaseReference databaseReferenceJios;
    private DatabaseReference databaseReferenceActivityEvent;
    private DatabaseReference databaseReferenceActivityJio;
    private DatabaseReference databaseReferenceUser;

    private ImageButton editUserInfoBtn;

    private StorageReference profileStorageRef;

    private UserItem user;
    private String residenceName;

    private TextView name;
    private TextView residence;
    private ImageView profilePic;
    private String firebaseUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mylist, container, false);
        name = rootView.findViewById(R.id.user_display_name);
        residence = rootView.findViewById(R.id.user_display_resi);
        profilePic = rootView.findViewById(R.id.user_display_picture);

        firebaseUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (MainActivity.getDisplayName() != null) {
            name.setText(MainActivity.getDisplayName());
        }
        if (MainActivity.getDisplayResidential() != null) {
            residence.setText(MainActivity.getDisplayResidential());
        }

        firebaseDatabase = FirebaseDatabase.getInstance();

        buildRecyclerView();

        initializeUserDetails();

        initializeList();

        // setting up toolbar
        setHasOptionsMenu(true);
        Toolbar toolbar = rootView.findViewById(R.id.mylist_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.setSupportActionBar(toolbar);

        return rootView;
    }

    /**
     * Method to initialize the list that will be contained within the Fragment. It will pull data
     * from the database for both Events and Jios.
     */
    public void initializeList() {
        eventIDs.clear();
        jioIDs.clear();
        final String firebaseUserIdFinal = firebaseUserId;
        databaseReferenceActivityEvent = firebaseDatabase.getReference().child("ActivityEvent");
        databaseReferenceActivityEvent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                        String selectedUserID = selected.getUserID();
                        if (selectedUserID.equals(firebaseUserIdFinal)) {
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
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                        String selectedUserID = selected.getUserID();
                        if (selectedUserID.equals(firebaseUserIdFinal)) {
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

        databaseReferenceEvents = firebaseDatabase.getReference().child("Events");
        databaseReferenceJios = firebaseDatabase.getReference().child("Jios");

        databaseReferenceEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                occasionEvents.clear();
                occasionAll.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Occasion selected = snapshot.getValue(EventsItem.class);
                        String eventID = selected.getOccasionID();
                        if (eventIDs.contains(eventID)) {
                            if (selected.getTimeInfo().length() > 4) {
                                continue;
                            }

                            int hour = Integer.parseInt(selected.getTimeInfo().substring(0, 2));
                            int min = Integer.parseInt(selected.getTimeInfo().substring(2));

                            Date eventDateZero = selected.getDateInfo();
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(eventDateZero);
                            cal.set(Calendar.HOUR_OF_DAY, hour);
                            cal.set(Calendar.MINUTE, min);
                            Date eventDate = cal.getTime();


                            Date currentDate = new Date();
                            if (eventDate.compareTo(currentDate) >= 0) {
                                occasionEvents.add(selected);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }

                ArrayList<Occasion> copyOfFullEvents = new ArrayList<>(occasionEvents);
                copyOfFullEvents.addAll(occasionJios);
                occasionAll = copyOfFullEvents;

                MainActivity.sort(occasionAll);

                MylistAdapter myListAdapter = new MylistAdapter(getActivity(), occasionAll);
                adapter = myListAdapter;
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReferenceJios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                occasionJios.clear();
                occasionAll.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Occasion selected = snapshot.getValue(JiosItem.class);
                        String jioID = selected.getOccasionID();
                        if (jioIDs.contains(jioID)) {
                            if (selected.getTimeInfo().length() > 4) {
                                continue;
                            }

                            int hour = Integer.parseInt(selected.getTimeInfo().substring(0, 2));
                            int min = Integer.parseInt(selected.getTimeInfo().substring(2));

                            Date eventDateZero = selected.getDateInfo();
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(eventDateZero);
                            cal.set(Calendar.HOUR_OF_DAY, hour);
                            cal.set(Calendar.MINUTE, min);
                            Date eventDate = cal.getTime();


                            Date currentDate = new Date();
                            if (eventDate.compareTo(currentDate) >= 0) {
                                occasionJios.add(selected);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
                ArrayList<Occasion> copyOfFullJios = new ArrayList<>(occasionJios);
                copyOfFullJios.addAll(occasionEvents);

                occasionAll = copyOfFullJios;

                MainActivity.sort(occasionAll);

                MylistAdapter myListAdapter = new MylistAdapter(getActivity(), occasionAll);
                adapter = myListAdapter;
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        adapter.notifyDataSetChanged();
    }

    /**
     * Initialize the details of the user logged in to the app. Data will be pulled from the database
     */
    public void initializeUserDetails() {
        final String firebaseUserIdFinal = firebaseUserId;
        final TextView nameFinal = name;
        final TextView resiFinal = residence;

        profileStorageRef = FirebaseStorage.getInstance().getReference("profile picture uploads");
        profileStorageRef = profileStorageRef.child(firebaseUserId);

        profileStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext()).load(uri).into(profilePic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                profilePic.setImageResource(R.drawable.fake_user_dp);
            }
        });

        // Pulling user from Firebase
        databaseReferenceUser = firebaseDatabase.getReference().child("Users");
        databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        UserItem selected = snapshot.getValue(UserItem.class);
                        String id = selected.getId();

                        if (firebaseUserIdFinal.equals(id)) {
                            user = selected;
                            String displayName = user.getName();
                            MainActivity.setDisplayName(displayName);
                            nameFinal.setText(displayName);

                            intToRes(user.getResidential());
                            resiFinal.setText(residenceName);
                            MainActivity.setDisplayResidential(residenceName);

                            MainActivity.setDisplayPhone(user.getPhone());
                            MainActivity.setDisplayTelegram(user.getTelegram());
                        }
                    } catch (Exception e) {
                        System.out.print(e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        buildRecyclerView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        editUserInfoBtn = (ImageButton) getView().findViewById(R.id.edit_user_info_btn);

        editUserInfoBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditUserInfoActivity.class);
            startActivity(intent);
        });

        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Sets the residence name based on the pre-coded number
     *
     * @param x The number representing each residence
     */
    public void intToRes(int x) {
        if (x == 0) {
            residenceName = "Off Campus";
        }
        if (x == 1) {
            residenceName = "Cinnamon";
        }
        if (x == 2) {
            residenceName = "Tembusu";
        }
        if (x == 3) {
            residenceName = "CAPT";
        }
        if (x == 4) {
            residenceName = "RC4";
        }
        if (x == 5) {
            residenceName = "RVRC";
        }
        if (x == 6) {
            residenceName = "Eusoff";
        }
        if (x == 7) {
            residenceName = "Kent Ridge";
        }
        if (x == 8) {
            residenceName = "King Edward VII";
        }
        if (x == 9) {
            residenceName = "Raffles";
        }
        if (x == 10) {
            residenceName = "Sheares";
        }
        if (x == 11) {
            residenceName = "Temasek";
        }
        if (x == 12) {
            residenceName = "PGP House";
        }
        if (x == 13) {
            residenceName = "PGP Residences";
        }
        if (x == 14) {
            residenceName = "UTown Residence";
        }

    }

    /**
     * Builds the recycler view to display a scrolling list of all the Jios and Events that
     * the user has registered for
     */
    public void buildRecyclerView() {
        recyclerView = rootView.findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new MylistAdapter(getActivity(), occasionAll);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
        case R.id.action_search:

            MenuItem searchItem = item;
            SearchView searchView = (SearchView) searchItem.getActionView();
            searchItem.setActionView(searchView);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.getFilter().filter(newText);
                    return false;
                }
            });

            break;
        case R.id.action_createdoccasions:
            CreatedFragment nextFrag = new CreatedFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, nextFrag)
                    .addToBackStack(null)
                    .commit();
            break;

        case R.id.action_history:
            HistoryFragment nextFrag2 = new HistoryFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, nextFrag2)
                    .addToBackStack(null)
                    .commit();
            break;
        default:

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
                adapter = adapter.resetAdapter();
                recyclerView.setAdapter(adapter);
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    public static void setRefreshUserDetails(boolean toSet) {
        refreshUserDetails = toSet;
    }

    public static void setRefreshList(boolean refreshList) {
        MylistFragment.refreshList = refreshList;
    }

    @Override
    public void onResume() {
        if (refreshUserDetails) {
            initializeUserDetails();
            refreshUserDetails = false;
        }
        if (refreshList) {
            initializeList();
            refreshList = false;
        }
        super.onResume();
    }
}
