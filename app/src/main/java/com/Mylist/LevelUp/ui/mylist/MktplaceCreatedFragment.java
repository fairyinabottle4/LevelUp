package com.Mylist.LevelUp.ui.mylist;

import java.util.ArrayList;

import com.Mktplace.LevelUp.ui.mktplace.MktplaceItem;
import com.Mktplace.LevelUp.ui.mktplace.MktplacePage;
import com.example.tryone.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MktplaceCreatedFragment extends Fragment implements MktplaceCreatedAdapter.OnItemClickListener {
    private static boolean refresh;

    private static ArrayList<MktplaceItem> mktplaceItemList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MktplaceCreatedAdapter adapter;
    private View rootView;
    private FloatingActionButton floatingActionButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseReference databaseRef;
    private MktplaceCreatedAdapter.OnItemClickListener mktplaceListener = this;
    private TextView nothingView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.mktplace_plain, container, false);
        nothingView = rootView.findViewById(R.id.nothing);

        createMktplaceList();

        buildRecyclerView();

        databaseRef = FirebaseDatabase.getInstance().getReference("mktplace uploads");

        loadDataMktplace();

        return rootView;
    }

    public void createMktplaceList() {
        mktplaceItemList = new ArrayList<>();
    }

    /**
     * Builds the recycler view which contains the list of MktplaceItems
     */
    public void buildRecyclerView() {
        recyclerView = rootView.findViewById(R.id.recyclerview);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        adapter = new MktplaceCreatedAdapter(getContext(), mktplaceItemList, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    public void onItemClick(int position) {
        MktplaceItem mItem = mktplaceItemList.get(position);
        Intent intent = new Intent(getActivity(), MktplacePage.class);
        intent.putExtra("description", mItem.getDescription());
        intent.putExtra("title", mItem.getName());
        intent.putExtra("location", mItem.getLocation());
        intent.putExtra("imageurl", mItem.getImageUrl());
        intent.putExtra("creatorID", mItem.getCreatorID());

        startActivity(intent);
    }

    public static void setMktplaceItemList(ArrayList<MktplaceItem> list) {
        mktplaceItemList = list;
    }

    /**
     * Loads data from the database into an ArrayList which will be displayed in the Fragment
     */
    public void loadDataMktplace() {
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mktplaceItemList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    MktplaceItem upload = postSnapshot.getValue(MktplaceItem.class);
                    if (userID.equals(upload.getCreatorID())) {
                        mktplaceItemList.add(upload);
                    }
                }
                if (mktplaceItemList.isEmpty()) {
                    nothingView.setVisibility(View.VISIBLE);
                }
                adapter = new MktplaceCreatedAdapter(getActivity(), mktplaceItemList, mktplaceListener);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        adapter.notifyDataSetChanged();
    }

    public static void setRefresh(boolean toSet) {
        refresh = toSet;
    }

    @Override
    public void onResume() {
        if (refresh) {
            loadDataMktplace();
            refresh = false;
        }
        super.onResume();
    }
}
