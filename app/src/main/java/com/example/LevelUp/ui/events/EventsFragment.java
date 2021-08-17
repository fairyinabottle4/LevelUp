package com.example.LevelUp.ui.events;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.Events.LevelUp.ui.events.EventsAdapter;
import com.Events.LevelUp.ui.events.EventsAdder;
import com.Events.LevelUp.ui.events.EventsItem;
import com.Events.LevelUp.ui.events.EventsLikedFragment;
import com.Events.LevelUp.ui.events.EventsMyListFragment;
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

public class EventsFragment extends Fragment {
    private static boolean refresh;
    private static final String[] categories = {"All",
        "Arts", "Sports", "Talks", "Volunteering", "Food", "Others"};
    private static ArrayList<EventsItem> eventsItemList;
    private static ArrayList<EventsItem> copy;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private EventsAdapter adapter;
    private View rootView;

    private FloatingActionButton floatingActionButton;

    private SwipeRefreshLayout swipeRefreshLayout;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_events, container, false);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Events");
        createEventsList();

        buildRecyclerView();
        floatingActionButton = rootView.findViewById(R.id.fab);
        if (MainActivity.getCurrentUser() != null && MainActivity.getCurrentUser().getIsStaff()) {
            floatingActionButton.setAlpha(0.50f); // setting transparency
            floatingActionButton.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), EventsAdder.class);
                startActivity(intent);
            });
        } else {
            floatingActionButton.setVisibility(View.INVISIBLE);
        }
        loadDataEvents();

        // setting up toolbar
        setHasOptionsMenu(true);
        Toolbar toolbar = rootView.findViewById(R.id.events_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        swipeRefreshLayout = rootView.findViewById(R.id.swiperefreshlayoutevents);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            eventsItemList.clear();
            loadDataEvents();
            // adapter.notifyDataSetChanged(); - added this line into loadDataEvents itself
            swipeRefreshLayout.setRefreshing(false);
        });

        return rootView;
    }

    public void createEventsList() {
        eventsItemList = new ArrayList<>();
    }

    /**
     * Builds a recycler view of a scrolling list of all the Eventitems
     */
    public void buildRecyclerView() {
        recyclerView = rootView.findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new EventsAdapter(getActivity(), eventsItemList);
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

        case R.id.action_filter:
            break;
        case R.id.subitem1:
            loadDataEvents();
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
            EventsMyListFragment nextFrag = new EventsMyListFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, nextFrag)
                    .addToBackStack(null)
                    .commit();
            break;
        case R.id.action_fav: // the heart
            EventsLikedFragment nextFrag2 = new EventsLikedFragment();
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
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
    }

    public static void setEventsItemList(ArrayList<EventsItem> eventsList) {
        eventsItemList = eventsList;
    }

    public static ArrayList<EventsItem> getEventsItemList() {
        return eventsItemList;
    }

    /**
     * Load data from the database into an ArrayList that will display the Events
     */
    public void loadDataEvents() {
        valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventsItemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        EventsItem selected = snapshot.getValue(EventsItem.class);

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
                            eventsItemList.add(selected);
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
                copy = new ArrayList<>(eventsItemList);
                EventsAdapter eventsAdapter = new EventsAdapter(getActivity(), eventsItemList);
                recyclerView.setAdapter(eventsAdapter);
                adapter = eventsAdapter;
                MainActivity.sort(eventsItemList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(valueEventListener);
        adapter.notifyDataSetChanged();
    }

    private void getSelectedCategoryData(int categoryID) {
        //This arraylist will contain only those in the certain categories
        ArrayList<EventsItem> list = new ArrayList<>();
        EventsAdapter eventsAdapter;
        //filter by id
        for (EventsItem eventsItem : eventsItemList) {
            if (eventsItem.getCategory() == categoryID) {
                list.add(eventsItem);
            }
        }
        eventsAdapter = new EventsAdapter(getActivity(), list);
        recyclerView.setAdapter(eventsAdapter);
        adapter = eventsAdapter;
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public static void setRefresh(boolean toSet) {
        refresh = toSet;
    }

    @Override
    public void onResume() {
        if (refresh) {
            loadDataEvents();
            refresh = false;
        }
        super.onResume();
    }
}

