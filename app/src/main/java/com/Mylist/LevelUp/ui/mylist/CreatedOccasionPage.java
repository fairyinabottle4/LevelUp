package com.Mylist.LevelUp.ui.mylist;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import com.Events.LevelUp.ui.events.EventsItem;
import com.Jios.LevelUp.ui.jios.JiosItem;
import com.MainActivity;
import com.UserProfile;
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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CreatedOccasionPage extends AppCompatActivity {
    private ImageView imageView;
    private Button editButton;
    private Button peopleButton;
    private Button peopleLikedButton;
    private TextView numLikesView;
    private TextView titleView;
    private TextView dateView;
    private TextView timeView;
    private TextView locationView;
    private TextView descView;
    private TextView creatorView;

    private String occaID;
    private String creatorName1;
    private Date updatedDate;

    private String uid;
    private String creatorName;
    private int creatorResidence;
    private String profilePictureUri;
    private String email;
    private long phone;
    private String telegram;


    private int position;
    private StorageReference profileStorageRef;
    private FirebaseDatabase firebaseDatabase;

    private boolean changes = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.created_occasion_page);
        profileStorageRef = FirebaseStorage.getInstance()
                .getReference("profile picture uploads");
        firebaseDatabase = FirebaseDatabase.getInstance();

        imageView = findViewById(R.id.event_page_image);
        editButton = findViewById(R.id.creator_page_edit);
        peopleButton = findViewById(R.id.creator_page_people);
        peopleLikedButton = findViewById(R.id.image_people_liked);
        numLikesView = findViewById(R.id.numlikes_textview);
        titleView = findViewById(R.id.event_page_title);
        dateView = findViewById(R.id.event_page_date);
        timeView = findViewById(R.id.event_page_time);
        locationView = findViewById(R.id.event_page_location);
        descView = findViewById(R.id.event_page_description);
        creatorView = findViewById(R.id.event_page_creator);

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

        occaID = occID;
        creatorName1 = creatorName;
        // Toast.makeText(this, occID, Toast.LENGTH_SHORT).show();
        final String creatorID = intent.getStringExtra("uid");
        position = intent.getIntExtra("position", 0);
        final String userID = MainActivity.getCurrUser().getId();
        final boolean isJio = intent.getBooleanExtra("isJio", true);

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
        descView.setText(description);
        creatorView.setText(creatorName);

        numLikesView.setText(Integer.toString(numLikes));

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreatedOccasionPage.this, EditOccasionInfoActivity.class);

                intent.putExtra("title", titleView.getText().toString().trim());
                intent.putExtra("location", locationView.getText().toString().trim());
                intent.putExtra("description", descView.getText().toString().trim());

                if (updatedDate == null) {
                    intent.putExtra("date", dateToShow);
                } else {
                    intent.putExtra("date", DateFormat
                        .getDateInstance(DateFormat.MEDIUM, Locale.UK).format(updatedDate));
                }
                intent.putExtra("time", timeView.getText().toString().trim());
                intent.putExtra("occID", occID);
                intent.putExtra("creatorID", creatorID);

                startActivity(intent);
            }
        });

        peopleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // new Activity
                Intent intent = new Intent(CreatedOccasionPage.this, CreatorViewNames.class);
                intent.putExtra("occID", occID);
                intent.putExtra("isJio", isJio);
                startActivity(intent);
            }
        });

        peopleLikedButton.setOnClickListener(new View.OnClickListener() {
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
        DatabaseReference databaseEventsRef = firebaseDatabase.getReference("Events");
        databaseEventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    EventsItem selected = snapshot.getValue(EventsItem.class);
                    if (occaID.equals(selected.getEventID())) {
                        titleView.setText(selected.getTitle());
                        dateView.setText(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK)
                            .format(selected.getDateInfo()));
                        updatedDate = selected.getDateInfo();
                        timeView.setText(selected.getTimeInfo());
                        locationView.setText(selected.getLocationInfo());
                        descView.setText(selected.getDescription());
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
        DatabaseReference databaseJiosRef = firebaseDatabase.getReference("Jios");
        databaseJiosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    JiosItem selected = snapshot.getValue(JiosItem.class);
                    if (occaID.equals(selected.getJioID())) {
                        titleView.setText(selected.getTitle());
                        dateView.setText(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK)
                            .format(selected.getDateInfo()));
                        updatedDate = selected.getDateInfo();
                        timeView.setText(selected.getTimeInfo());
                        locationView.setText(selected.getLocationInfo());
                        descView.setText(selected.getDescription());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
