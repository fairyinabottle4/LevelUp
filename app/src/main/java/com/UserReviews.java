package com;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.example.tryone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter viewAdapter;
    private ArrayList<ReviewItem> reviewItemList;
    private ValueEventListener valueEventListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button submitButton;
    private EditText writeReview;

    private String userID = MainActivity.getCurrentUser().getId();
    private String name;
    private String creatorID;

    private FirebaseDatabase database;
    private DatabaseReference databaseRef;

    private DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
    private Date currentDate = new Date();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        name = MainActivity.getCurrentUser().getName();
        creatorID = getIntent().getStringExtra("creatorid");
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference().child("Users").child(creatorID);
        setContentView(R.layout.user_reviews);
        createReviewList();
        buildRecyclerView();
        loadDataReviews();

        swipeRefreshLayout = findViewById(R.id.swiperefreshlayoutreviews);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reviewItemList.clear();
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
                databaseRef.child("Reviews").child(userID).setValue(reviewItem);
                Toast.makeText(getApplicationContext(), "Thank you for your review!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void createReviewList() {
        reviewItemList = new ArrayList<>();
    }

    /**
     * Build the recycler view that will display the list of items
     */
    public void buildRecyclerView() {
        recyclerView = findViewById(R.id.recyclerviewReviews);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        viewAdapter = new ReviewAdapter(reviewItemList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(viewAdapter);
    }

    /**
     * Load the review information from the storage
     */
    public void loadDataReviews() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewItemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ReviewItem selected = snapshot.getValue(ReviewItem.class);
                    reviewItemList.add(selected);
                }
                ReviewAdapter reviewAdapter = new ReviewAdapter(reviewItemList);
                recyclerView.setAdapter(reviewAdapter);
                viewAdapter = reviewAdapter;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseRef.child("Reviews").addListenerForSingleValueEvent(valueEventListener);
        viewAdapter.notifyDataSetChanged();
    }
}
