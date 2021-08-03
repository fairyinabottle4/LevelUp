package com.levelup.user;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.levelup.R;
import com.levelup.activity.MainActivity;
import com.levelup.review.ReviewAdapter;
import com.levelup.review.ReviewItem;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class UserReviews extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<ReviewItem> ReviewItemList;
    private ValueEventListener mValueEventListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button submitButton;
    private EditText writeReview;

    private String userID = MainActivity.getCurrentUser().getId();
    private String name;
    private String creatorID;

    FirebaseDatabase mDatabase;
    DatabaseReference mDatabaseReference;

    DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
    Date currentDate = new Date();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        name = MainActivity.getCurrentUser().name;
        creatorID = getIntent().getStringExtra("creatorid");
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("Users").child(creatorID);
        setContentView(R.layout.user_reviews);
        createReviewList();
        buildRecyclerView();
        loadDataReviews();

        swipeRefreshLayout = findViewById(R.id.swiperefreshlayoutreviews);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ReviewItemList.clear();
                loadDataReviews();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        writeReview = findViewById(R.id.review_box);
        submitButton = findViewById(R.id.submit_review_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSubmit = writeReview.getText().toString().trim();
                ReviewItem reviewItem = new ReviewItem(userID, name, df.format(currentDate), toSubmit);
                mDatabaseReference.child("Reviews").child(userID).setValue(reviewItem);
                Toast.makeText(getApplicationContext(), "Thank you for your review!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void createReviewList() {
        ReviewItemList = new ArrayList<>();
    }

    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerviewReviews);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ReviewAdapter(ReviewItemList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void loadDataReviews() {
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ReviewItemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ReviewItem selected = snapshot.getValue(ReviewItem.class);
                    ReviewItemList.add(selected);
                }
                ReviewAdapter reviewAdapter = new ReviewAdapter(ReviewItemList);
                mRecyclerView.setAdapter(reviewAdapter);
                mAdapter = reviewAdapter;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.child("Reviews").addListenerForSingleValueEvent(mValueEventListener);
        mAdapter.notifyDataSetChanged();
    }
}
