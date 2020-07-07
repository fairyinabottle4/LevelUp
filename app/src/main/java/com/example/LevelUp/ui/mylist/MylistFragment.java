package com.example.LevelUp.ui.mylist;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Events.LevelUp.ui.events.EventsItem;
import com.Jios.LevelUp.ui.jios.JiosItem;
import com.MainActivity;
import com.Mylist.LevelUp.ui.mylist.EditUserInfoActivity;
import com.Mylist.LevelUp.ui.mylist.MylistAdapter;
import com.UserItem;
import com.example.LevelUp.ui.Occasion;
import com.example.LevelUp.ui.jios.JiosFragment;
import com.example.tryone.R;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;

public class MylistFragment extends Fragment {
    ArrayList<Occasion> mOccasionListEventsInitial = new ArrayList<>();
    ArrayList<Occasion> mOccasionListJiosInitial = new ArrayList<>();
    // ArrayList<Occasion> mOccasionListReal = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MylistAdapter mAdapter;
    private View rootView;
    private static ArrayList<Integer> numberEvents = new ArrayList<>();
    private static ArrayList<Integer> numberJios = new ArrayList<>();
    private FirebaseDatabase mDatabaseEvents;
    private DatabaseReference mDatabaseReferenceEvents;
    private FirebaseDatabase mDatabaseJios;
    private DatabaseReference mDatabaseReferenceJios;
    private DatabaseReference mDatabaseReferenceUser;

    ValueEventListener mValueEventListenerEvents;
    ValueEventListener mValueEventListenerJios;

    private ImageButton editUserInfoBtn;

    private StorageReference mProfileStorageRef;
//    private DatabaseReference mUserDatabaseRef;
//    private StorageReference mProfilePicRef;

    private UserItem user;
    private Uri profilePicURI = EditUserInfoActivity.profileImageUri;
    private String display_name;
    private String residence_name;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mylist, container, false);
        final TextView name = rootView.findViewById(R.id.user_display_name);
        final TextView resi = rootView.findViewById(R.id.user_display_resi);
        final ImageView profilePic = rootView.findViewById(R.id.user_display_picture);

        final String fbUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (MainActivity.display_name != null) {
            name.setText(MainActivity.display_name);
        }
        if (MainActivity.display_residential != null) {
            resi.setText(MainActivity.display_residential);
        }
//        if (profilePicURI != null) {
//            profilePic.setImageURI(profilePicURI);
//        }

        mProfileStorageRef = FirebaseStorage.getInstance().getReference("profile picture uploads");
        // mUserDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mProfileStorageRef = mProfileStorageRef.child(fbUID);

        mProfileStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profilePic);
                profilePicURI = uri;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                profilePic.setImageResource(R.drawable.fake_user_dp);
            }
        });

        // Pulling user from Firebase
        mDatabaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserItem selected = snapshot.getValue(UserItem.class);
                    String id = selected.getId();

                    if (fbUID.equals(id)) {
                        user = selected;
                        String disp_name = user.getName();
                        MainActivity.display_name = disp_name;
                        name.setText(disp_name);

                        intToRes(user.getResidential());
                        resi.setText(residence_name);
                        MainActivity.display_residential = residence_name;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseEvents = FirebaseDatabase.getInstance();
        mDatabaseReferenceEvents = mDatabaseEvents.getReference().child("Events");

        mDatabaseJios = FirebaseDatabase.getInstance();
        mDatabaseReferenceJios = mDatabaseJios.getReference().child("Jios");

        // Events
        mValueEventListenerEvents = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mOccasionListEventsInitial.add(snapshot.getValue(EventsItem.class));
                }
                if (numberEvents.size() != 0) {
                    ArrayList<Occasion> mOLR = MainActivity.mOccasionListReal;
                    ArrayList<Integer> IDs = MainActivity.mEventsIDs;

                    for (int id : numberEvents){
                        if (!IDs.contains(id)) {
                            Occasion toAdd = mOccasionListEventsInitial.get(id);
                            mOLR.add(toAdd);
                            IDs.add(id);
                            Toast.makeText(getContext(), Integer.toString(id), Toast.LENGTH_SHORT).show();
                            MylistAdapter myListAdapter = new MylistAdapter(mOLR);
                            mRecyclerView.setAdapter(myListAdapter);
                            mAdapter = myListAdapter;
                        } else {
                            // This toast is temporary, it should change the + to a tick or smth
                            Toast.makeText(getContext(), "Event already added to your list", Toast.LENGTH_SHORT).show();
                            MylistAdapter myListAdapter = new MylistAdapter(mOLR);
                            mRecyclerView.setAdapter(myListAdapter);
                            mAdapter = myListAdapter;
                        }
                    }
                    MainActivity.sort(mOLR);
                    MainActivity.mOccasionListRealFull = new ArrayList<>(mOLR);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReferenceEvents.addValueEventListener(mValueEventListenerEvents);

        // Jios
        mValueEventListenerJios = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mOccasionListJiosInitial.add(snapshot.getValue(JiosItem.class));
                }

                if (numberJios.size() != 0) {
                    ArrayList<Occasion> mOLR = MainActivity.mOccasionListReal;
                    ArrayList<Integer> IDs = MainActivity.mJiosIDs;
                    for (int id : numberJios) {
                        if (!IDs.contains(id)) {
                            Occasion toAdd = mOccasionListJiosInitial.get(id);
                            mOLR.add(toAdd);

                            IDs.add(id);
                            Toast.makeText(getContext(), Integer.toString(id), Toast.LENGTH_SHORT).show();
                            MylistAdapter myListAdapter = new MylistAdapter(mOLR);
                            mRecyclerView.setAdapter(myListAdapter);
                            mAdapter = myListAdapter;
                        } else {
                            // This toast is temporary, it should change the + to a tick or smth
                            Toast.makeText(getContext(), "Jio already added to your list", Toast.LENGTH_SHORT).show();
                            MylistAdapter myListAdapter = new MylistAdapter(mOLR);
                            mRecyclerView.setAdapter(myListAdapter);
                            mAdapter = myListAdapter;
                        }
                    }
                    MainActivity.sort(mOLR);
                    MainActivity.mOccasionListRealFull = new ArrayList<>(mOLR);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReferenceJios.addValueEventListener(mValueEventListenerJios);

        // setting up toolbar
        setHasOptionsMenu(true);
        Toolbar toolbar = rootView.findViewById(R.id.mylist_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.setSupportActionBar(toolbar);

        buildRecyclerView();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        editUserInfoBtn = (ImageButton) getView().findViewById(R.id.edit_user_info_btn);

        editUserInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditUserInfoActivity.class);
                startActivity(intent);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    public void intToRes(int x) {
        if (x == 1) {
            residence_name = "Cinammon";
        }
        if (x == 2) {
            residence_name = "Tembusu";
        }
        if (x == 3) {
            residence_name = "CAPT";
        }
        if (x == 4) {
            residence_name = "RC4";
        }
    }

    public void buildRecyclerView() {
        mRecyclerView = rootView.findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new MylistAdapter(MainActivity.mOccasionListRealFull);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public static void setNumberEvents(int i) {
        numberEvents.add(i);
    }

    public static void setNumberJios(int i) {
        numberJios.add(i);
    }

//    @Override
//    public void onResume() {
//        if (refresh) {
//            refresh = false;
//            getActivity().getSupportFragmentManager().beginTransaction()
//                    .detach(MylistFragment.this)
//                    .attach(MylistFragment.this)
//                    .commit();
//        }
//        super.onResume();
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_search:

                MenuItem searchItem = item;
                SearchView searchView = (SearchView) searchItem.getActionView();
                // searchView.setQueryHint("Search");
                // searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                searchItem.setActionView(searchView);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        mAdapter.getFilter().filter(newText);
                        return false;
                    }
                });

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.mylist_top_menu, menu);

        // setting the search function UI
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search");

        // ???
        // searchItem.setOnMenuItemClickListener()

        super.onCreateOptionsMenu(menu, inflater);
    }
}
