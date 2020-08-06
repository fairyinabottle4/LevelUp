package com.example.LevelUp.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Dashboard.LevelUp.ui.dashboard.DashboardAdapter;
import com.Dashboard.LevelUp.ui.dashboard.DashboardSettingsActivity;
import com.Events.LevelUp.ui.events.EventsItem;
import com.Jios.LevelUp.ui.jios.JiosItem;
import com.MainActivity;
import com.example.LevelUp.ui.Occasion;
import com.example.tryone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class DashboardFragment extends Fragment {
    private View rootView;

    private RecyclerView mRecyclerViewTrending;
    private LinearLayoutManager mLayoutManagerTrending;
    private DashboardAdapter mAdapterTrending;

    private RecyclerView mRecyclerViewToday;
    private LinearLayoutManager mLayoutManagerToday;
    private DashboardAdapter mAdapterToday;

    private RecyclerView mRecyclerViewNewlyCreated;
    private LinearLayoutManager mLayoutManagerNewlyCreated;
    private DashboardAdapter mAdapterNewlyCreated;

    ArrayList<Occasion> mOccasionAll = new ArrayList<>();
    ArrayList<Occasion> mOccasionTrending = new ArrayList<>();
    ArrayList<Occasion> mOccasionToday = new ArrayList<>();
    ArrayList<Occasion> mNewlyCreatedJios = new ArrayList<>();
    ArrayList<Occasion> mNewlyCreatedEvents = new ArrayList<>();
    ArrayList<Occasion> mOccasionNewlyCreated = new ArrayList<>();

    ArrayList<Occasion> mOccasionEvents = new ArrayList<>();
    ArrayList<Occasion> mOccasionJios = new ArrayList<>();
    ArrayList<String> mEventIDs = new ArrayList<>();
    ArrayList<String> mJioIDs = new ArrayList<>();

    private DatabaseReference mDatabaseReferenceEvents;
    private DatabaseReference mDatabaseReferenceJios;
    private FirebaseDatabase mFirebaseDatabase;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // setting up toolbar
        setHasOptionsMenu(true);
        Toolbar toolbar = rootView.findViewById(R.id.dashboard_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.setSupportActionBar(toolbar);

        buildTrendingRecyclerView();
        buildTodayRecyclerView();
        buildNewlyCreatedRecyclerView();

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        initializeListTrending();
        initializeListToday();
        initializeListNewlyCreated();


        return rootView;
    }

    public void buildTrendingRecyclerView() {
        mRecyclerViewTrending = rootView.findViewById(R.id.trending);
        mLayoutManagerTrending = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
//                mLayoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapterTrending = new DashboardAdapter(getActivity(), mOccasionTrending);
        mRecyclerViewTrending.setLayoutManager(mLayoutManagerTrending);
        mRecyclerViewTrending.setAdapter(mAdapterTrending);
        mRecyclerViewTrending.setItemAnimator(new DefaultItemAnimator());
    }

    public void buildTodayRecyclerView() {
        mRecyclerViewToday = rootView.findViewById(R.id.today);
        mLayoutManagerToday = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
//                mLayoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapterToday = new DashboardAdapter(getActivity(), mOccasionToday);
        mRecyclerViewToday.setLayoutManager(mLayoutManagerToday);
        mRecyclerViewToday.setAdapter(mAdapterToday);
        mRecyclerViewToday.setItemAnimator(new DefaultItemAnimator());
    }

    public void buildNewlyCreatedRecyclerView() {
        mRecyclerViewNewlyCreated = rootView.findViewById(R.id.newOccasions);
        mLayoutManagerNewlyCreated = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
//                mLayoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapterNewlyCreated = new DashboardAdapter(getActivity(), mOccasionNewlyCreated);
        mRecyclerViewNewlyCreated.setLayoutManager(mLayoutManagerNewlyCreated);
        mRecyclerViewNewlyCreated.setAdapter(mAdapterNewlyCreated);
        mRecyclerViewNewlyCreated.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_dashboard_settings:
                Intent intent = new Intent(getActivity(), DashboardSettingsActivity.class);
                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.dashboard_top_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void initializeListTrending() {
        final String fbUIDFinal = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabaseReferenceEvents = mFirebaseDatabase.getReference().child("Events");
        mDatabaseReferenceJios = mFirebaseDatabase.getReference().child("Jios");

        mDatabaseReferenceEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mOccasionEvents.clear();
                mOccasionAll.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Occasion selected = snapshot.getValue(EventsItem.class);
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

                ArrayList<Occasion> copyOfFullEvents = new ArrayList<>(mOccasionEvents);
                copyOfFullEvents.addAll(mOccasionJios);
                mOccasionAll = copyOfFullEvents;

                MainActivity.sort(mOccasionAll);

                // At this point, mOccasionAll will have full list of Occasions
                // Sort by likes then take 1st 5

                // LOGIC GOES FROM HERE

//                Collections.sort(mOccasionAll, new Comparator<Occasion>(){
//                    public int compare(Occasion s1,Occasion s2) {
//                        return s2.getNumLikes() - s1.getNumLikes();
//                }});
//
//                ArrayList<Occasion> topFive = new ArrayList<>();
//                for (int i = 0; i < 5; i++) {
//                    topFive.add(mOccasionAll.get(i));
//                }

//                DashboardAdapter dashboardAdapter = new DashboardAdapter(getActivity(), topFive);
//                mAdapterTrending = dashboardAdapter;
//                mAdapterToday = dashboardAdapter;
//                mAdapterNewOcc = dashboardAdapter;

                mRecyclerViewTrending.setAdapter(mAdapterTrending);

                // TO HERE
//                mRecyclerViewToday.setAdapter(mAdapterToday);
//                mRecyclerViewNewOcc.setAdapter(mAdapterNewOcc);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseReferenceJios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mOccasionJios.clear();
                mOccasionAll.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Occasion selected = snapshot.getValue(JiosItem.class);
                    String jioID = selected.getOccasionID();
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

                ArrayList<Occasion> copyOfFullJios = new ArrayList<>(mOccasionJios);
                copyOfFullJios.addAll(mOccasionEvents);

                mOccasionAll = copyOfFullJios;

                MainActivity.sort(mOccasionAll);

                Collections.sort(mOccasionAll, new Comparator<Occasion>(){
                    public int compare(Occasion s1,Occasion s2) {
                        return s2.getNumLikes() - s1.getNumLikes();
                    }});

                ArrayList<Occasion> topFive = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    topFive.add(mOccasionAll.get(i));
                }

                DashboardAdapter dashboardAdapter = new DashboardAdapter(getActivity(), topFive);
                mAdapterTrending = dashboardAdapter;
//                mAdapterToday = dashboardAdapter;
//                mAdapterNewOcc = dashboardAdapter;

                mRecyclerViewTrending.setAdapter(mAdapterTrending);
//                mRecyclerViewToday.setAdapter(mAdapterToday);
//                mRecyclerViewNewOcc.setAdapter(mAdapterNewOcc);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void initializeListToday() {
        final String fbUIDFinal = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabaseReferenceEvents = mFirebaseDatabase.getReference().child("Events");
        mDatabaseReferenceJios = mFirebaseDatabase.getReference().child("Jios");

        mDatabaseReferenceEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mOccasionEvents.clear();
                mOccasionAll.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Occasion selected = snapshot.getValue(EventsItem.class);
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

                ArrayList<Occasion> copyOfFullEvents = new ArrayList<>(mOccasionEvents);
                copyOfFullEvents.addAll(mOccasionJios);
                mOccasionAll = copyOfFullEvents;

                MainActivity.sort(mOccasionAll);

                // At this point, mOccasionAll will have full list of Occasions

                // LOGIC GOES FROM HERE
                // get Today's date but set time to 0
                // get all Occasions that are compareTo returns 0

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date currentDate = cal.getTime();

                ArrayList<Occasion> todayOcc = new ArrayList<>();

                for (Occasion occ : mOccasionAll) {
                    if (occ.getDateInfo().compareTo(currentDate) == 0) {
                        todayOcc.add(occ);
                    }
                }


                DashboardAdapter dashboardAdapter = new DashboardAdapter(getActivity(), todayOcc);
                mAdapterToday = dashboardAdapter;
//                mAdapterToday = dashboardAdapter;
//                mAdapterNewOcc = dashboardAdapter;

                mRecyclerViewToday.setAdapter(mAdapterToday);

                // TO HERE
//                mRecyclerViewToday.setAdapter(mAdapterToday);
//                mRecyclerViewNewOcc.setAdapter(mAdapterNewOcc);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseReferenceJios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mOccasionJios.clear();
                mOccasionAll.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Occasion selected = snapshot.getValue(JiosItem.class);
                    String jioID = selected.getOccasionID();
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

                ArrayList<Occasion> copyOfFullJios = new ArrayList<>(mOccasionJios);
                copyOfFullJios.addAll(mOccasionEvents);

                mOccasionAll = copyOfFullJios;

                MainActivity.sort(mOccasionAll);

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date currentDate = cal.getTime();

                ArrayList<Occasion> todayOcc = new ArrayList<>();

                for (Occasion occ : mOccasionAll) {
                    if (occ.getDateInfo().compareTo(currentDate) == 0) {
                        todayOcc.add(occ);
                    }
                }


                DashboardAdapter dashboardAdapter = new DashboardAdapter(getActivity(), todayOcc);
                mAdapterToday = dashboardAdapter;
//                mAdapterToday = dashboardAdapter;
//                mAdapterNewOcc = dashboardAdapter;

                mRecyclerViewToday.setAdapter(mAdapterToday);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void initializeListNewlyCreated() {
        final String fbUIDFinal = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabaseReferenceEvents = mFirebaseDatabase.getReference().child("Events");
        mDatabaseReferenceJios = mFirebaseDatabase.getReference().child("Jios");

        mDatabaseReferenceEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mOccasionEvents.clear();
                mOccasionAll.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Occasion selected = snapshot.getValue(EventsItem.class);
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

                //add into mNewlyCreatedEvents
                for (int i = mOccasionEvents.size() - 1; i > mOccasionEvents.size() - 4; i--) {
                    mNewlyCreatedEvents.add(mOccasionEvents.get(i));
                }

                ArrayList<Occasion> copyOfFullEvents = new ArrayList<>(mOccasionEvents);
                copyOfFullEvents.addAll(mOccasionJios);
                mOccasionAll = copyOfFullEvents;

                MainActivity.sort(mOccasionAll);

                // At this point, mOccasionAll will have full list of Occasions

                // LOGIC GOES FROM HERE
                // get Today's date but set time to 0
                // get all Occasions that are compareTo returns 0

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date currentDate = cal.getTime();
                mRecyclerViewToday.setAdapter(mAdapterToday);

                // TO HERE
//                mRecyclerViewToday.setAdapter(mAdapterToday);
//                mRecyclerViewNewOcc.setAdapter(mAdapterNewOcc);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseReferenceJios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mOccasionJios.clear();
                mOccasionAll.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Occasion selected = snapshot.getValue(JiosItem.class);
                    String jioID = selected.getOccasionID();
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

                //add into mNewlyCreatedJios
                for (int i = mOccasionJios.size() - 1; i > mOccasionEvents.size() - 4; i--) {
                    mNewlyCreatedJios.add(mOccasionJios.get(i));
                }

                ArrayList<Occasion> copyOfFullJios = new ArrayList<>(mOccasionJios);
                copyOfFullJios.addAll(mOccasionEvents);

                mOccasionAll = copyOfFullJios;

                MainActivity.sort(mOccasionAll);

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date currentDate = cal.getTime();

                //logic for creating Newly Added
                mOccasionNewlyCreated.addAll(mNewlyCreatedEvents);
                mOccasionNewlyCreated.addAll(mNewlyCreatedJios);
                MainActivity.sort(mOccasionNewlyCreated);
                DashboardAdapter dashboardAdapter = new DashboardAdapter(getActivity(), mOccasionNewlyCreated);
                mAdapterNewlyCreated = dashboardAdapter;
                mRecyclerViewNewlyCreated.setAdapter(mAdapterNewlyCreated);

//                mAdapterNewOcc = dashboardAdapter;
//                DashboardAdapter dashboardAdapter = new DashboardAdapter(getActivity(), topFive);
//                mAdapterTrending = dashboardAdapter;
//                mAdapterToday = dashboardAdapter;
//                mAdapterNewOcc = dashboardAdapter;

//                mRecyclerViewTrending.setAdapter(mAdapterTrending);
//                mRecyclerViewToday.setAdapter(mAdapterToday);
//                mRecyclerViewNewOcc.setAdapter(mAdapterNewOcc);





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
