package com.example.LevelUp.ui.mktplace;

import java.util.ArrayList;

import com.Mktplace.LevelUp.ui.mktplace.MktplaceAdapter;
import com.Mktplace.LevelUp.ui.mktplace.MktplaceAdder;
import com.Mktplace.LevelUp.ui.mktplace.MktplaceItem;
import com.Mktplace.LevelUp.ui.mktplace.MktplaceLikedFragment;
import com.example.tryone.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import android.widget.SearchView;
import android.widget.TextView;
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

public class MktplaceFragment extends Fragment {
    private static boolean refresh;

    private static ArrayList<MktplaceItem> mktPlaceItemList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MktplaceAdapter adapter;
    private View rootView;
    private FloatingActionButton floatingActionButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseReference databaseRef;
    private TextView nothingView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mktplace, container, false);
        nothingView = rootView.findViewById(R.id.nothing2);
        createMktplaceList();

        buildRecyclerView();
        floatingActionButton = rootView.findViewById(R.id.fab_mktplace);
        floatingActionButton.setAlpha(0.50f); // setting transparency
        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MktplaceAdder.class);
            startActivity(intent);
        });

        databaseRef = FirebaseDatabase.getInstance().getReference("mktplace uploads");

        loadDataMktplace();
        swipeRefreshLayout = rootView.findViewById(R.id.swiperefreshlayoutmktplace);

        // setting up toolbar
        setHasOptionsMenu(true);
        Toolbar toolbar = rootView.findViewById(R.id.mktplace_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.setSupportActionBar(toolbar);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            mktPlaceItemList.clear();
            loadDataMktplace();
            swipeRefreshLayout.setRefreshing(false);
        });
        return rootView;
    }

    public void createMktplaceList() {
        mktPlaceItemList = new ArrayList<>();
    }

    /**
     * Builds the list of items that will be contained in the Fragment
     */
    public void buildRecyclerView() {
        recyclerView = rootView.findViewById(R.id.recyclerview);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        adapter = new MktplaceAdapter(getActivity(), mktPlaceItemList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
        case R.id.action_search:

            MenuItem searchItem = item;
            SearchView searchView = (SearchView) searchItem.getActionView();
            searchItem.setActionView(searchView);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.getFilter().filter(newText);
                    return false;
                }
            });

            break;

        case R.id.action_fav: // the heart
            MktplaceLikedFragment nextFrag2 = new MktplaceLikedFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, nextFrag2)
                .addToBackStack(null)
                .commit();
            break;
        default:
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
                adapter.resetAdapter();
                recyclerView.setAdapter(adapter);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Loads listing data from the database into the ArrayList which will be displayed
     */
    public void loadDataMktplace() {
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mktPlaceItemList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    MktplaceItem upload = postSnapshot.getValue(MktplaceItem.class);
                    mktPlaceItemList.add(upload);
                }

                if (mktPlaceItemList.isEmpty()) {
                    nothingView.setVisibility(View.VISIBLE);
                }
                adapter = new MktplaceAdapter(getActivity(), mktPlaceItemList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        adapter.notifyDataSetChanged();
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
