package com.Mktplace.LevelUp.ui.mktplace;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.Events.LevelUp.ui.events.EventsMyListFragment;
import com.LikeOccasionItem;
import com.MainActivity;
import com.UserItem;
import com.bumptech.glide.Glide;
import com.example.LevelUp.ui.events.EventsFragment;
import com.example.LevelUp.ui.mktplace.MktplaceFragment;
import com.example.LevelUp.ui.mylist.MylistFragment;
import com.example.tryone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class MktplacePage extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;
    private TextView mTextView4;
    private TextView mNumLikesTextView;

    private String url;
    private String title;
    private String location;
    private String description;
    private String creatorID;
    private String mktplaceID;

    private boolean isLiked;
    private int numLikes;
    private ToggleButton mLikeButton;
    private FirebaseDatabase mFirebaseDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marketplace_page);
        mImageView = findViewById(R.id.listing_image_full);
        mTextView1 = findViewById(R.id.listing_title_full);
        mTextView2 = findViewById(R.id.meetup_location_full);
        mTextView3 = findViewById(R.id.listing_description_full);
        mTextView4 = findViewById(R.id.creator_name);
        mNumLikesTextView = findViewById(R.id.numlikes_textview);
        mLikeButton = findViewById(R.id.image_like);

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        title = extras.getString("title");
        location = extras.getString("location");
        description = extras.getString("description");
        url = extras.getString("imageurl");
        creatorID = extras.getString("creatorID");
        isLiked = extras.getBoolean("stateLiked", false);
        numLikes = extras.getInt("numLikes", 0);
        mktplaceID = extras.getString("mktplaceID");

        mNumLikesTextView.setText(Integer.toString(numLikes));

        if (isLiked) {
            mLikeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
        } else {
            mLikeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
        }

        mLikeButton.setChecked(isLiked);

        DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserItem selected = snapshot.getValue(UserItem.class);
                    String id = selected.getId();

                    if (creatorID.equals(id)) {
                        String name = selected.getName();
                        mTextView4.setText(name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Toast.makeText(this, creatorID, Toast.LENGTH_SHORT).show();


        /*
        url = intent.getStringExtra("imageurl");
        title = intent.getStringExtra("title");
        location = intent.getStringExtra("location");
        description = intent.getStringExtra("description");
         */

        Glide.with(mImageView.getContext()).load(url).into(mImageView);
        mTextView1.setText(title);
        mTextView2.setText(location);
        mTextView3.setText(description);

        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final ArrayList<Integer> numLikesArrLi = new ArrayList<>(Arrays.asList(numLikes));

        mLikeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mLikeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);

                    // send to Database
                    DatabaseReference mLikeMktplaceRef = mFirebaseDatabase.getReference("LikeMktplace");
                    LikeOccasionItem likeOccasionItem = new LikeOccasionItem(mktplaceID, userID);
                    mLikeMktplaceRef.push().setValue(likeOccasionItem);

                    // +1 to the Likes on the eventItem
                    int currLikes = numLikesArrLi.get(0);
                    DatabaseReference mEventRef = mFirebaseDatabase.getReference("mktplace uploads");
                    mEventRef.child(mktplaceID).child("numLikes").setValue(currLikes + 1);
                    numLikesArrLi.set(0, currLikes + 1);
                    mNumLikesTextView.setText(Integer.toString(currLikes + 1)); // for display only

                } else {
                    mLikeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);

                    // delete the entry from like DB
                    final DatabaseReference mLikeMktplaceRef = mFirebaseDatabase.getReference("LikeMktplace");
                    mLikeMktplaceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                                if (mktplaceID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
                                    String key = snapshot.getKey();
                                    mLikeMktplaceRef.child(key).removeValue();
                                    Toast.makeText(getApplicationContext(), "Unliked", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    // -1 to the Likes on the mktplaceItem
                    int currLikes = numLikesArrLi.get(0);
                    DatabaseReference mMktplaceRef = mFirebaseDatabase.getReference("mktplace uploads");
                    mMktplaceRef.child(mktplaceID).child("numLikes").setValue(currLikes - 1);
                    numLikesArrLi.set(0, currLikes - 1);
                    mNumLikesTextView.setText(Integer.toString(currLikes - 1)); // for display only

                    MainActivity.mLikeMktplaceIDs.remove(mktplaceID);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
//        MylistFragment.setRefreshList(true);
        MktplaceFragment.setRefresh(true);
        super.onBackPressed();
    }

}
