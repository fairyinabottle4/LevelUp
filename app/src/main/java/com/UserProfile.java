package com;

import com.example.tryone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class UserProfile extends AppCompatActivity {

    private static final String[] residentials = {"I don't stay on campus",
        "Cinnamon", "Tembusu", "CAPT", "RC4", "RVRC",
        "Eusoff", "Kent Ridge", "King Edward VII", "Raffles",
        "Sheares", "Temasek", "PGP House", "PGP Residences", "UTown Residence",
        "Select Residence"};

    private TextView displayName;
    private TextView residence;
    private ImageView displayPicture;
    private TextView telegramTitle;
    private TextView emailTitle;
    private TextView phoneTitle;
    private TextView telegramHandle;
    private TextView emailAddress;
    private TextView phoneNumber;
    private TextView idBox;
    private TextView actualRating;
    private ImageView ratingStar;
    private TextView rateThisUser;
    private RatingBar ratingBar;
    private Button reviewButton;

    private StorageReference profileStorageRef;

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private String currUserId = MainActivity.getCurrentUser().getId();
    private String creatorId;

    private float sumOfRatings = 0;
    private float numOfRatings = 0;
    private float averageRatingGlobal;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        profileStorageRef = FirebaseStorage.getInstance()
                .getReference("profile picture uploads");
        displayName = findViewById(R.id.TextViewDisplayName);
        residence = findViewById(R.id.TextViewResidenceName);
        displayPicture = findViewById(R.id.ViewProfilePicture);
        telegramTitle = findViewById(R.id.DisplayTelegramTitle);
        telegramHandle = findViewById(R.id.DisplayTelegramHandle);
        emailTitle = findViewById(R.id.DisplayEmailTitle);
        emailAddress = findViewById(R.id.DisplayEmailAddress);
        phoneTitle = findViewById(R.id.DisplayPhoneTitle);
        phoneNumber = findViewById(R.id.DisplayPhoneNumber);
        idBox = findViewById(R.id.IDBox);
        actualRating = findViewById(R.id.actual_score);
        ratingStar = findViewById(R.id.rating_star);
        rateThisUser = findViewById(R.id.rate_this_user);
        ratingBar = findViewById(R.id.UserRating);
        reviewButton = findViewById(R.id.review_button);

        Intent intent = getIntent();
        String creatorIdCopy = intent.getStringExtra("creatorfid");
        if (creatorIdCopy == null) {
            creatorId = currUserId;
        } else {
            creatorId = creatorIdCopy;
        }
        final String name = intent.getStringExtra("name");
        final int residenceIndex = intent.getIntExtra("residence", 0);
        final String telegram = intent.getStringExtra("telegram");
        final String email = intent.getStringExtra("email");
        final Long phone = intent.getLongExtra("phone", 0);

        displayName.setText(name);
        residence.setText(residentials[residenceIndex]);
        telegramHandle.setText(telegram);
        emailAddress.setText(email);
        phoneNumber.setText(phone.toString());
        StorageReference mProfileStorageRefIndiv = profileStorageRef.child(creatorId);
        mProfileStorageRefIndiv.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(displayPicture);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                displayPicture.setImageResource(R.drawable.fake_user_dp);
            }
        });


        //pulling the rating from the database
        db.getReference().child("Users").child(creatorId).child("Ratings")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot child :dataSnapshot.getChildren()) {
                            sumOfRatings += Float.parseFloat(child.getValue().toString());
                            numOfRatings++;
                            if (child.getKey().equals(currUserId)) {
                                ratingBar.setRating(Float.parseFloat(child.getValue().toString()));
                            }
                        }
                    }
                    float averageRating = sumOfRatings / numOfRatings;
                    averageRatingGlobal = averageRating;
                    if (numOfRatings == 0) {
                        actualRating.setText("No ratings yet!");
                    } else {
                        actualRating.setText(String.format("%.1f", averageRating));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        //when the rating is changed.
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                db.getReference().child("Users").child(currUserId).child("Ratings").child(creatorId).setValue(rating);
                db.getReference().child("Users").child(creatorId).child("Ratings").child(currUserId).setValue(rating);
            }
        });

        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserReviews.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("name", name);
                intent.putExtra("creatorid", creatorId);
                getApplicationContext().startActivity(intent);
            }
        });

    }
}
