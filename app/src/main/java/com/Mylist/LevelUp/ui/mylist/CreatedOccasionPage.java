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
import com.Jios.LevelUp.ui.jios.JiosItem;
import com.MainActivity;
import com.Mktplace.LevelUp.ui.mktplace.MktplaceItem;
import com.UserItem;
import com.UserProfile;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CreatedOccasionPage extends AppCompatActivity {
    private ImageView mImageView;
    private Button mEditButton;
    private Button mPeopleButton;
    private Button mPeopleLikedButton;
    private TextView mNumLikes;
    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;
    private TextView mTextView4;
    private TextView mTextView5;
    private TextView mTextView6;

    private String occaID;
    private String creatorName1;
    private Date updatedDate;

    String uid;
    String creatorName;
    int creatorResidence;
    String profilePictureUri;
    String email;
    long phone;
    String telegram;


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
        mPeopleButton = findViewById(R.id.creator_page_people);
        mPeopleLikedButton = findViewById(R.id.image_people_liked);
        mNumLikes = findViewById(R.id.numlikes_textview);
        mTextView1 = findViewById(R.id.event_page_title);
        mTextView2 = findViewById(R.id.event_page_date);
        mTextView3 = findViewById(R.id.event_page_time);
        mTextView4 = findViewById(R.id.event_page_location);
        mTextView5 = findViewById(R.id.event_page_description);
        mTextView6 = findViewById(R.id.event_page_creator);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        creatorName = intent.getStringExtra("creatorName");
        creatorResidence = intent.getIntExtra("residence", 0);
        telegram = intent.getStringExtra("telegram");
        email = intent.getStringExtra("email");
        phone = intent.getLongExtra("phone", 0);

        final String title = intent.getStringExtra("title");
        final String date = intent.getStringExtra("date");
        final String dateToShow = intent.getStringExtra("dateToShow");
        final String time = intent.getStringExtra("time");
        final String location = intent.getStringExtra("location");
        final String description = intent.getStringExtra("description");
        final String occID = intent.getStringExtra("occID");
        final int numLikes = intent.getIntExtra("numLikes", 0);

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

        occaID = occID;
        creatorName1 = creatorName;
        // Toast.makeText(this, occID, Toast.LENGTH_SHORT).show();
        final String creatorID = intent.getStringExtra("uid");
        position = intent.getIntExtra("position", 0);
        final String userID = MainActivity.currUser.getId();
        final boolean isJio = intent.getBooleanExtra("isJio", true);

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

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreatedOccasionPage.this, EditOccasionInfoActivity.class);

                intent.putExtra("title", mTextView1.getText().toString().trim());
                intent.putExtra("location", mTextView4.getText().toString().trim());
                intent.putExtra("description", mTextView5.getText().toString().trim());

                if (updatedDate == null) {
                    intent.putExtra("date", dateToShow);
                } else {
                    intent.putExtra("date", DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK).format(updatedDate));
                }
                intent.putExtra("time", mTextView3.getText().toString().trim());
                intent.putExtra("occID", occID);
                intent.putExtra("creatorID", creatorID);

                startActivity(intent);
            }
        });

        mPeopleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // new Activity
                Intent intent = new Intent(CreatedOccasionPage.this, CreatorViewNames.class);
                intent.putExtra("occID", occID);
                intent.putExtra("isJio", isJio);
                startActivity(intent);
            }
        });

        mPeopleLikedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreatedOccasionPage.this, CreatorViewLikeNames.class);
                intent.putExtra("occID", occID);
                intent.putExtra("isJio", isJio);
                startActivity(intent);
            }
        });


    }

    private void refreshEvent() {
        // get the occID, go DB repull then set all the views to it
        // need title, loca, desc, date, time, - creator name and occid is the same
        DatabaseReference mDatabaseEventsRef = mFirebaseDatabase.getReference("Events");
        mDatabaseEventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    EventsItem selected = snapshot.getValue(EventsItem.class);
                    if (occaID.equals(selected.getEventID())) {
                        mTextView1.setText(selected.getTitle());
                        mTextView2.setText(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK).format(selected.getDateInfo()));
                        updatedDate = selected.getDateInfo();
                        mTextView3.setText(selected.getTimeInfo());
                        mTextView4.setText(selected.getLocationInfo());
                        mTextView5.setText(selected.getDescription());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void refreshJio() {
        // get the occID, go DB repull then set all the views to it
        // need title, loca, desc, date, time, - creator name and occid is the same
        DatabaseReference mDatabaseJiosRef = mFirebaseDatabase.getReference("Jios");
        mDatabaseJiosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    JiosItem selected = snapshot.getValue(JiosItem.class);
                    if (occaID.equals(selected.getJioID())) {
                        mTextView1.setText(selected.getTitle());
                        mTextView2.setText(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK).format(selected.getDateInfo()));
                        updatedDate = selected.getDateInfo();
                        mTextView3.setText(selected.getTimeInfo());
                        mTextView4.setText(selected.getLocationInfo());
                        mTextView5.setText(selected.getDescription());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setProfilePictureUri(String profilePictureUri) { this.profilePictureUri = profilePictureUri;}

    public void setEmail(String email) {this.email = email;}

    public void setTelegram(String telegram) { this.telegram = telegram;}

    public void setPhone(long phone) {this.phone = phone;}

    @Override
    public void onBackPressed() {
//        if (changes) {
//            EventsFragment.setRefresh(true);
//        }
        // refresh the Jios Created Fragment and Events Created Fragment
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        refreshEvent();
        refreshJio();
        super.onResume();
    }
}
