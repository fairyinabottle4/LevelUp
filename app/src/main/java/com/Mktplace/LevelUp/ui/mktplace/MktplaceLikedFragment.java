package com.Mktplace.LevelUp.ui.mktplace;

import java.util.ArrayList;

import com.LikeOccasionItem;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MktplaceLikedFragment extends Fragment {

    private static ArrayList<String> mktplaceIDs = new ArrayList<>();
    private static ArrayList<MktplaceItem> mktplaceItems = new ArrayList<>();

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MktplaceLikedAdapter adapter;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.mktplace_plain_withtb, container, false);

        Toolbar toolbar = rootView.findViewById(R.id.mktplace_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle("Listings I Liked");

        buildRecyclerView();

        initializeList();

        return rootView;

    }

    /**
     * Method that builds the recycler view so that the listings will be shown in a list format.
     */
    public void buildRecyclerView() {
        recyclerView = rootView.findViewById(R.id.recyclerview);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        adapter = new MktplaceLikedAdapter(getActivity(), mktplaceItems);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initializeList() {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        final String fbUidFinal = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseRefLikeMktplace = mFirebaseDatabase.getReference().child("LikeMktplace");
        databaseRefLikeMktplace.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mktplaceIDs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                    String selectedUserID = selected.getUserID();
                    if (selectedUserID.equals(fbUidFinal)) {
                        mktplaceIDs.add(selected.getOccasionID());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference mDatabaseReferenceMktplace = mFirebaseDatabase.getReference().child("mktplace uploads");
        mDatabaseReferenceMktplace.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mktplaceItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MktplaceItem selected = snapshot.getValue(MktplaceItem.class);
                    String mktplaceID = selected.getMktPlaceID();
                    if (mktplaceIDs.contains(mktplaceID)) {
                        mktplaceItems.add(selected);
                    }
                }
                MktplaceLikedAdapter mktplaceAdapter = new MktplaceLikedAdapter(getActivity(), mktplaceItems);
                adapter = mktplaceAdapter;
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
