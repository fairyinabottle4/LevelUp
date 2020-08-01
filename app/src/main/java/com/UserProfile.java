package com;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tryone.R;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
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
    }
}
