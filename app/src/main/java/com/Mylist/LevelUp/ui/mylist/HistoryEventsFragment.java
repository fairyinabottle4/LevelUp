package com.Mylist.LevelUp.ui.mylist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ActivityOccasionItem;
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
import java.util.Date;

public class HistoryEventsFragment extends Fragment {
    private View rootView;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MylistAdapter mAdapter;

    private ArrayList<Occasion> mPastOccasions = new ArrayList<>();
    ArrayList<String> mEventIDs = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.occ_mylist_fragment_plain, container, false);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        final String fbUIDFinal = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // pulling activityevent with my userID
        DatabaseReference mDatabaseReferenceActivityEvent = mFirebaseDatabase.getReference().child("ActivityEvent");
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

        DatabaseReference mDatabaseReferenceEvents = mFirebaseDatabase.getReference().child("Events");

        mDatabaseReferenceEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                        if (eventDate.compareTo(currentDate) < 0) {
                            mPastOccasions.add(selected);
                        }
                    }
                }
                MainActivity.sort(mPastOccasions);
                MylistAdapter myListAdapter = new MylistAdapter(mPastOccasions);
                mAdapter = myListAdapter;
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        buildRecyclerView();
        return rootView;
    }

    public void buildRecyclerView() {
        mRecyclerView = rootView.findViewById(R.id.occMylistFragmentRecyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new MylistAdapter(mPastOccasions);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

}
