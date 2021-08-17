package com.Mktplace.LevelUp.ui.mktplace;

import java.util.ArrayList;
import java.util.Arrays;

import com.LikeOccasionItem;
import com.MainActivity;
import com.UserItem;
import com.UserProfile;
import com.bumptech.glide.Glide;
import com.example.LevelUp.ui.mktplace.MktplaceFragment;
import com.example.tryone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
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

public class MktplacePage extends AppCompatActivity {

    private ImageView imageView;
    private TextView titleView;
    private TextView locationView;
    private TextView descView;
    private TextView creatorView;
    private TextView numLikesView;

    private String url;
    private String title;
    private String location;
    private String description;
    private String creatorID;
    private String mktplaceID;

    private String creatorName;
    private int creatorResidence;
    private String profilePictureUri;
    private String telegram;
    private String email;
    private long phone;

    private boolean isLiked;
    private int numLikes;
    private ToggleButton likeButton;
    private FirebaseDatabase firebaseDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marketplace_page);
        imageView = findViewById(R.id.listing_image_full);
        titleView = findViewById(R.id.listing_title_full);
        locationView = findViewById(R.id.meetup_location_full);
        descView = findViewById(R.id.listing_description_full);
        creatorView = findViewById(R.id.creator_name);
        numLikesView = findViewById(R.id.numlikes_textview);
        likeButton = findViewById(R.id.image_like);

        firebaseDatabase = FirebaseDatabase.getInstance();

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
        creatorName = extras.getString("name");
        creatorResidence = extras.getInt("residence");
        profilePictureUri = extras.getString("profilePictureUri");
        telegram = extras.getString("telegram");
        email = extras.getString("email");
        phone = extras.getLong("phone");

        numLikesView.setText(Integer.toString(numLikes));

        if (isLiked) {
            likeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
        } else {
            likeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
        }

        likeButton.setChecked(isLiked);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserItem selected = snapshot.getValue(UserItem.class);
                    String id = selected.getId();

                    if (creatorID.equals(id)) {
                        String name = selected.getName();
                        creatorView.setText(name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Glide.with(imageView.getContext()).load(url).into(imageView);
        titleView.setText(title);
        locationView.setText(location);
        descView.setText(description);

        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final ArrayList<Integer> numLikesArrLi = new ArrayList<>(Arrays.asList(numLikes));

        likeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    likeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);

                    // send to Database
                    DatabaseReference likeMktplaceref = firebaseDatabase.getReference("LikeMktplace");
                    LikeOccasionItem likeOccasionItem = new LikeOccasionItem(mktplaceID, userID);
                    likeMktplaceref.push().setValue(likeOccasionItem);

                    // +1 to the Likes on the eventItem
                    int currLikes = numLikesArrLi.get(0);
                    DatabaseReference mEventRef = firebaseDatabase.getReference("mktplace uploads");
                    mEventRef.child(mktplaceID).child("numLikes").setValue(currLikes + 1);
                    numLikesArrLi.set(0, currLikes + 1);
                    numLikesView.setText(Integer.toString(currLikes + 1)); // for display only

                } else {
                    likeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);

                    // delete the entry from like DB
                    final DatabaseReference likeMktplaceref = firebaseDatabase.getReference("LikeMktplace");
                    likeMktplaceref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                                if (mktplaceID.equals(selected.getOccasionID())
                                    && userID.equals(selected.getUserID())) {
                                    String key = snapshot.getKey();
                                    likeMktplaceref.child(key).removeValue();
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
                    DatabaseReference mktplaceRef = firebaseDatabase.getReference("mktplace uploads");
                    mktplaceRef.child(mktplaceID).child("numLikes").setValue(currLikes - 1);
                    numLikesArrLi.set(0, currLikes - 1);
                    numLikesView.setText(Integer.toString(currLikes - 1)); // for display only

                    MainActivity.getLikeMktplaceIDs().remove(mktplaceID);
                }
            }
        });

        creatorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("creatorfid", creatorID);
                intent.putExtra("name", creatorName);
                intent.putExtra("residence", creatorResidence);
                intent.putExtra("dpUri", profilePictureUri);
                intent.putExtra("telegram", telegram);
                intent.putExtra("email", email);
                intent.putExtra("phone", phone);
                getApplicationContext().startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        MktplaceFragment.setRefresh(true);
        super.onBackPressed();
    }

}
