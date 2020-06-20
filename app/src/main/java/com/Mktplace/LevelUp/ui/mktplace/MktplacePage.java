package com.Mktplace.LevelUp.ui.mktplace;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tryone.R;

public class MktplacePage extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;

    private String url;
    private String title;
    private String location;
    private String description;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marketplace_page);
        mImageView = findViewById(R.id.listing_image_full);
        mTextView1 = findViewById(R.id.listing_title_full);
        mTextView2 = findViewById(R.id.meetup_location_full);
        mTextView3 = findViewById(R.id.listing_description_full);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        title = extras.getString("title");
        location = extras.getString("location");
        description = extras.getString("description");
        url = extras.getString("imageurl");

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
