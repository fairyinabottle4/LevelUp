package com.example.LevelUp.ui.mylist;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Events.LevelUp.ui.events.EventsItem;
import com.Jios.LevelUp.ui.jios.JiosItem;
import com.MainActivity;
import com.Mylist.LevelUp.ui.mylist.EditUserInfoActivity;
import com.Mylist.LevelUp.ui.mylist.MylistAdapter;
import com.example.LevelUp.ui.Occasion;
import com.example.LevelUp.ui.jios.JiosFragment;
import com.example.tryone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MylistFragment extends Fragment {
    ArrayList<Occasion> mOccasionListEventsInitial = new ArrayList<>();
    ArrayList<Occasion> mOccasionListJiosInitial = new ArrayList<>();
    // ArrayList<Occasion> mOccasionListReal = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MylistAdapter mAdapter;
    private View rootView;
    private static ArrayList<Integer> numberEvents = new ArrayList<>();
    private static ArrayList<Integer> numberJios = new ArrayList<>();
    private FirebaseDatabase mDatabaseEvents;
    private DatabaseReference mDatabaseReferenceEvents;
    private FirebaseDatabase mDatabaseJios;
    private DatabaseReference mDatabaseReferenceJios;
    ValueEventListener mValueEventListenerEvents;
    ValueEventListener mValueEventListenerJios;

    private ImageButton editUserInfoBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mylist, container, false);
        mDatabaseEvents = FirebaseDatabase.getInstance();
        mDatabaseReferenceEvents = mDatabaseEvents.getReference().child("Events");

        mDatabaseJios = FirebaseDatabase.getInstance();
        mDatabaseReferenceJios = mDatabaseJios.getReference().child("Jios");

        // createMylistList();
        // MainActivity.mOccasionListReal;

        // Events
        mValueEventListenerEvents = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mOccasionListEventsInitial.add(snapshot.getValue(EventsItem.class));
                }
                if (numberEvents.size() != 0) {
                    ArrayList<Occasion> mOLR = MainActivity.mOccasionListReal;
                    ArrayList<Integer> IDs = MainActivity.mEventsIDs;

                    for (int id : numberEvents){
                        if (!IDs.contains(id)) {
                            Occasion toAdd = mOccasionListEventsInitial.get(id);
                            mOLR.add(toAdd);
                            IDs.add(id);
                            Toast.makeText(getContext(), Integer.toString(id), Toast.LENGTH_SHORT).show();
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
                    MainActivity.sort(mOLR);
                    MainActivity.mOccasionListRealFull = new ArrayList<>(mOLR);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReferenceEvents.addValueEventListener(mValueEventListenerEvents);

        /*
        ArrayList<JiosItem> jiosItemArrayList = MainActivity.getJiosListReal();

        if (MainActivity.mJiosIDs.size() != 0 && jiosItemArrayList.size() != 0) {
            ArrayList<Occasion> temp = new ArrayList<>();
            for (int id : MainActivity.mJiosIDs) {
                Occasion toAdd = jiosItemArrayList.get(id);
                temp.add(toAdd);
            }
            MainActivity.mOccasionListReal = new ArrayList<>(temp);
        }

         */


        // Jios
        mValueEventListenerJios = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mOccasionListJiosInitial.add(snapshot.getValue(JiosItem.class));
                }

                if (numberJios.size() != 0) {
                    ArrayList<Occasion> mOLR = MainActivity.mOccasionListReal;
                    ArrayList<Integer> IDs = MainActivity.mJiosIDs;
                    for (int id : numberJios) {
                        if (!IDs.contains(id)) {
                            Occasion toAdd = mOccasionListJiosInitial.get(id);
                            mOLR.add(toAdd);

                            IDs.add(id);
                            Toast.makeText(getContext(), Integer.toString(id), Toast.LENGTH_SHORT).show();
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
                    MainActivity.sort(mOLR);
                    MainActivity.mOccasionListRealFull = new ArrayList<>(mOLR);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReferenceJios.addValueEventListener(mValueEventListenerJios);

        // setting up toolbar
        setHasOptionsMenu(true);
        Toolbar toolbar = rootView.findViewById(R.id.mylist_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.setSupportActionBar(toolbar);



        buildRecyclerView();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        editUserInfoBtn = (ImageButton) getView().findViewById(R.id.edit_user_info_btn);

        editUserInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditUserInfoActivity.class);
                startActivity(intent);
            }
        });

        super.onViewCreated(view, savedInstanceState);
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
        mAdapter = new MylistAdapter(MainActivity.mOccasionListReal);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }
    
    public static void setNumberEvents(int i) {
        numberEvents.add(i);
    }

    public static void setNumberJios(int i) {
        numberJios.add(i);
    }

    /*
    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        activity.saveMyListData();
    }

     */


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
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
        inflater.inflate(R.menu.mylist_top_menu, menu);

        // setting the search function UI
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search");

        // ???
        // searchItem.setOnMenuItemClickListener()

        super.onCreateOptionsMenu(menu, inflater);
    }
}
