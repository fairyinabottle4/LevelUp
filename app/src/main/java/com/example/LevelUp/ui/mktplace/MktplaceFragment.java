package com.example.LevelUp.ui.mktplace;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Mktplace.LevelUp.ui.mktplace.MktplaceAdapter;
import com.Mktplace.LevelUp.ui.mktplace.MktplaceAdder;
import com.Mktplace.LevelUp.ui.mktplace.MktplaceItem;
import com.example.tryone.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MktplaceFragment extends Fragment {
    ArrayList<MktplaceItem> mktplaceItemList;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MktplaceAdapter mAdapter;
    private View rootView;
    public FloatingActionButton floatingActionButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mktplace, container, false);

        createMktplaceList();
        buildRecyclerView();

        floatingActionButton = rootView.findViewById(R.id.fab_mktplace);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MktplaceAdder.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    /*
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mktplaceViewModel =
                ViewModelProviders.of(this).get(MktplaceViewModel.class);
        View root = inflater.inflate(R.layout.fragment_mktplace, container, false);
        final TextView textView = root.findViewById(R.id.text_mktplace);
        mktplaceViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

     */

    public void createMktplaceList() {
        mktplaceItemList = new ArrayList<>();
        mktplaceItemList.add(new MktplaceItem(R.drawable.joy_con, "Nintendo Switch Joy Cons"));
        mktplaceItemList.add(new MktplaceItem(R.drawable.mosquito_catcher, "Mosquito Catcher!! Deal with mozzies in your room!"));
        mktplaceItemList.add(new MktplaceItem(R.drawable.iphone_ipad_charger, "iPhone/iPad charger"));
        mktplaceItemList.add(new MktplaceItem(R.drawable.night_lamp, "USB Night Lamp"));
        mktplaceItemList.add(new MktplaceItem(R.drawable.laptop_case, "Laptop Case 13 inch"));
        mktplaceItemList.add(new MktplaceItem(R.drawable.table_fan, "USB table fan"));
        mktplaceItemList.add(new MktplaceItem(R.drawable.ic_android_black_24dp, "Line 1"));
        mktplaceItemList.add(new MktplaceItem(R.drawable.ic_launcher_foreground, "Line 3"));
        mktplaceItemList.add(new MktplaceItem(R.drawable.ic_launcher_background, "Line 5"));
        mktplaceItemList.add(new MktplaceItem(R.drawable.ic_android_black_24dp, "Line 1"));
        mktplaceItemList.add(new MktplaceItem(R.drawable.ic_launcher_foreground, "Line 3"));
        mktplaceItemList.add(new MktplaceItem(R.drawable.ic_launcher_background, "Line 5"));
    }

    public void buildRecyclerView() {
        mRecyclerView = rootView.findViewById(R.id.recyclerview);
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mAdapter = new MktplaceAdapter(mktplaceItemList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}
