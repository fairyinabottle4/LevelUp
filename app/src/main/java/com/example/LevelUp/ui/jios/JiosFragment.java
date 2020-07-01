package com.example.LevelUp.ui.jios;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.Jios.LevelUp.ui.jios.JiosAdder;
import com.Jios.LevelUp.ui.jios.JiosAdapter;
import com.Jios.LevelUp.ui.jios.JiosItem;
import com.MainActivity;
import com.example.tryone.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class JiosFragment extends Fragment {
    private static ArrayList<JiosItem> JiosItemList;
    private static ArrayList<JiosItem> copy;
    FirebaseDatabase mDatabase;
    DatabaseReference mDatabaseReference;
    ValueEventListener mValueEventListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private JiosAdapter mAdapter;
    private View rootView;
    public FloatingActionButton floatingActionButton;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_jios, container, false);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("Jios");
        createJiosList();

        buildRecyclerView();
        floatingActionButton = rootView.findViewById(R.id.fab);
        floatingActionButton.setAlpha(0.50f); // setting transparency
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), JiosAdder.class);
                startActivity(intent);
            }
        });

        loadDataJios();


        // setting up Toolbar
        setHasOptionsMenu(true);
        Toolbar toolbar = rootView.findViewById(R.id.jios_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        swipeRefreshLayout = rootView.findViewById(R.id.swiperefreshlayoutjios);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                JiosItemList.clear();
                loadDataJios();
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    public void createJiosList() {
        JiosItemList = new ArrayList<>();
    }

    /*
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        JiosViewModel =
                ViewModelProviders.of(this).get(JiosViewModel.class);
        View root = inflater.inflate(R.layout.fragment_Jios, container, false);
        final TextView textView = root.findViewById(R.id.text_Jios);
        JiosViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

     */

//    public void createJiosList() {
//        JiosItemList = new ArrayList<>();
//    }

    public void buildRecyclerView() {
        mRecyclerView = rootView.findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new JiosAdapter(getActivity(), JiosItemList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:

                MenuItem searchItem = item;
                SearchView searchView = (SearchView) searchItem.getActionView();
                // searchView.setQueryHint("Search");
                // searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                searchItem.setActionView(searchView);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        mAdapter.getFilter().filter(newText);
                        return false;
                    }
                });

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.occasions_top_menu, menu);

        // setting the search function UI
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search");

        // ???
        // searchItem.setOnMenuItemClickListener()

        super.onCreateOptionsMenu(menu, inflater);
    }


    public static void setJiosItemList(ArrayList<JiosItem> jioslist) {
        JiosItemList = jioslist;
    }

    public static ArrayList<JiosItem> getJiosItemListCopy() {
        return copy;
    }

    public static ArrayList<JiosItem> getJiosItemList() {
        return JiosItemList;
    }

    public void loadDataJios() {
        mValueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    JiosItemList.add(snapshot.getValue(JiosItem.class));
                }
                copy = new ArrayList<>(JiosItemList);
                JiosAdapter jiosAdapter = new JiosAdapter(getActivity(), JiosItemList);
                mRecyclerView.setAdapter(jiosAdapter);
                mAdapter = jiosAdapter; // YI EN ADDED THIS LINE
                MainActivity.sort(JiosItemList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addValueEventListener(mValueEventListener);
    }
}
