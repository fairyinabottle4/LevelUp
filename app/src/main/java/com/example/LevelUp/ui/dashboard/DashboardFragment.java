package com.example.LevelUp.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ActivityOccasionItem;
import com.Dashboard.LevelUp.ui.dashboard.DashboardAdapter;
import com.Dashboard.LevelUp.ui.dashboard.DashboardSettingsActivity;
import com.Dashboard.LevelUp.ui.dashboard.TrendingFragment;
import com.Dashboard.LevelUp.ui.dashboard.UpcomingEventsFragment;
import com.Dashboard.LevelUp.ui.dashboard.UpcomingJiosFragment;
import com.Events.LevelUp.ui.events.EventsAdapter;
import com.Events.LevelUp.ui.events.EventsItem;
import com.Jios.LevelUp.ui.jios.JiosItem;
import com.LikeOccasionItem;
import com.MainActivity;
import com.Mktplace.LevelUp.ui.mktplace.MktplaceAdapter;
import com.Mktplace.LevelUp.ui.mktplace.MktplaceItem;
import com.Mylist.LevelUp.ui.mylist.MylistAdapter;
import com.Mylist.LevelUp.ui.mylist.MylistLikedAdapter;
import com.example.LevelUp.ui.Occasion;
import com.example.tryone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DashboardFragment extends Fragment {
    private View rootView;

    private RecyclerView mRecyclerViewTrending;
    private LinearLayoutManager mLayoutManagerTrending;
    private DashboardAdapter mAdapterTrending;

    private RecyclerView mRecyclerViewToday;
    private LinearLayoutManager mLayoutManagerToday;
    private DashboardAdapter mAdapterToday;

    private RecyclerView mRecyclerViewNewOcc;
    private LinearLayoutManager mLayoutManagerNewOcc;
    private DashboardAdapter mAdapterNewOcc;

    ArrayList<Occasion> mOccasionAll = new ArrayList<>();
    ArrayList<Occasion> mOccasionTrending = new ArrayList<>();
    ArrayList<Occasion> mOccasionToday = new ArrayList<>();
    ArrayList<Occasion> mOccasionNewOcc = new ArrayList<>();

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
        buildNewOccasionsTrendingRecyclerView();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        initializeListTrending();


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

        mAdapterToday = new DashboardAdapter(getActivity(), mOccasionAll);
        mRecyclerViewToday.setLayoutManager(mLayoutManagerToday);
        mRecyclerViewToday.setAdapter(mAdapterToday);
        mRecyclerViewToday.setItemAnimator(new DefaultItemAnimator());
    }

    public void buildNewOccasionsTrendingRecyclerView() {
        mRecyclerViewNewOcc = rootView.findViewById(R.id.newOccasions);
        mLayoutManagerNewOcc = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
//                mLayoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapterNewOcc = new DashboardAdapter(getActivity(), mOccasionAll);
        mRecyclerViewNewOcc.setLayoutManager(mLayoutManagerNewOcc);
        mRecyclerViewNewOcc.setAdapter(mAdapterNewOcc);
        mRecyclerViewNewOcc.setItemAnimator(new DefaultItemAnimator());
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

//    private void initializeEvents() {
//        ValueEventListener mValueEventListener = new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mEventsList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    EventsItem selected = snapshot.getValue(EventsItem.class);
//                    // EventsItemList.add(selected);
//
//                    // To show ALL Events created comment out lines 231 to 261 and uncomment out line 227
//
//                    if (selected.getTimeInfo().length() > 4) {
//                        continue;
//                    }
//
//                    int hour = Integer.parseInt(selected.getTimeInfo().substring(0,2));
//                    int min = Integer.parseInt(selected.getTimeInfo().substring(2));
//
//                    Date eventDateZero = selected.getDateInfo();
//                    Calendar cal = Calendar.getInstance();
//                    cal.setTime(eventDateZero);
//                    cal.set(Calendar.HOUR_OF_DAY, hour);
//                    cal.set(Calendar.MINUTE, min);
//                    Date eventDate = cal.getTime();
//
//                    // Toast.makeText(getActivity(), eventDate.toString(), Toast.LENGTH_SHORT).show();
//
//                    Date currentDate = new Date();
//                    // eventDate.compareTo(currentDate) >= 0
//                    //eventDate.after(currentDate)
//                    if (eventDate.compareTo(currentDate) >= 0) {
//                        mEventsList.add(selected);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//
//        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
//        DatabaseReference mDatabaseReferenceEvents = mDatabase.getReference().child("Events");
//        mDatabaseReferenceEvents.addListenerForSingleValueEvent(mValueEventListener);
//    }
//
//    private void initializeJios() {
//        ValueEventListener mValueEventListener = new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mJiosList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    JiosItem selected = snapshot.getValue(JiosItem.class);
//                    // EventsItemList.add(selected);
//
//                    // To show ALL Events created comment out lines 231 to 261 and uncomment out line 227
//
//                    if (selected.getTimeInfo().length() > 4) {
//                        continue;
//                    }
//
//                    int hour = Integer.parseInt(selected.getTimeInfo().substring(0,2));
//                    int min = Integer.parseInt(selected.getTimeInfo().substring(2));
//
//                    Date eventDateZero = selected.getDateInfo();
//                    Calendar cal = Calendar.getInstance();
//                    cal.setTime(eventDateZero);
//                    cal.set(Calendar.HOUR_OF_DAY, hour);
//                    cal.set(Calendar.MINUTE, min);
//                    Date eventDate = cal.getTime();
//
//                    // Toast.makeText(getActivity(), eventDate.toString(), Toast.LENGTH_SHORT).show();
//
//                    Date currentDate = new Date();
//                    // eventDate.compareTo(currentDate) >= 0
//                    //eventDate.after(currentDate)
//                    if (eventDate.compareTo(currentDate) >= 0) {
//                        mJiosList.add(selected);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//
//        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
//        DatabaseReference mDatabaseReferenceJios = mDatabase.getReference().child("Jios");
//        mDatabaseReferenceJios.addListenerForSingleValueEvent(mValueEventListener);
//    }

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
}
