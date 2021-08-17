package com.Jios.LevelUp.ui.jios;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.ActivityOccasionItem;
import com.MainActivity;
import com.Mylist.LevelUp.ui.mylist.MylistAdapter;
import com.example.LevelUp.ui.Occasion;
import com.example.tryone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class JiosMyListFragment extends Fragment {
    private static ArrayList<String> jioIdList = new ArrayList<>();
    private static ArrayList<Occasion> occasionJioList = new ArrayList<>();

    private static boolean refreshList;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MylistAdapter adapter;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.occ_mylist_fragment, container, false);

        Toolbar toolbar = rootView.findViewById(R.id.occ_mylist_fragment_title);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle("Jios I Signed Up For");



        buildRecyclerView();

        initializeList();

        return rootView;

    }

    /**
     * Builds the recycler view for the list of items displayed
     */
    public void buildRecyclerView() {
        recyclerView = rootView.findViewById(R.id.occMylistFragmentRecyclerView);
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new MylistAdapter(getActivity(), occasionJioList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initializeList() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final String fbUidFinal = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabaseReferenceActivityJio = firebaseDatabase.getReference().child("ActivityJio");
        mDatabaseReferenceActivityJio.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jioIdList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                    String selectedUserID = selected.getUserID();
                    if (selectedUserID.equals(fbUidFinal)) {
                        jioIdList.add(selected.getOccasionID());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference databaseRefJios = firebaseDatabase.getReference().child("Jios");
        databaseRefJios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                occasionJioList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    JiosItem selected = snapshot.getValue(JiosItem.class);
                    String jioId = selected.getOccasionID();

                    if (jioIdList.contains(jioId)) {
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
                            occasionJioList.add(selected);
                        }
                    }
                }
                MainActivity.sort(occasionJioList);
                MylistAdapter mylistAdapter = new MylistAdapter(getActivity(), occasionJioList);
                adapter = mylistAdapter;
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        adapter.notifyDataSetChanged();
    }

    public static void setRefreshList(boolean refreshList) {
        JiosMyListFragment.refreshList = refreshList;
    }

    @Override
    public void onResume() {
        if (refreshList) {
            initializeList();
            refreshList = false;
        }
        super.onResume();
    }
}
