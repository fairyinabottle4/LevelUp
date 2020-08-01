package com;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tryone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class UserProfile extends AppCompatActivity {
    private TextView displayName;
    private TextView residence;
    private ImageView displayPicture;
    private TextView telegramTitle;
    private TextView emailTitle;
    private TextView phoneTitle;
    private TextView telegramHandle;
    private TextView emailAddress;
    private TextView phoneNumber;
    private RatingBar ratingBar;

    private StorageReference mProfileStorageRef;


    private static final String[] residentials = {"I don't stay on campus",
            "Cinnamon", "Tembusu", "CAPT", "RC4", "RVRC",
            "Eusoff", "Kent Ridge", "King Edward VII", "Raffles",
            "Sheares", "Temasek", "PGP House", "PGP Residences", "UTown Residence",
            "Select Residence"};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        mProfileStorageRef = FirebaseStorage.getInstance()
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
        ratingBar = findViewById(R.id.UserRating);

        Intent intent = getIntent();
        String uid = intent.getStringExtra("uid");
        String name = intent.getStringExtra("name");
        int residenceIndex = intent.getIntExtra("residence", 0);
        String profilePictureUri = intent.getStringExtra("dpUri");
        String telegram = intent.getStringExtra("telegram");
        String email = intent.getStringExtra("email");
        Long phone = intent.getLongExtra("phone", 0);

        displayName.setText(name);
        residence.setText(residentials[residenceIndex]);
        telegramHandle.setText(telegram);
        emailAddress.setText(email);
        phoneNumber.setText(phone.toString());
        StorageReference mProfileStorageRefIndiv = mProfileStorageRef.child(uid);
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

    }
}
