package com.example.LevelUp.ui.mylist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Events.LevelUp.ui.events.EventsAdder;
import com.Events.LevelUp.ui.events.EventsItem;
import com.Jios.LevelUp.ui.jios.JiosItem;
import com.MainActivity;
import com.Mylist.LevelUp.ui.mylist.MylistAdapter;
import com.example.LevelUp.ui.Occasion;
import com.example.tryone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class MylistFragment extends Fragment {
    ArrayList<Occasion> mOccasionListEventsInitial = new ArrayList<>();
    ArrayList<Occasion> mOccasionListJiosInitial = new ArrayList<>();
    // ArrayList<Occasion> mOccasionListReal = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MylistAdapter mAdapter;
    private View rootView;
    private static Integer numberEvents = -1;
    private static Integer numberJios = -1;
    private FirebaseDatabase mDatabaseEvents;
    private DatabaseReference mDatabaseReferenceEvents;
    private FirebaseDatabase mDatabaseJios;
    private DatabaseReference mDatabaseReferenceJios;
    ValueEventListener mValueEventListenerEvents;
    ValueEventListener mValueEventListenerJios;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mylist, container, false);
        mDatabaseEvents = FirebaseDatabase.getInstance();
        mDatabaseReferenceEvents = mDatabaseEvents.getReference().child("Events");
        
        mDatabaseJios = FirebaseDatabase.getInstance();
        mDatabaseReferenceJios = mDatabaseJios.getReference().child("Jios");

        // createMylistList();


        // Events
        mValueEventListenerEvents = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mOccasionListEventsInitial.add(snapshot.getValue(EventsItem.class));
                }
                if (numberEvents != -1) {
                    ArrayList<Occasion> mOLR = MainActivity.mOccasionListReal;
                    ArrayList<Integer> IDs = MainActivity.mEventsIDs;
                    Occasion toAdd = mOccasionListEventsInitial.get(numberEvents);
                    if (!IDs.contains(numberEvents)) {
                        mOLR.add(toAdd);
                        IDs.add(numberEvents);
                        Toast.makeText(getContext(), numberEvents.toString(), Toast.LENGTH_SHORT).show();
                        MylistAdapter myListAdapter = new MylistAdapter(mOLR);
                        mRecyclerView.setAdapter(myListAdapter);
                        mAdapter = myListAdapter;
                    } else {
                        // This toast is temporary, it should change the + to a tick or smth
                        Toast.makeText(getContext(), "Event already added to your list", Toast.LENGTH_SHORT).show();
                        MylistAdapter myListAdapter = new MylistAdapter(mOLR);
                        mRecyclerView.setAdapter(myListAdapter);
                        mAdapter = myListAdapter;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReferenceEvents.addValueEventListener(mValueEventListenerEvents);

        // Jios
        mValueEventListenerJios = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mOccasionListJiosInitial.add(snapshot.getValue(JiosItem.class));
                }
                if (numberJios != -1) {
                    ArrayList<Occasion> mOLR = MainActivity.mOccasionListReal;
                    ArrayList<Integer> IDs = MainActivity.mJiosIDs;
                    Occasion toAdd = mOccasionListJiosInitial.get(numberJios);
                    if (!IDs.contains(numberJios)) {
                        mOLR.add(toAdd);
                        IDs.add(numberJios);
                        Toast.makeText(getContext(), numberJios.toString(), Toast.LENGTH_SHORT).show();
                        MylistAdapter myListAdapter = new MylistAdapter(mOLR);
                        mRecyclerView.setAdapter(myListAdapter);
                        mAdapter = myListAdapter;
                    } else {
                        // This toast is temporary, it should change the + to a tick or smth
                        Toast.makeText(getContext(), "Jio already added to your list", Toast.LENGTH_SHORT).show();
                        MylistAdapter myListAdapter = new MylistAdapter(mOLR);
                        mRecyclerView.setAdapter(myListAdapter);
                        mAdapter = myListAdapter;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReferenceJios.addValueEventListener(mValueEventListenerJios);
        buildRecyclerView();
        return rootView;
    }

    /*
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MylistViewModel =
                ViewModelProviders.of(this).get(MylistViewModel.class);
        View root = inflater.inflate(R.layout.fragment_Mylist, container, false);
        final TextView textView = root.findViewById(R.id.text_Mylist);
        MylistViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

     */
    /*
    public void createMylistList() {
        mOccasionListEventsInitial = new ArrayList<>();
        mOccasionListJiosInitial = new ArrayList<>();
        //mOccasionListReal = new ArrayList<>();
    }
    */


    public void buildRecyclerView() {
        mRecyclerView = rootView.findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(getContext());
        // mAdapter = new MylistAdapter(mOccasionListReal);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }
    
    public static void setNumberEvents(int i) {
        numberEvents = i;
    }

    public static void setNumberJios(int i) {
        numberJios = i;
    }
}
