package com.example.LevelUp.ui.dashboard;

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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Dashboard.LevelUp.ui.dashboard.DashboardSettingsActivity;
import com.Dashboard.LevelUp.ui.dashboard.TrendingFragment;
import com.Dashboard.LevelUp.ui.dashboard.UpcomingEventsFragment;
import com.Dashboard.LevelUp.ui.dashboard.UpcomingJiosFragment;
import com.MainActivity;
import com.Mktplace.LevelUp.ui.mktplace.MktplaceAdapter;
import com.Mktplace.LevelUp.ui.mktplace.MktplaceItem;
import com.example.tryone.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private View rootView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // for Trending Page
        CardView trendingCard = rootView.findViewById(R.id.trending);
        trendingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getActivity(), "Trending!", Toast.LENGTH_SHORT).show();
                TrendingFragment nextFrag= new TrendingFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, nextFrag)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // for upcoming jios
        CardView jiosCard = rootView.findViewById(R.id.upcoming_jios);
        jiosCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getActivity(), "Trending!", Toast.LENGTH_SHORT).show();
                UpcomingJiosFragment nextFrag= new UpcomingJiosFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, nextFrag)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // for upcoming events
        CardView eventsCard = rootView.findViewById(R.id.upcoming_events);
        eventsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getActivity(), "Trending!", Toast.LENGTH_SHORT).show();
                UpcomingEventsFragment nextFrag= new UpcomingEventsFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, nextFrag)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // setting up toolbar
        setHasOptionsMenu(true);
        Toolbar toolbar = rootView.findViewById(R.id.dashboard_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.setSupportActionBar(toolbar);

        return rootView;
    }
    /*
    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_dashboard_settings:
                Intent intent = new Intent(getActivity(), DashboardSettingsActivity.class);
                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.dashboard_top_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

}
