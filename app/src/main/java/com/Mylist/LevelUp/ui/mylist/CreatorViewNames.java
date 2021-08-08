package com.Mylist.LevelUp.ui.mylist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ActivityOccasionItem;
import com.Jios.LevelUp.ui.jios.JiosItem;
import com.UserItem;
import com.example.tryone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CreatorViewNames extends AppCompatActivity {
    public ArrayList<String> userIDs = new ArrayList<>();
    public ArrayList<String> names = new ArrayList<>();
    public String occID;
    public boolean isJio;

    public FirebaseDatabase mFirebaseDatabase;
    public DatabaseReference mDatabaseRefActivityEvents;
    public DatabaseReference mDatabaseRefActivityJios;

    public Toolbar tb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.creator_names_view);
        final ListView list = findViewById(R.id.names_list);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseRefActivityEvents = mFirebaseDatabase.getReference("ActivityEvent");
        mDatabaseRefActivityJios = mFirebaseDatabase.getReference("ActivityJio");

        tb = findViewById(R.id.creator_names_toolbar);
        setSupportActionBar(tb);

        Intent intent = getIntent();
        occID = intent.getStringExtra("occID");
        // Toast.makeText(this, occID, Toast.LENGTH_SHORT).show();
        isJio = intent.getBooleanExtra("isJio", true);

        if (isJio) {
            // search AJ
            mDatabaseRefActivityJios.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                        if (occID.equals(selected.getOccasionID())) {
                            // userIDs.add(selected.getUserID());
                            final String userID = selected.getUserID();
                            mFirebaseDatabase.getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        UserItem selectedUser = snapshot.getValue(UserItem.class);
                                        if (userID.equals(selectedUser.getId())) {
                                            names.add("(" + intToRes(selectedUser.getResidential()) + ") " + selectedUser.getName());
                                        }
                                    }

                                    ArrayAdapter adapter = new ArrayAdapter(CreatorViewNames.this, android.R.layout.simple_list_item_1, names);
                                    list.setAdapter(adapter);
                                    getSupportActionBar().setTitle(names.size() + (names.size() == 1 ? " Person " : " People ") + "Signed Up");
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            // search AE
            mDatabaseRefActivityEvents.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                        if (occID.equals(selected.getOccasionID())) {
                            final String userID = selected.getUserID();
                            mFirebaseDatabase.getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        UserItem selectedUser = snapshot.getValue(UserItem.class);
                                        if (userID.equals(selectedUser.getId())) {
                                            names.add("(" + intToRes(selectedUser.getResidential()) + ") " + selectedUser.getName());
                                        }
                                    }

                                    ArrayAdapter adapter = new ArrayAdapter(CreatorViewNames.this, android.R.layout.simple_list_item_1, names);
                                    list.setAdapter(adapter);

                                    getSupportActionBar().setTitle(names.size() + (names.size() == 1 ? " Person " : " People ") + "Signed Up");

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

//        if (names.size() == 0) {
//            names.add("No one has signed up yet");
//            ArrayAdapter adapter = new ArrayAdapter(CreatorViewNames.this, android.R.layout.simple_list_item_1, names);
//            list.setAdapter(adapter);
//        }

        super.onCreate(savedInstanceState);
    }

    /**
     * Sets the residence name based on the pre-coded number
     *
     * @param x The number representing each residence
     */
    public String intToRes(int x) {
        String residence_name = "";
        if (x == 0) {
            residence_name = "Off Campus";
        }
        if (x == 1) {
            residence_name = "Cinnamon";
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
        if (x == 5) {
            residence_name = "RVRC";
        }
        if (x == 6) {
            residence_name = "Eusoff";
        }
        if (x == 7) {
            residence_name = "Kent Ridge";
        }
        if (x == 8) {
            residence_name = "King Edward VII";
        }
        if (x == 9) {
            residence_name = "Raffles";
        }
        if (x == 10) {
            residence_name = "Sheares";
        }
        if (x == 11) {
            residence_name = "Temasek";
        }
        if (x == 12) {
            residence_name = "PGP House";
        }
        if (x == 13) {
            residence_name = "PGP Residences";
        }
        if (x == 14) {
            residence_name = "UTown Residence";
        }
        return residence_name;
    }
}
