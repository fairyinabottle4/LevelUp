package com.levelup.ui.dashboard;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.levelup.R;
import com.levelup.activity.MainActivity;
import com.levelup.occasion.Occasion;
import com.levelup.ui.events.EventPage;
import com.levelup.user.UserItem;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder> {

    private ArrayList<Occasion> occasionList;
    private StorageReference mProfileStorageRef;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserRef;
    private DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
    private FragmentActivity mContext;

    public static class DashboardViewHolder extends RecyclerView.ViewHolder {
        public String creatorUid;
        public String creatorName;
        public int creatorResidence;
        public String profilePictureUri;
        public String email;
        public long phone;
        public String telegram;
        public String description;

        public String eventID;
        public boolean isChecked;
        public boolean isLiked;
        public int numLikes;

        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView3;
        public TextView mTextView4;
        public TextView mTextView5;

        public DashboardViewHolder (final Context context, View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.category_imageView);
            // mAddButton = itemView.findViewById(R.id.image_add);
            // mLikeButton = itemView.findViewById(R.id.image_like);
            mTextView1 = itemView.findViewById(R.id.title);
            mTextView3 = itemView.findViewById(R.id.date);
            mTextView4 = itemView.findViewById(R.id.location);
            mTextView5 = itemView.findViewById(R.id.time);
            // mTextView6 = itemView.findViewById(R.id.eventCreator);
            // mNumLikes = itemView.findViewById(R.id.numlikes_textview);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, EventPage.class);
                    intent.putExtra("uid", creatorUid);
                    intent.putExtra("creatorName", creatorName);
                    intent.putExtra("title", mTextView1.getText().toString());
                    intent.putExtra("description", description);
                    intent.putExtra("date", mTextView3.getText().toString());
                    intent.putExtra("location", mTextView4.getText().toString());
                    intent.putExtra("time", mTextView5.getText().toString());
                    intent.putExtra("position", getAdapterPosition());
                    intent.putExtra("stateChecked", isChecked);
                    intent.putExtra("stateLiked", isLiked);
                    intent.putExtra("numLikes", numLikes);
                    intent.putExtra("eventID", eventID);
                    intent.putExtra("residence", creatorResidence);
                    intent.putExtra("dpUri", profilePictureUri);
                    intent.putExtra("telegram", telegram);
                    intent.putExtra("email", email);
                    intent.putExtra("phone", phone);
                    context.startActivity(intent);
                }
            });

        }

        public void setCreatorUid(String newUid) {
            this.creatorUid = newUid;
        }

        public void setCreatorName(String creatorName) {
            this.creatorName = creatorName;
        }

        public void setCreatorResidence(int creatorResidence) {
            this.creatorResidence = creatorResidence;
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

        public void setChecked(boolean toSet) {
            this.isChecked = toSet;
        }

        public void setEventID(String eventID) {
            this.eventID = eventID;
        }

        public void setLiked(boolean liked) {
            isLiked = liked;
        }

        public void setNumLikes(int numLikes) {
            this.numLikes = numLikes;
        }

    }

    public DashboardAdapter(FragmentActivity context, ArrayList<Occasion> occasionList) {
        this.occasionList = occasionList;
        mProfileStorageRef = FirebaseStorage.getInstance()
                .getReference("profile picture uploads");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserRef = mFirebaseDatabase.getReference("Users");
        this.mContext = context;
    }


    @NonNull
    @Override
    public DashboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.occ_item_dashboard, parent, false);
        DashboardAdapter.DashboardViewHolder dvh = new DashboardAdapter.DashboardViewHolder(mContext, v);
        return dvh;
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardViewHolder holder, final int position) {
        final Occasion currentItem = occasionList.get(position);
        final DashboardAdapter.DashboardViewHolder holder1 = holder;
        final String creatorUid = currentItem.getCreatorID();
        holder1.setCreatorUid(creatorUid);

        int category = currentItem.getCategory();

        if (category == 0) { // arts
            holder1.mImageView.setImageResource(R.drawable.arts);
        }
        if (category == 1) { // sports
            holder1.mImageView.setImageResource(R.drawable.sports);
        }
        if (category == 2) { // talks
            holder1.mImageView.setImageResource(R.drawable.talks);
        }
        if (category == 3) { // volunteering
            holder1.mImageView.setImageResource(R.drawable.volunteering);
        }
        if (category == 4) { // food
            holder1.mImageView.setImageResource(R.drawable.food);
        }
        if (category == 5) { // food
            holder1.mImageView.setImageResource(R.drawable.others);
        }


        //        StorageReference mProfileStorageRefIndiv = mProfileStorageRef.child(creatorUid);
        //        mProfileStorageRefIndiv.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
        //            @Override
        //            public void onSuccess(Uri uri) {
        //                Picasso.get().load(uri).into(holder1.mImageView);
        //            }
        //        }).addOnFailureListener(new OnFailureListener() {
        //            @Override
        //            public void onFailure(@NonNull Exception e) {
        //                holder1.mImageView.setImageResource(R.drawable.fake_user_dp);
        //            }
        //        });

        holder1.mTextView1.setText(currentItem.getTitle());
        holder1.description = currentItem.getDescription();
        holder1.mTextView4.setText(currentItem.getLocationInfo());

        String date = df.format(currentItem.getDateInfo());
        holder1.mTextView3.setText(date);

        String time = currentItem.getTimeInfo();
        holder1.mTextView5.setText(time);

        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserItem selected = snapshot.getValue(UserItem.class);
                    String id = selected.getId();

                    if (creatorUid.equals(id)) {
                        String name = selected.getName();
                        int res = selected.getResidential();
                        String telegram = selected.getTelegram();
                        String email = selected.getEmail();
                        String dpUri = selected.getProfilePictureUri();
                        long phone = selected.getPhone();
                        holder1.setCreatorName(name);
                        holder1.setCreatorResidence(res);
                        holder1.setTelegram(telegram);
                        holder1.setEmail(email);
                        holder1.setProfilePictureUri(dpUri);
                        holder1.setPhone(phone);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String eventID = currentItem.getOccasionID();
        holder1.setEventID(eventID);

        // set stateChecked and stateLiked to pass into the intent
        if (currentItem.isJio()) {
            if (MainActivity.mJioIDs.contains(currentItem.getOccasionID())) {
                holder1.setChecked(true);
            }
            if (MainActivity.mLikeJioIDs.contains(currentItem.getOccasionID())) {
                holder1.setLiked(true);
            }
        } else {
            if (MainActivity.mEventIDs.contains(currentItem.getOccasionID())) {
                holder1.setChecked(true);
            }
            if (MainActivity.mLikeEventIDs.contains(currentItem.getOccasionID())) {
                holder1.setLiked(true);
            }
        }

        holder1.setNumLikes(currentItem.getNumLikes());

    }

    @Override
    public int getItemCount() {
        return occasionList.size();
    }


}

