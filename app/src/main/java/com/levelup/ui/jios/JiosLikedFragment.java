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
import com.levelup.occasion.LikeOccasionItem;
import com.levelup.occasion.Occasion;
import com.levelup.ui.mylist.MylistLikedAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class JiosLikedFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MylistLikedAdapter mAdapter;
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
        tb.setTitle("Jios I Liked");


        buildRecyclerView();

        initializeList();

        return rootView;

    }

    public void buildRecyclerView() {
        mRecyclerView = rootView.findViewById(R.id.occMylistFragmentRecyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new MylistLikedAdapter(getActivity(), mOccasionJios);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initializeList() {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        final String fbUIDFinal = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabaseReferenceLikeJio = mFirebaseDatabase.getReference().child("LikeJio");
        mDatabaseReferenceLikeJio.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mJioIDs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                    String selectedUserID = selected.getUserID();
                    if (selectedUserID.equals(fbUIDFinal)) {
                        mJioIDs.add(selected.getOccasionID());
                        // Toast.makeText(MainActivity.this, mJioIDs.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference mDatabaseReferenceJios = mFirebaseDatabase.getReference().child("Jios");
        mDatabaseReferenceJios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mOccasionJios.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    JiosItem selected = snapshot.getValue(JiosItem.class);
                    String eventID = selected.getOccasionID();

                    if (mJioIDs.contains(eventID)) {
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
                        // mOccasionEvents.add(selected);
                    }
                }
                MainActivity.sort(mOccasionJios);
                MylistLikedAdapter mylistAdapter = new MylistLikedAdapter(getActivity(), mOccasionJios);
                mAdapter = mylistAdapter;
                mRecyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void setRefreshList(boolean refreshList) {
        JiosLikedFragment.refreshList = refreshList;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (refreshList) {
            initializeList();
            refreshList = false;
        }
        super.onCreate(savedInstanceState);
    }
}
