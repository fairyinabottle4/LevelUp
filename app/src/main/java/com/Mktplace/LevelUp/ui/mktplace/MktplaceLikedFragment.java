package com.Mktplace.LevelUp.ui.mktplace;

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

import com.Events.LevelUp.ui.events.EventsItem;
import com.LikeOccasionItem;
import com.MainActivity;
import com.Mylist.LevelUp.ui.mylist.MylistLikedAdapter;
import com.example.LevelUp.ui.Occasion;
import com.example.tryone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MktplaceLikedFragment extends Fragment {
  private RecyclerView mRecyclerView;
  private RecyclerView.LayoutManager mLayoutManager;
  private MktplaceAdapter mAdapter;
  private View rootView;

  public static ArrayList<String> mMktplaceIDs = new ArrayList<>();
  public static ArrayList<MktplaceItem> mMktplaceItems = new ArrayList<>();

  // private static boolean refreshList;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.mktplace_plain_withtb, container, false);

    Toolbar tb = rootView.findViewById(R.id.mktplace_toolbar);
    AppCompatActivity activity = (AppCompatActivity) getActivity();
    activity.setSupportActionBar(tb);
    tb.setTitle("Listings I Liked");

    buildRecyclerView();

    initializeList();

    return rootView;

  }

  public void buildRecyclerView() {
    mRecyclerView = rootView.findViewById(R.id.recyclerview);
    mLayoutManager = new LinearLayoutManager(getContext());
    mAdapter = new MktplaceAdapter(getActivity(), mMktplaceItems);
    mRecyclerView.setLayoutManager(mLayoutManager);
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
  }

  private void initializeList() {
    FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    final String fbUIDFinal = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference mDatabaseReferenceLikeMktplace = mFirebaseDatabase.getReference().child("LikeMktplace");
    mDatabaseReferenceLikeMktplace.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        mMktplaceIDs.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
          String selectedUserID = selected.getUserID();
          if (selectedUserID.equals(fbUIDFinal)) {
            mMktplaceIDs.add(selected.getOccasionID());
            // Toast.makeText(MainActivity.this, mJioIDs.toString(), Toast.LENGTH_SHORT).show();
          }
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
    DatabaseReference mDatabaseReferenceMktplace = mFirebaseDatabase.getReference().child("mkplace uploads");
    mDatabaseReferenceMktplace.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        mMktplaceItems.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          MktplaceItem selected = snapshot.getValue(MktplaceItem.class);
          String mktplaceID = selected.getMktPlaceID();
          if (mMktplaceIDs.contains(mktplaceID)) {
            mMktplaceItems.add(selected);
          }
        }
        MktplaceAdapter mktplaceAdapter = new MktplaceAdapter(getActivity(), mMktplaceItems);
        mAdapter = mktplaceAdapter;
        mRecyclerView.setAdapter(mAdapter);

      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

}