package com.example.LevelUp.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

    public static boolean refresh;

    private RecyclerView recyclerViewTrending;
    private LinearLayoutManager layoutManagerTrending;
    private DashboardAdapter adapterTrending;

    private RecyclerView recyclerViewToday;
    private LinearLayoutManager layoutManagerToday;
    private DashboardAdapter adapterToday;

    private RecyclerView recyclerViewNewlyCreated;
    private LinearLayoutManager layoutManagerNewlyCreated;
    private DashboardAdapter adapterNewlyCreated;

    ArrayList<Occasion> occasionAll = new ArrayList<>();
    ArrayList<Occasion> occastionTrending = new ArrayList<>();
    ArrayList<Occasion> occasionToday = new ArrayList<>();
    ArrayList<Occasion> newlyCreatedJios = new ArrayList<>();
    ArrayList<Occasion> newlyCreatedEvents = new ArrayList<>();
    ArrayList<Occasion> occasionNewlyCreated = new ArrayList<>();

    ArrayList<Occasion> occasionEvents = new ArrayList<>();
    ArrayList<Occasion> occasionJios = new ArrayList<>();
    ArrayList<String> mEventIDs = new ArrayList<>();
    ArrayList<String> mJioIDs = new ArrayList<>();

    private DatabaseReference databaseReferenceEvents;
    private DatabaseReference databaseReferenceJios;
    private FirebaseDatabase firebaseDatabase;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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

        firebaseDatabase = FirebaseDatabase.getInstance();

        initializeListTrending();
        initializeListToday();
        initializeListNewlyCreated();

        return rootView;
    }

    public void buildTrendingRecyclerView() {
        recyclerViewTrending = rootView.findViewById(R.id.trending);
        layoutManagerTrending = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
//                mLayoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);

        adapterTrending = new DashboardAdapter(getActivity(), occastionTrending);
        recyclerViewTrending.setLayoutManager(layoutManagerTrending);
        recyclerViewTrending.setAdapter(adapterTrending);
        recyclerViewTrending.setItemAnimator(new DefaultItemAnimator());
    }

    public void buildTodayRecyclerView() {
        recyclerViewToday = rootView.findViewById(R.id.today);
        layoutManagerToday = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
//                mLayoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);

        adapterToday = new DashboardAdapter(getActivity(), occasionToday);
        recyclerViewToday.setLayoutManager(layoutManagerToday);
        recyclerViewToday.setAdapter(adapterToday);
        recyclerViewToday.setItemAnimator(new DefaultItemAnimator());
    }

    public void buildNewlyCreatedRecyclerView() {
        recyclerViewNewlyCreated = rootView.findViewById(R.id.newOccasions);
        layoutManagerNewlyCreated = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
//                mLayoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);

        adapterNewlyCreated = new DashboardAdapter(getActivity(), occasionNewlyCreated);
        recyclerViewNewlyCreated.setLayoutManager(layoutManagerNewlyCreated);
        recyclerViewNewlyCreated.setAdapter(adapterNewlyCreated);
        recyclerViewNewlyCreated.setItemAnimator(new DefaultItemAnimator());
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
        databaseReferenceEvents = firebaseDatabase.getReference().child("Events");
        databaseReferenceJios = firebaseDatabase.getReference().child("Jios");

        databaseReferenceEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                occasionEvents.clear();
                occasionAll.clear();
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
                        occasionEvents.add(selected);
                    }
                }

                ArrayList<Occasion> copyOfFullEvents = new ArrayList<>(occasionEvents);
                copyOfFullEvents.addAll(occasionJios);
                occasionAll = copyOfFullEvents;
                MainActivity.sort(occasionAll);
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
                        occasionJios.add(selected);
                    }
                }

                ArrayList<Occasion> copyOfFullJios = new ArrayList<>(occasionJios);
                copyOfFullJios.addAll(occasionEvents);

                occasionAll = copyOfFullJios;

                MainActivity.sort(occasionAll);

                // At this point, occasionAll will have full list of Occasions
                // Sort by likes then take 1st 5

                // LOGIC GOES FROM HERE

                Collections.sort(occasionAll, new Comparator<Occasion>(){
                    public int compare(Occasion s1,Occasion s2) {
                        return s2.getNumLikes() - s1.getNumLikes();
                    }});

                ArrayList<Occasion> topFive = new ArrayList<>();
                if (occasionAll.size() > 4) {
                    for (int i = 0; i < 5; i++) {
                        topFive.add(occasionAll.get(i));
                    }
                } else {
                    topFive.addAll(occasionAll);
                }
                if (topFive.size() == 0) {
                    TextView t = rootView.findViewById(R.id.today_textView);
                    t.setText("\n" + "There is nothing here at the moment :(" + "\n");
                }

                DashboardAdapter dashboardAdapter = new DashboardAdapter(getActivity(), topFive);
                adapterTrending = dashboardAdapter;
                recyclerViewTrending.setAdapter(adapterTrending);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void initializeListToday() {

        databaseReferenceEvents = firebaseDatabase.getReference().child("Events");
        databaseReferenceJios = firebaseDatabase.getReference().child("Jios");

        databaseReferenceEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                occasionEvents.clear();
                occasionAll.clear();
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
                        occasionEvents.add(selected);
                    }
                }

                ArrayList<Occasion> copyOfFullEvents = new ArrayList<>(occasionEvents);
                copyOfFullEvents.addAll(occasionJios);
                occasionAll = copyOfFullEvents;
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
                        occasionJios.add(selected);
                    }
                }

                ArrayList<Occasion> copyOfFullJios = new ArrayList<>(occasionJios);
                copyOfFullJios.addAll(occasionEvents);

                occasionAll = copyOfFullJios;

                // At this point, occasionAll will have full list of Occasions
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

                for (Occasion occ : occasionAll) {
                    if (occ.getDateInfo().compareTo(currentDate) == 0) {
                        todayOcc.add(occ);
                    }
                }
                MainActivity.sort(todayOcc);

                if (todayOcc.size() == 0) {
                    TextView t = rootView.findViewById(R.id.today_textView);
                    t.setText("\n" + "There is nothing happening today :(" + "\n");
                }

                DashboardAdapter dashboardAdapter = new DashboardAdapter(getActivity(), todayOcc);
                adapterToday = dashboardAdapter;
                recyclerViewToday.setAdapter(adapterToday);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void initializeListNewlyCreated() {

        databaseReferenceEvents = firebaseDatabase.getReference().child("Events");
        databaseReferenceJios = firebaseDatabase.getReference().child("Jios");

        databaseReferenceEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                occasionEvents.clear();
                occasionAll.clear();
                newlyCreatedJios.clear();
                newlyCreatedEvents.clear();
                occasionNewlyCreated.clear();
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
                        occasionEvents.add(selected);
                    }
                }

                //add into newlyCreatedEvents
                if (occasionEvents.size() > 3) {
                    for (int i = occasionEvents.size() - 1; i > occasionEvents.size() - 4; i--) {
                        newlyCreatedEvents.add(occasionEvents.get(i));
                    }
                } else {
                    newlyCreatedEvents.addAll(occasionEvents);
                }
                occasionNewlyCreated.addAll(newlyCreatedEvents);
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
                newlyCreatedJios.clear();
                newlyCreatedEvents.clear();
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
                        occasionJios.add(selected);
                    }
                }
                //add into newlyCreatedJios
                if (occasionJios.size() > 3) {
                    for (int i = occasionJios.size() - 1; i > occasionJios.size() - 4; i--) {
                        newlyCreatedJios.add(occasionJios.get(i));
                    }
                } else {
                    newlyCreatedJios.addAll(occasionJios);
                }
                //logic for creating Newly Added
                occasionNewlyCreated.addAll(newlyCreatedJios);
                MainActivity.sort(occasionNewlyCreated);
                if (occasionNewlyCreated.size() == 0) {
                    TextView t = rootView.findViewById(R.id.today_textView);
                    t.setText("\n" + "There is nothing here at the moment :(" + "\n");
                }
                DashboardAdapter dashboardAdapter = new DashboardAdapter(getActivity(), occasionNewlyCreated);
                adapterNewlyCreated = dashboardAdapter;
                recyclerViewNewlyCreated.setAdapter(adapterNewlyCreated);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void setRefresh(boolean toSet) {
        refresh = toSet;
    }

    @Override
    public void onResume() {
        if (refresh) {
            initializeListTrending();
            initializeListToday();
            initializeListNewlyCreated();
            refresh = false;
        }
        super.onResume();
    }
}
