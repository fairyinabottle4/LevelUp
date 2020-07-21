package com.Mktplace.LevelUp.ui.mktplace;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.UserItem;
import com.bumptech.glide.Glide;
import com.example.tryone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MktplacePage extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;
    private TextView mTextView4;

    private String url;
    private String title;
    private String location;
    private String description;
    private String creatorID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marketplace_page);
        mImageView = findViewById(R.id.listing_image_full);
        mTextView1 = findViewById(R.id.listing_title_full);
        mTextView2 = findViewById(R.id.meetup_location_full);
        mTextView3 = findViewById(R.id.listing_description_full);
        mTextView4 = findViewById(R.id.creator_name);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        title = extras.getString("title");
        location = extras.getString("location");
        description = extras.getString("description");
        url = extras.getString("imageurl");
        creatorID = extras.getString("creatorID");

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
    }

}
