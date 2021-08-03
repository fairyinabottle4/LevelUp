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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.LikeOccasionItem;
import com.example.tryone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MktplaceLikedFragment extends Fragment {
  private RecyclerView mRecyclerView;
  private RecyclerView.LayoutManager mLayoutManager;
  private MktplaceLikedAdapter mAdapter;
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
    mLayoutManager = new GridLayoutManager(getActivity(), 2);
    mAdapter = new MktplaceLikedAdapter(getActivity(), mMktplaceItems);
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
        mMktplaceItems.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          MktplaceItem selected = snapshot.getValue(MktplaceItem.class);
          String mktplaceID = selected.getMktPlaceID();
          if (mMktplaceIDs.contains(mktplaceID)) {
            mMktplaceItems.add(selected);
          }
        }
        MktplaceLikedAdapter mktplaceAdapter = new MktplaceLikedAdapter(getActivity(), mMktplaceItems);
        mAdapter = mktplaceAdapter;
        mRecyclerView.setAdapter(mAdapter);

      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

}