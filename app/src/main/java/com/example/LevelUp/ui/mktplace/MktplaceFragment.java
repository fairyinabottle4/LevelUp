package com.example.LevelUp.ui.mktplace;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.Mktplace.LevelUp.ui.mktplace.MktplaceAdapter;
import com.Mktplace.LevelUp.ui.mktplace.MktplaceAdder;
import com.Mktplace.LevelUp.ui.mktplace.MktplaceItem;
import com.Mktplace.LevelUp.ui.mktplace.MktplacePage;
import com.example.tryone.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MktplaceFragment extends Fragment implements MktplaceAdapter.OnItemClickListener {
    private static ArrayList<MktplaceItem> mktplaceItemList;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MktplaceAdapter mAdapter;
    private View rootView;
    private FloatingActionButton floatingActionButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseReference mDatabaseRef;
    private MktplaceAdapter.OnItemClickListener mListener = this;

    public static boolean refresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mktplace, container, false);

        createMktplaceList();

        buildRecyclerView();
        floatingActionButton = rootView.findViewById(R.id.fab_mktplace);
        floatingActionButton.setAlpha(0.50f); // setting transparency
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MktplaceAdder.class);
                startActivity(intent);
            }
        });

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("mktplace uploads");

        loadDataMktplace();
        swipeRefreshLayout = rootView.findViewById(R.id.swiperefreshlayoutmktplace);

        // setting up toolbar
        setHasOptionsMenu(true);
        Toolbar toolbar = rootView.findViewById(R.id.mktplace_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.setSupportActionBar(toolbar);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mktplaceItemList.clear();
                loadDataMktplace();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return rootView;
    }

    /*
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mktplaceViewModel =
                ViewModelProviders.of(this).get(MktplaceViewModel.class);
        View root = inflater.inflate(R.layout.fragment_mktplace, container, false);
        final TextView textView = root.findViewById(R.id.text_mktplace);
        mktplaceViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

     */

    public void createMktplaceList() {
        mktplaceItemList = new ArrayList<>();
    }

    public void buildRecyclerView() {
        mRecyclerView = rootView.findViewById(R.id.recyclerview);
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mAdapter = new MktplaceAdapter(getContext(), mktplaceItemList, this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

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
        inflater.inflate(R.menu.mktplace_top_menu, menu);

        // setting the search function UI
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search");

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mAdapter.resetAdapter();
                mRecyclerView.setAdapter(mAdapter);
                return true;
            }
        });

        // ???
        // searchItem.setOnMenuItemClickListener()

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onItemClick(int position) {
        MktplaceItem mItem = mktplaceItemList.get(position);
        Intent intent = new Intent(getActivity(), MktplacePage.class);
        intent.putExtra("description", mItem.getDescription());
        intent.putExtra("title", mItem.getName());
        intent.putExtra("location", mItem.getLocation());
        intent.putExtra("imageurl", mItem.getImageUrl());
        startActivity(intent);
    }

    public static void setMktplaceItemList(ArrayList<MktplaceItem> list) {
        mktplaceItemList = list;
    }

    public void loadDataMktplace() {
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mktplaceItemList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    MktplaceItem upload = postSnapshot.getValue(MktplaceItem.class);
                    mktplaceItemList.add(upload);
                }

                mAdapter = new MktplaceAdapter(getActivity(), mktplaceItemList, mListener);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    public static void setRefresh(boolean toSet) {
        refresh = toSet;
    }

    @Override
    public void onResume() {
        if (refresh) {
            loadDataMktplace();
            refresh = false;
        }
        super.onResume();
    }
}
