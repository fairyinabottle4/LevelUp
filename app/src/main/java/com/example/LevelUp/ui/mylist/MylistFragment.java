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

import com.Events.LevelUp.ui.events.EventsItem;
import com.Jios.LevelUp.ui.jios.JiosItem;
import com.Mylist.LevelUp.ui.mylist.MylistAdapter;
import com.example.LevelUp.ui.Occasion;
import com.example.tryone.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class MylistFragment extends Fragment {
    ArrayList<Occasion> OccasionList;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MylistAdapter mAdapter;
    private View rootView;
//    Intent intent = getActivity().getIntent();
//    int profilePicture;
//    int hourOfDay;
//    int minute;
//    String title;
//    String description;
//    Date date;
//    String location;
//    String time;
    EventsItem event = new EventsItem(Parcel.obtain());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mylist, container, false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            event = bundle.getParcelable("event");
            Toast.makeText(getContext(), "there is something", Toast.LENGTH_SHORT).show();
        }
        String s = event.getDescription();
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
        createMylistList();
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

    public void createMylistList() {
        OccasionList = new ArrayList<>();
        OccasionList.add(event);
        Toast.makeText(getContext(), "event added", Toast.LENGTH_SHORT).show();
//        OccasionList.add(new EventsItem(profilePicture, date, time, hourOfDay, minute, location, title, description));
    }

    public void buildRecyclerView() {
        mRecyclerView = rootView.findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new MylistAdapter(OccasionList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}
