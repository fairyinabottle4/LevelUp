package com.Mylist.LevelUp.ui.mylist;

import java.util.ArrayList;

import com.ActivityOccasionItem;
import com.UserItem;
import com.example.tryone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CreatorViewNames extends AppCompatActivity {
    private ArrayList<String> userIDs = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private String occID;
    private boolean isJio;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseRefActivityEvents;
    private DatabaseReference databaseRefActivityJios;

    private Toolbar tb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.creator_names_view);
        final ListView list = findViewById(R.id.names_list);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseRefActivityEvents = firebaseDatabase.getReference("ActivityEvent");
        databaseRefActivityJios = firebaseDatabase.getReference("ActivityJio");

        tb = findViewById(R.id.creator_names_toolbar);
        setSupportActionBar(tb);

        Intent intent = getIntent();
        occID = intent.getStringExtra("occID");
        isJio = intent.getBooleanExtra("isJio", true);

        if (isJio) {
            // search AJ
            databaseRefActivityJios.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                        if (occID.equals(selected.getOccasionID())) {
                            final String userID = selected.getUserID();
                            firebaseDatabase.getReference("Users")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            UserItem selectedUser = snapshot.getValue(UserItem.class);
                                            if (userID.equals(selectedUser.getId())) {
                                                names.add("(" + intToRes(selectedUser.getResidential())
                                                    + ") " + selectedUser.getName());
                                            }
                                        }

                                        ArrayAdapter adapter = new ArrayAdapter(
                                            CreatorViewNames.this,
                                            android.R.layout.simple_list_item_1, names);
                                        list.setAdapter(adapter);
                                        getSupportActionBar().setTitle(names.size()
                                            + (names.size() == 1 ? " Person " : " People ")
                                            + "Signed Up");
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
            databaseRefActivityEvents.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                        if (occID.equals(selected.getOccasionID())) {
                            final String userID = selected.getUserID();
                            firebaseDatabase.getReference("Users")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            UserItem selectedUser = snapshot.getValue(UserItem.class);
                                            if (userID.equals(selectedUser.getId())) {
                                                names.add("(" + intToRes(selectedUser.getResidential())
                                                    + ") " + selectedUser.getName());
                                            }
                                        }

                                        ArrayAdapter adapter = new ArrayAdapter(
                                            CreatorViewNames.this,
                                                android.R.layout.simple_list_item_1, names);
                                        list.setAdapter(adapter);

                                        getSupportActionBar().setTitle(names.size()
                                            + (names.size() == 1 ? " Person " : " People ")
                                            + "Signed Up");

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
        super.onCreate(savedInstanceState);
    }

    /**
     * Sets the residence name based on the pre-coded number
     *
     * @param x The number representing each residence
     */
    public String intToRes(int x) {
        String residenceName = "";
        if (x == 0) {
            residenceName = "Off Campus";
        }
        if (x == 1) {
            residenceName = "Cinnamon";
        }
        if (x == 2) {
            residenceName = "Tembusu";
        }
        if (x == 3) {
            residenceName = "CAPT";
        }
        if (x == 4) {
            residenceName = "RC4";
        }
        if (x == 5) {
            residenceName = "RVRC";
        }
        if (x == 6) {
            residenceName = "Eusoff";
        }
        if (x == 7) {
            residenceName = "Kent Ridge";
        }
        if (x == 8) {
            residenceName = "King Edward VII";
        }
        if (x == 9) {
            residenceName = "Raffles";
        }
        if (x == 10) {
            residenceName = "Sheares";
        }
        if (x == 11) {
            residenceName = "Temasek";
        }
        if (x == 12) {
            residenceName = "PGP House";
        }
        if (x == 13) {
            residenceName = "PGP Residences";
        }
        if (x == 14) {
            residenceName = "UTown Residence";
        }
        return residenceName;
    }
}
