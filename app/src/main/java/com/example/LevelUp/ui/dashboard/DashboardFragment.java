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
import java.util.Date;
import java.util.List;

public class DashboardFragment extends Fragment {
    private View rootView;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private DashboardAdapter mAdapter;

    public static ArrayList<String> mOccIDs = new ArrayList<>();
    public static ArrayList<Occasion> mOccasions = new ArrayList<>();
    public static ArrayList<EventsItem> mEventsList = new ArrayList<>();
    public static ArrayList<JiosItem> mJiosList = new ArrayList<>();


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


        initializeEvents();
        initializeJios();

        buildTrendingRecyclerView();
        buildTodayRecyclerView();
        buildNewOccasionsTrendingRecyclerView();


        return rootView;
    }

    public void buildTrendingRecyclerView() {
        mRecyclerView = rootView.findViewById(R.id.trending);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
//                mLayoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapter = new DashboardAdapter(getActivity(), mOccasions);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void buildTodayRecyclerView() {
        mRecyclerView = rootView.findViewById(R.id.today);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
//                mLayoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapter = new DashboardAdapter(getActivity(), mOccasions);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void buildNewOccasionsTrendingRecyclerView() {
        mRecyclerView = rootView.findViewById(R.id.newOccasions);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
//                mLayoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapter = new DashboardAdapter(getActivity(), mOccasions);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
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

    private void initializeEvents() {
        ValueEventListener mValueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mOccasions.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    EventsItem selected = snapshot.getValue(EventsItem.class);
                    // EventsItemList.add(selected);

                    // To show ALL Events created comment out lines 231 to 261 and uncomment out line 227

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

                    // Toast.makeText(getActivity(), eventDate.toString(), Toast.LENGTH_SHORT).show();

                    Date currentDate = new Date();
                    // eventDate.compareTo(currentDate) >= 0
                    //eventDate.after(currentDate)
                    if (eventDate.compareTo(currentDate) >= 0) {
                        mEventsList.add(selected);
                    }
                }
                MainActivity.sort(mEventsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReferenceEvents = mDatabase.getReference().child("Events");
        mDatabaseReferenceEvents.addListenerForSingleValueEvent(mValueEventListener);
    }

    private void initializeJios() {
        ValueEventListener mValueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mOccasions.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    JiosItem selected = snapshot.getValue(JiosItem.class);
                    // EventsItemList.add(selected);

                    // To show ALL Events created comment out lines 231 to 261 and uncomment out line 227

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

                    // Toast.makeText(getActivity(), eventDate.toString(), Toast.LENGTH_SHORT).show();

                    Date currentDate = new Date();
                    // eventDate.compareTo(currentDate) >= 0
                    //eventDate.after(currentDate)
                    if (eventDate.compareTo(currentDate) >= 0) {
                        mOccasions.add(selected);
                    }
                }
                MainActivity.sort(mOccasions);
                DashboardAdapter occAdapter = new DashboardAdapter(getActivity(), mOccasions);
                mRecyclerView.setAdapter(occAdapter);
                mAdapter = occAdapter;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReferenceJios = mDatabase.getReference().child("Jios");
        mDatabaseReferenceJios.addListenerForSingleValueEvent(mValueEventListener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
