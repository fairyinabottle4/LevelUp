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
import com.MainActivity;
import com.UserItem;
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
    private int position;
    private ArrayList<JiosItem> jiosItemArrayList = JiosFragment.getJiosItemList();
    private Context mContext = this;
    private StorageReference mProfileStorageRef;
    private FirebaseDatabase mFirebaseDatabase;

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

        Intent intent = getIntent();
        String uid = intent.getStringExtra("uid");
        String creatorName = intent.getStringExtra("creatorName");
        String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String location = intent.getStringExtra("location");
        String description = intent.getStringExtra("description");
        boolean isChecked = intent.getBooleanExtra("stateChecked", true);
        final String jioID = intent.getStringExtra("jioID");
        position = intent.getIntExtra("position", 0);
        final String userID = MainActivity.currUser.getId();

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

//        mAddButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                JiosItem ji = jiosItemArrayList.get(position);
//                int index = JiosFragment.getJiosItemListCopy().indexOf(ji);
//
//                // add to ActivityJio firebase
//                UserItem user = MainActivity.currUser;
//                String jioID = ji.getJioID();
//                String userID = user.getId();
//                DatabaseReference mActivityJioRef = mFirebaseDatabase.getReference("ActivityJio");
//                ActivityOccasionItem activityOccasionItem = new ActivityOccasionItem(jioID, userID);
//                mActivityJioRef.push().setValue(activityOccasionItem);
//
//                Toast.makeText(mContext, "Jio added to your list!", Toast.LENGTH_SHORT).show();
//            }
//        });

//        String jioID = currentItem.getJioID();
//        if (MainActivity.mJioIDs.contains(jioID)) {
//            holder1.mAddButton.setBackgroundResource(R.drawable.ic_done_black_24dp);
//            holder1.mAddButton.setChecked(true);
//        } else {
//            holder1.mAddButton.setBackgroundResource(R.drawable.ic_add_black_24dp);
//            holder1.mAddButton.setChecked(false);
//        }


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



        mLikeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mLikeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
                    // do wtv u need to when user clicks liked button
                } else {
                    mLikeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                    // do wtv u need to when user unlikes an event
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (changes) {
            JiosFragment.setRefresh(true);
        }
        super.onBackPressed();
    }
}
