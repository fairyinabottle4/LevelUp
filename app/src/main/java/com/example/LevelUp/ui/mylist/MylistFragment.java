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
import com.Mylist.LevelUp.ui.mylist.MylistAdapter;
import com.example.LevelUp.ui.Occasion;
import com.example.tryone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class MylistFragment extends Fragment {
    ArrayList<Occasion> mOccasionListEventsInitial;
    ArrayList<Occasion> mOccasionListJiosInitial;
    ArrayList<Occasion> mOccasionListReal;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MylistAdapter mAdapter;
    private View rootView;
    private static Integer numberEvents;
    private static Integer numberJios;
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

        createMylistList();
        buildRecyclerView();

        mValueEventListenerEvents = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mOccasionListEventsInitial.add(snapshot.getValue(EventsItem.class));
                }
                mOccasionListReal.add(mOccasionListEventsInitial.get(numberEvents));
                Toast.makeText(getContext(), numberEvents.toString(), Toast.LENGTH_SHORT).show();
                MylistAdapter mylistAdapter = new MylistAdapter(mOccasionListReal);
                mRecyclerView.setAdapter(mylistAdapter);
                mAdapter = mylistAdapter; // YI EN ADDED THIS LINE
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReferenceEvents.addValueEventListener(mValueEventListenerEvents);

        mValueEventListenerJios = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mOccasionListJiosInitial.add(snapshot.getValue(JiosItem.class));
                }
                mOccasionListReal.add(mOccasionListJiosInitial.get(numberJios));
                Toast.makeText(getContext(), numberJios.toString(), Toast.LENGTH_SHORT).show();
                MylistAdapter myListAdapter = new MylistAdapter(mOccasionListReal);
                mRecyclerView.setAdapter(myListAdapter);
                mAdapter = myListAdapter; // YI EN ADDED THIS LINE
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReferenceJios.addValueEventListener(mValueEventListenerJios);

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

    public void createMylistList() {
        mOccasionListEventsInitial = new ArrayList<>();
        mOccasionListJiosInitial = new ArrayList<>();
        mOccasionListReal = new ArrayList<>();
    }

    public void buildRecyclerView() {
        mRecyclerView = rootView.findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new MylistAdapter(mOccasionListReal);
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
