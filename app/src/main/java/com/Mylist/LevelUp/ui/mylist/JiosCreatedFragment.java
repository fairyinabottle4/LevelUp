package com.Mylist.LevelUp.ui.mylist;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.Jios.LevelUp.ui.jios.JiosItem;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class JiosCreatedFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MylistCreatedAdapter adapter;
    private View rootView;
    private ArrayList<Occasion> occasionJios = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.occ_mylist_fragment_plain, container, false);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final String fbUidFinal = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReferenceJios = firebaseDatabase.getReference().child("Jios");
        databaseReferenceJios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                occasionJios.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    JiosItem selected = snapshot.getValue(JiosItem.class);
                    String creatorID = selected.getCreatorID();
                    if (creatorID.equals(fbUidFinal)) {
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
                            occasionJios.add(selected);
                        }
                    }
                }
                MylistCreatedAdapter mylistCreatedAdapter = new MylistCreatedAdapter(getActivity(), occasionJios);
                adapter = mylistCreatedAdapter;
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        buildRecyclerView();
        return rootView;
    }

    /**
     * Builds the recycler view to display the list of items
     */
    public void buildRecyclerView() {
        recyclerView = rootView.findViewById(R.id.occMylistFragmentRecyclerView);
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new MylistCreatedAdapter(getActivity(), occasionJios);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}
