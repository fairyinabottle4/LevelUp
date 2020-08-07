package com;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tryone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{

    private ArrayList<ReviewItem> mReviewList;
    private StorageReference mProfileStorageRef;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserRef;

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        public ImageView mProfilePicture;
        public TextView mReviewerName;
        public TextView mReviewDate;
        public TextView mReview;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            mProfilePicture = itemView.findViewById(R.id.imageViewReview);
            mReviewerName = itemView.findViewById(R.id.author);
            mReviewDate = itemView.findViewById(R.id.date);
            mReview = itemView.findViewById(R.id.review_description);
            //h
        }
    }

    public ReviewAdapter(ArrayList<ReviewItem> mReviewList) {
        this.mReviewList = mReviewList;
        mProfileStorageRef = FirebaseStorage.getInstance()
                .getReference("profile picture uploads");
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        ReviewViewHolder rvh = new ReviewViewHolder(v);
        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewItem currentItem = mReviewList.get(position);
        final ReviewViewHolder holder1 = holder;
        String reviewerID = currentItem.getUserID();
        StorageReference mProfileStorageRefIndiv = mProfileStorageRef.child(reviewerID);
        mProfileStorageRefIndiv.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder1.mProfilePicture);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                holder1.mProfilePicture.setImageResource(R.drawable.fake_user_dp);
            }
        });
        holder.mReviewerName.setText(currentItem.getReviewerdisplayName());
        holder.mReviewDate.setText(currentItem.getDate());
        holder.mReview.setText(currentItem.getReview());
    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }
}
