package com.example.LevelUp.ui.jios;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.Jios.LevelUp.ui.jios.JiosAdapter;
import com.Jios.LevelUp.ui.jios.JiosAdder;
import com.Jios.LevelUp.ui.jios.JiosItem;
import com.Jios.LevelUp.ui.jios.JiosLikedFragment;
import com.Jios.LevelUp.ui.jios.JiosMyListFragment;
import com.MainActivity;
import com.example.tryone.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

public class JiosFragment extends Fragment {
    private static boolean refresh;

    private static ArrayList<JiosItem> jiosItemList;
    private static ArrayList<JiosItem> copy;
    private static final String[] categories = {"All",
        "Arts", "Sports", "Talks", "Volunteering", "Food", "Others"};

    private FloatingActionButton floatingActionButton;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private JiosAdapter adapter;
    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_jios, container, false);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Jios");
        createJiosList();

        buildRecyclerView();
        floatingActionButton = rootView.findViewById(R.id.fab);
        floatingActionButton.setAlpha(0.50f); // setting transparency
        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), JiosAdder.class);
            startActivity(intent);
        });

        loadDataJios();
        // setting up Toolbar
        setHasOptionsMenu(true);
        Toolbar toolbar = rootView.findViewById(R.id.jios_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        swipeRefreshLayout = rootView.findViewById(R.id.swiperefreshlayoutjios);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            jiosItemList.clear();
            loadDataJios();
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });

        return rootView;
    }

    public void createJiosList() {
        jiosItemList = new ArrayList<>();
    }

    /**
     * Builds the list of items that will be contained in the Fragment
     */
    public void buildRecyclerView() {
        recyclerView = rootView.findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new JiosAdapter(getActivity(), jiosItemList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
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
        case R.id.action_filter:
            break;
        case R.id.subitem1:
            loadDataJios();
            break;
        case R.id.subitem2:
            getSelectedCategoryData(0);
            break;
        case R.id.subitem3:
            getSelectedCategoryData(1);
            break;
        case R.id.subitem4:
            getSelectedCategoryData(2);
            break;
        case R.id.subitem5:
            getSelectedCategoryData(3);
            break;
        case R.id.subitem6:
            getSelectedCategoryData(4);
            break;
        case R.id.subitem7:
            getSelectedCategoryData(5);
            break;

        case R.id.action_cfmed_events: // the tick
            JiosMyListFragment nextFrag = new JiosMyListFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, nextFrag)
                    .addToBackStack(null)
                    .commit();
            break;
        case R.id.action_fav: // the heart
            JiosLikedFragment nextFrag2 = new JiosLikedFragment();
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
        inflater.inflate(R.menu.occasions_top_menu, menu);

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
                closeKeyboard();
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void closeKeyboard() {
        View view = this.getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
    }

    public static ArrayList<JiosItem> getJiosItemList() {
        return jiosItemList;
    }

    private void getSelectedCategoryData(int categoryID) {
        //This arraylist will contain only those in the certain categories
        ArrayList<JiosItem> list = new ArrayList<>();
        JiosAdapter jiosAdapter;
        //filter by id
        for (JiosItem jiosItem : jiosItemList) {
            if (jiosItem.getCategory() == categoryID) {
                list.add(jiosItem);
            }
        }
        jiosAdapter = new JiosAdapter(getActivity(), list);
        recyclerView.setAdapter(jiosAdapter);
        adapter = jiosAdapter;
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * Load data from database into ArrayList which contains the JiosItem
     */
    public void loadDataJios() {
        valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jiosItemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        JiosItem selected = snapshot.getValue(JiosItem.class);

                        if (selected.getTimeInfo().length() > 4) {
                            continue;
                        }

                        int hour = Integer.parseInt(selected.getTimeInfo().substring(0, 2));
                        int min = Integer.parseInt(selected.getTimeInfo().substring(2));

                        Date eventDateZero = selected.getDateInfo();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(eventDateZero);
                        cal.set(Calendar.HOUR_OF_DAY, hour);
                        cal.set(Calendar.MINUTE, min);
                        Date eventDate = cal.getTime();

                        Date currentDate = new Date();

                        if (eventDate.compareTo(currentDate) >= 0) {
                            jiosItemList.add(selected);
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
                copy = new ArrayList<>(jiosItemList);
                JiosAdapter jiosAdapter = new JiosAdapter(getActivity(), jiosItemList);
                recyclerView.setAdapter(jiosAdapter);
                adapter = jiosAdapter;
                MainActivity.sort(jiosItemList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

    public static void setRefresh(boolean toSet) {
        refresh = toSet;
    }

    @Override
    public void onResume() {
        if (refresh) {
            loadDataJios();
            refresh = false;
        }
        super.onResume();
    }
}
