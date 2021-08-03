package com.Jios.LevelUp.ui.jios;

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

import com.ActivityOccasionItem;
import com.LikeOccasionItem;
import com.MainActivity;
import com.UserItem;
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

import java.util.ArrayList;
import java.util.Arrays;

public class JiosPage extends AppCompatActivity {

    private ImageView mImageView;
    private ToggleButton mAddButton;
    private ToggleButton mLikeButton;
    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;
    private TextView mTextView4;
    private TextView mTextView5;
    private TextView mTextView6;
    private TextView mNumLikes;
    private int position;
    private ArrayList<JiosItem> jiosItemArrayList = JiosFragment.getJiosItemList();
    private Context mContext = this;
    private StorageReference mProfileStorageRef;
    private FirebaseDatabase mFirebaseDatabase;

    String uid;
    String creatorName;
    int creatorResidence;
    String profilePictureUri;
    String email;
    long phone;
    String telegram;


    private boolean changes = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.occasion_page);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mProfileStorageRef = FirebaseStorage.getInstance()
                .getReference("profile picture uploads");
        mImageView = findViewById(R.id.event_page_image);
        mAddButton = findViewById(R.id.events_page_image_add);
        mLikeButton = findViewById(R.id.events_page_image_like);
        mTextView1 = findViewById(R.id.event_page_title);
        mTextView2 = findViewById(R.id.event_page_date);
        mTextView3 = findViewById(R.id.event_page_time);
        mTextView4 = findViewById(R.id.event_page_location);
        mTextView5 = findViewById(R.id.event_page_description);
        mTextView6 = findViewById(R.id.event_page_creator);
        mNumLikes = findViewById(R.id.numlikes_textview);


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
        final String userID = MainActivity.currUser.getId();

        mImageView.setOnClickListener(new View.OnClickListener() {
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

        mTextView6.setOnClickListener(new View.OnClickListener() {
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

        StorageReference mProfileStorageRefIndiv = mProfileStorageRef.child(uid);
        mProfileStorageRefIndiv.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(mImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mImageView.setImageResource(R.drawable.fake_user_dp);
            }
        });
        // mImageView.setImageResource(R.mipmap.ic_launcher_round);
        // mAddButton.setImageResource(R.drawable.ic_add_black_24dp);
        mTextView1.setText(title);
        mTextView2.setText(date);
        mTextView3.setText(time);
        mTextView4.setText(location);
        mTextView5.setText(description);
        mTextView6.setText(creatorName);

        mNumLikes.setText(Integer.toString(numLikes));

        if (isChecked) {
            mAddButton.setBackgroundResource(R.drawable.ic_done_black_24dp);
        } else {
            mAddButton.setBackgroundResource(R.drawable.ic_add_black_24dp);
        }

        mAddButton.setChecked(isChecked);

        mAddButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changes = true;
                if (isChecked) {
                    // change to tick
                    mAddButton.setBackgroundResource(R.drawable.ic_done_black_24dp);

                    // add to ActivityEvent firebase
                    DatabaseReference mActivityJioRef = mFirebaseDatabase.getReference("ActivityJio");
                    ActivityOccasionItem activityOccasionItem = new ActivityOccasionItem(jioID, userID);
                    mActivityJioRef.push().setValue(activityOccasionItem);

                    Toast.makeText(mContext, "Jio added to your list!", Toast.LENGTH_SHORT).show();
                } else {
                    // change back to plus
                    mAddButton.setBackgroundResource(R.drawable.ic_add_black_24dp);

                    // delete the entry from activity DB
                    final DatabaseReference mActivityJioRef = mFirebaseDatabase.getReference("ActivityJio");
                    mActivityJioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                                if (jioID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
                                    String key = snapshot.getKey();
                                    mActivityJioRef.child(key).removeValue();
                                    Toast.makeText(mContext, "Jio removed from your list", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    MainActivity.mJioIDs.remove(jioID);
                }
            }
        });

        if (isLiked) {
            mLikeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
        } else {
            mLikeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
        }

        mLikeButton.setChecked(isLiked);

        mLikeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changes = true;
                if (isChecked) {
                    mLikeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);

                    // send to Database
                    DatabaseReference mLikeJioRef = mFirebaseDatabase.getReference("LikeJio");
                    LikeOccasionItem likeOccasionItem = new LikeOccasionItem(jioID, userID);
                    mLikeJioRef.push().setValue(likeOccasionItem);

                    // +1 to the Likes on the jiosItem
                    int currLikes = numLikesArrLi.get(0);
                    DatabaseReference mJioRef = mFirebaseDatabase.getReference("Jios");
                    mJioRef.child(jioID).child("numLikes").setValue(currLikes + 1);
                    numLikesArrLi.set(0, currLikes + 1);
                    mNumLikes.setText(Integer.toString(currLikes + 1)); // for display only
                } else {
                    mLikeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);

                    // delete the entry from like DB
                    final DatabaseReference mLikeJioRef = mFirebaseDatabase.getReference("LikeJio");
                    mLikeJioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                                if (jioID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
                                    String key = snapshot.getKey();
                                    mLikeJioRef.child(key).removeValue();
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
                    DatabaseReference mJioRef = mFirebaseDatabase.getReference("Jios");
                    mJioRef.child(jioID).child("numLikes").setValue(currLikes - 1);
                    numLikesArrLi.set(0, currLikes - 1);
                    mNumLikes.setText(Integer.toString(currLikes - 1)); // for display only

                    MainActivity.mLikeJioIDs.remove(jioID);
                }
            }
        });


    }

    public void setProfilePictureUri(String profilePictureUri) { this.profilePictureUri = profilePictureUri;}

    public void setEmail(String email) {this.email = email;}

    public void setTelegram(String telegram) { this.telegram = telegram;}

    public void setPhone(long phone) {this.phone = phone;}

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
