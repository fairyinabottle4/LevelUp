package com.example.LevelUp.ui.dashboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import com.Dashboard.LevelUp.ui.dashboard.DashboardAdapter;
import com.Dashboard.LevelUp.ui.dashboard.DashboardSettingsActivity;
import com.Events.LevelUp.ui.events.EventsItem;
import com.Jios.LevelUp.ui.jios.JiosItem;
import com.MainActivity;
import com.example.LevelUp.ui.Occasion;
import com.example.tryone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DashboardFragment extends Fragment {

    private static boolean refresh;
    private View rootView;
    private RecyclerView recyclerViewTrending;
    private LinearLayoutManager layoutManagerTrending;
    private DashboardAdapter adapterTrending;

    private RecyclerView recyclerViewToday;
    private LinearLayoutManager layoutManagerToday;
    private DashboardAdapter adapterToday;

    private RecyclerView recyclerViewNewlyCreated;
    private LinearLayoutManager layoutManagerNewlyCreated;
    private DashboardAdapter adapterNewlyCreated;

    private ArrayList<Occasion> occasionAll = new ArrayList<>();
    private ArrayList<Occasion> occastionTrending = new ArrayList<>();
    private ArrayList<Occasion> occasionToday = new ArrayList<>();
    private ArrayList<Occasion> newlyCreatedJios = new ArrayList<>();
    private ArrayList<Occasion> newlyCreatedEvents = new ArrayList<>();
    private ArrayList<Occasion> occasionNewlyCreated = new ArrayList<>();

    private ArrayList<Occasion> occasionEvents = new ArrayList<>();
    private ArrayList<Occasion> occasionJios = new ArrayList<>();
    private ArrayList<String> mEventIDs = new ArrayList<>();
    private ArrayList<String> mJioIDs = new ArrayList<>();

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

    /**
     * Build a list of items that have received the most likes, with a maximum of 6, 3 each from
     * Jios and Events displayed with a horizontal scrolling list
     */
    public void buildTrendingRecyclerView() {
        recyclerViewTrending = rootView.findViewById(R.id.trending);
        layoutManagerTrending = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        adapterTrending = new DashboardAdapter(getActivity(), occastionTrending);
        recyclerViewTrending.setLayoutManager(layoutManagerTrending);
        recyclerViewTrending.setAdapter(adapterTrending);
        recyclerViewTrending.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * Build a list of items that are happening on the same date as the system time, with a maximum
     * of 6, 3 each from Jios and Events displayed on a horizontal scrolling list.
     */
    public void buildTodayRecyclerView() {
        recyclerViewToday = rootView.findViewById(R.id.today);
        layoutManagerToday = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        adapterToday = new DashboardAdapter(getActivity(), occasionToday);
        recyclerViewToday.setLayoutManager(layoutManagerToday);
        recyclerViewToday.setAdapter(adapterToday);
        recyclerViewToday.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * Build a list of items that are most recently created, with a maximum
     * of 6, 3 each from Jios and Events displayed on a horizontal scrolling list.
     */
    public void buildNewlyCreatedRecyclerView() {
        recyclerViewNewlyCreated = rootView.findViewById(R.id.newOccasions);
        layoutManagerNewlyCreated = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
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
        default:
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

    /**
     * Pulls data from the database and search for those with the most likes and put them into
     * the designated ArrayList
     */
    public void initializeListTrending() {
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
                    } catch (Exception e) {
                        System.out.println(e);
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
                    try {
                        Occasion selected = snapshot.getValue(JiosItem.class);
                        String jioID = selected.getOccasionID();
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
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }

                ArrayList<Occasion> copyOfFullJios = new ArrayList<>(occasionJios);
                copyOfFullJios.addAll(occasionEvents);

                occasionAll = copyOfFullJios;

                MainActivity.sort(occasionAll);

                // At this point, occasionAll will have full list of Occasions
                // Sort by likes then take 1st 5

                // LOGIC GOES FROM HERE

                Collections.sort(occasionAll, (s1, s2) -> s2.getNumLikes() - s1.getNumLikes());

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

    /**
     * Pulls data from the database and search for Jios and Events that are happening on the same day
     * as system time and put them into the designated ArrayList
     */
    public void initializeListToday() {

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
                    } catch (Exception e) {
                        System.out.println(e);
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
                    try {
                        Occasion selected = snapshot.getValue(JiosItem.class);
                        String jioID = selected.getOccasionID();
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
                    } catch (Exception e) {
                        System.out.println(e);
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

    /**
     * Pulls data from the database and search for Jios and Events that are most recently created
     * and put them into the designated ArrayList
     */
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
                    try {
                        Occasion selected = snapshot.getValue(EventsItem.class);
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
                    } catch (Exception e) {
                        System.out.println(e);
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
                    try {
                        Occasion selected = snapshot.getValue(JiosItem.class);
                        String jioID = selected.getOccasionID();
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
                    } catch (Exception e) {
                        System.out.println(e);
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
