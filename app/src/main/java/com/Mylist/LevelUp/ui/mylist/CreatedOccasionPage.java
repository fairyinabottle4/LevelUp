package com.Mylist.LevelUp.ui.mylist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ActivityOccasionItem;
import com.Events.LevelUp.ui.events.EventsItem;
import com.MainActivity;
import com.example.LevelUp.ui.events.EventsFragment;
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

public class CreatedOccasionPage extends AppCompatActivity {
    private ImageView mImageView;
    private Button mEditButton;
    private ToggleButton mLikeButton;
    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;
    private TextView mTextView4;
    private TextView mTextView5;
    private TextView mTextView6;

    private int position;
    // private Context mContext = this;
    private StorageReference mProfileStorageRef;
    private FirebaseDatabase mFirebaseDatabase;

    private boolean changes = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.created_occasion_page);
        mProfileStorageRef = FirebaseStorage.getInstance()
                .getReference("profile picture uploads");
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mImageView = findViewById(R.id.event_page_image);
        mEditButton = findViewById(R.id.creator_page_edit);
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
        final String title = intent.getStringExtra("title");
        final String date = intent.getStringExtra("date");
        final String dateToShow = intent.getStringExtra("dateToShow");
        final String time = intent.getStringExtra("time");
        final String location = intent.getStringExtra("location");
        final String description = intent.getStringExtra("description");
        final String occID = intent.getStringExtra("occID");
        final String creatorID = intent.getStringExtra("uid");
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

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreatedOccasionPage.this, EditOccasionInfoActivity.class);

                intent.putExtra("title", title);
                intent.putExtra("location", location);
                intent.putExtra("description", description);
                intent.putExtra("date", dateToShow);
                intent.putExtra("time", time);
                intent.putExtra("occID", occID);
                intent.putExtra("creatorID", creatorID);

                startActivity(intent);
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
            EventsFragment.setRefresh(true);
        }
        super.onBackPressed();
    }
}
