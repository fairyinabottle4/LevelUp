package com.levelup.ui.jios;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.levelup.R;
import com.levelup.activity.MainActivity;
import com.levelup.occasion.ActivityOccasionItem;
import com.levelup.occasion.Occasion;
import com.levelup.ui.mylist.MylistAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class JiosMyListFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MylistAdapter mAdapter;
    private View rootView;
    public static ArrayList<String> mJioIDs = new ArrayList<>();
    public static ArrayList<Occasion> mOccasionJios = new ArrayList<>();

    private static boolean refreshList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.occ_mylist_fragment, container, false);

        Toolbar tb = rootView.findViewById(R.id.occ_mylist_fragment_title);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(tb);
        tb.setTitle("Jios I Signed Up For");



        buildRecyclerView();

        initializeList();

        return rootView;

    }

    public void buildRecyclerView() {
        mRecyclerView = rootView.findViewById(R.id.occMylistFragmentRecyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new MylistAdapter(getActivity(), mOccasionJios);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initializeList() {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        final String fbUIDFinal = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabaseReferenceActivityJio = mFirebaseDatabase.getReference().child("ActivityJio");
        mDatabaseReferenceActivityJio.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mJioIDs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                    String selectedUserID = selected.getUserID();
                    if (selectedUserID.equals(fbUIDFinal)) {
                        mJioIDs.add(selected.getOccasionID());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference mDatabaseReferenceJios = mFirebaseDatabase.getReference().child("Jios");
        mDatabaseReferenceJios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mOccasionJios.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    JiosItem selected = snapshot.getValue(JiosItem.class);
                    String jioID = selected.getOccasionID();

                    if (mJioIDs.contains(jioID)) {
                        if (selected.getTimeInfo().length() > 4) {
                            continue;
                        }

                        int hour = Integer.parseInt(selected.getTimeInfo().substring(0,2));
                        int min = Integer.parseInt(selected.getTimeInfo().substring(2));

                        Date eventDateZero = selected.getDateInfo();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(eventDateZero);
                        cal.set(Calendar.HOUR_OF_DAY, hour);
                        cal.set(Calendar.MINUTE, min);
                        Date eventDate = cal.getTime();

                        Date currentDate = new Date();

                        if (eventDate.compareTo(currentDate) >= 0) {
                            mOccasionJios.add(selected);
                        }
                    }
                }
                MainActivity.sort(mOccasionJios);
                MylistAdapter mylistAdapter = new MylistAdapter(getActivity(), mOccasionJios);
                mAdapter = mylistAdapter;
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mAdapter.notifyDataSetChanged();
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
