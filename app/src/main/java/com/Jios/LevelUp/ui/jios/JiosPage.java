package com.Jios.LevelUp.ui.jios;

import java.util.ArrayList;
import java.util.Arrays;

import com.ActivityOccasionItem;
import com.LikeOccasionItem;
import com.MainActivity;
import com.UserProfile;
import com.example.LevelUp.ui.jios.JiosFragment;
import com.example.LevelUp.ui.mylist.MylistFragment;
import com.example.tryone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class JiosPage extends AppCompatActivity {

    private ImageView imageView;
    private ToggleButton addButton;
    private ToggleButton likeButton;
    private TextView titleView;
    private TextView dateView;
    private TextView timeView;
    private TextView locationView;
    private TextView descriptionView;
    private TextView creatorView;
    private TextView numLikesView;
    private int position;
    private ArrayList<JiosItem> jiosItemArrayList = JiosFragment.getJiosItemList();
    private Context mContext = this;
    private StorageReference profileStorageRef;
    private FirebaseDatabase firebaseDatabase;

    private String uid;
    private String creatorName;
    private int creatorResidence;
    private String profilePictureUri;
    private String email;
    private long phone;
    private String telegram;


    private boolean changes = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.occasion_page);
        firebaseDatabase = FirebaseDatabase.getInstance();
        profileStorageRef = FirebaseStorage.getInstance()
                .getReference("profile picture uploads");
        imageView = findViewById(R.id.event_page_image);
        addButton = findViewById(R.id.events_page_image_add);
        likeButton = findViewById(R.id.events_page_image_like);
        titleView = findViewById(R.id.event_page_title);
        dateView = findViewById(R.id.event_page_date);
        timeView = findViewById(R.id.event_page_time);
        locationView = findViewById(R.id.event_page_location);
        descriptionView = findViewById(R.id.event_page_description);
        creatorView = findViewById(R.id.event_page_creator);
        numLikesView = findViewById(R.id.numlikes_textview);


        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        creatorName = intent.getStringExtra("creatorName");
        creatorResidence = intent.getIntExtra("residence", 0);
        telegram = intent.getStringExtra("telegram");
        email = intent.getStringExtra("email");
        phone = intent.getLongExtra("phone", 0);

        String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String location = intent.getStringExtra("location");
        String description = intent.getStringExtra("description");
        boolean isChecked = intent.getBooleanExtra("stateChecked", true);
        boolean isLiked = intent.getBooleanExtra("stateLiked", true);
        int numLikes = intent.getIntExtra("numLikes", 0);
        final ArrayList<Integer> numLikesArrLi = new ArrayList<>(Arrays.asList(numLikes));
        final String jioID = intent.getStringExtra("jioID");
        position = intent.getIntExtra("position", 0);
        final String userID = MainActivity.getCurrUser().getId();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("creatorfid", uid);
                intent.putExtra("name", creatorName);
                intent.putExtra("residence", creatorResidence);
                intent.putExtra("dpUri", profilePictureUri);
                intent.putExtra("telegram", telegram);
                intent.putExtra("email", email);
                intent.putExtra("phone", phone);
                getApplicationContext().startActivity(intent);
            }
        });

        creatorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("creatorfid", uid);
                intent.putExtra("name", creatorName);
                intent.putExtra("residence", creatorResidence);
                intent.putExtra("dpUri", profilePictureUri);
                intent.putExtra("telegram", telegram);
                intent.putExtra("email", email);
                intent.putExtra("phone", phone);
                getApplicationContext().startActivity(intent);
            }
        });

        StorageReference profileStorageRefIndiv = profileStorageRef.child(uid);
        profileStorageRefIndiv.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageView.setImageResource(R.drawable.fake_user_dp);
            }
        });
        titleView.setText(title);
        dateView.setText(date);
        timeView.setText(time);
        locationView.setText(location);
        descriptionView.setText(description);
        creatorView.setText(creatorName);

        numLikesView.setText(Integer.toString(numLikes));

        if (isChecked) {
            addButton.setBackgroundResource(R.drawable.ic_done_black_24dp);
        } else {
            addButton.setBackgroundResource(R.drawable.ic_add_black_24dp);
        }

        addButton.setChecked(isChecked);

        addButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changes = true;
                if (isChecked) {
                    // change to tick
                    addButton.setBackgroundResource(R.drawable.ic_done_black_24dp);

                    // add to ActivityEvent firebase
                    DatabaseReference activityJioRef = firebaseDatabase.getReference("ActivityJio");
                    ActivityOccasionItem activityOccasionItem = new ActivityOccasionItem(jioID, userID);
                    activityJioRef.push().setValue(activityOccasionItem);

                    Toast.makeText(mContext, "Jio added to your list!", Toast.LENGTH_SHORT).show();
                } else {
                    // change back to plus
                    addButton.setBackgroundResource(R.drawable.ic_add_black_24dp);

                    // delete the entry from activity DB
                    final DatabaseReference activityJioRef = firebaseDatabase.getReference("ActivityJio");
                    activityJioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                                if (jioID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
                                    String key = snapshot.getKey();
                                    activityJioRef.child(key).removeValue();
                                    Toast.makeText(mContext, "Jio removed from your list", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    MainActivity.getJioIds().remove(jioID);
                }
            }
        });

        if (isLiked) {
            likeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
        } else {
            likeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
        }

        likeButton.setChecked(isLiked);

        likeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changes = true;
                if (isChecked) {
                    likeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);

                    // send to Database
                    DatabaseReference likeJioRef = firebaseDatabase.getReference("LikeJio");
                    LikeOccasionItem likeOccasionItem = new LikeOccasionItem(jioID, userID);
                    likeJioRef.push().setValue(likeOccasionItem);

                    // +1 to the Likes on the jiosItem
                    int currLikes = numLikesArrLi.get(0);
                    DatabaseReference jioRef = firebaseDatabase.getReference("Jios");
                    jioRef.child(jioID).child("numLikes").setValue(currLikes + 1);
                    numLikesArrLi.set(0, currLikes + 1);
                    numLikesView.setText(Integer.toString(currLikes + 1)); // for display only
                } else {
                    likeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);

                    // delete the entry from like DB
                    final DatabaseReference likeJioRef = firebaseDatabase.getReference("LikeJio");
                    likeJioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                                if (jioID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
                                    String key = snapshot.getKey();
                                    likeJioRef.child(key).removeValue();
                                    Toast.makeText(mContext, "Unliked", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    // -1 to the Likes on the jioItem
                    int currLikes = numLikesArrLi.get(0);
                    DatabaseReference jioRef = firebaseDatabase.getReference("Jios");
                    jioRef.child(jioID).child("numLikes").setValue(currLikes - 1);
                    numLikesArrLi.set(0, currLikes - 1);
                    numLikesView.setText(Integer.toString(currLikes - 1)); // for display only

                    MainActivity.getLikeJioIDs().remove(jioID);
                }
            }
        });


    }

    public void setProfilePictureUri(String profilePictureUri) {
        this.profilePictureUri = profilePictureUri;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelegram(String telegram) {
        this.telegram = telegram;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    @Override
    public void onBackPressed() {
        if (changes) {
            JiosFragment.setRefresh(true);
        }
        MylistFragment.setRefreshList(true);
        JiosMyListFragment.setRefreshList(true);
        super.onBackPressed();
    }
}
