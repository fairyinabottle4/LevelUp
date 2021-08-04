package com.levelup.ui.mylist;

import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.levelup.R;
import com.levelup.ui.mktplace.MktplaceItem;
import com.levelup.user.UserItem;
import com.levelup.user.UserProfile;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MktplaceCreatedAdapter extends RecyclerView.Adapter<MktplaceCreatedAdapter.MktplaceCreatedViewHolder> {
    //ArrayList is passed in from MktplaceItem.java
    private Context mContext;

    private ArrayList<MktplaceItem> mMktplaceList;
    private ArrayList<MktplaceItem> mMktplaceListFull;
    private MktplaceCreatedAdapter.OnItemClickListener mAdapterListener;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserRef;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    //the ViewHolder holds the content of the card
    public static class MktplaceCreatedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public String creatorUid;
        public String creatorName;
        public String mktPlaceID;
        public int creatorResidence;
        public String profilePictureUri;
        public String email;
        public long phone;
        public String telegram;


        public ImageView mImageView;
        public TextView mTitle;
        public TextView mCreatorName;
        public Button mEditButton;
        public Button mPeopleLikedButton;
        private MktplaceCreatedAdapter.OnItemClickListener mListener;

        public MktplaceCreatedViewHolder(final Context context, View itemView,
                                         final MktplaceCreatedAdapter.OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTitle = itemView.findViewById(R.id.textView);
            mCreatorName = itemView.findViewById(R.id.creatorTextView);
            mCreatorName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserProfile.class);
                    intent.putExtra("creatorfid", creatorUid);
                    intent.putExtra("name", creatorName);
                    intent.putExtra("residence", creatorResidence);
                    intent.putExtra("dpUri", profilePictureUri);
                    intent.putExtra("telegram", telegram);
                    intent.putExtra("email", email);
                    intent.putExtra("phone", phone);
                    context.startActivity(intent);
                }
            });
            mEditButton = itemView.findViewById(R.id.mktplace_edit_btn);
            mPeopleLikedButton = itemView.findViewById(R.id.image_people_liked);
            mListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(getAdapterPosition());
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

    }

    //Constructor for MktplaceAdapter class. This ArrayList contains the
    //complete list of items that we want to add to the View.
    public MktplaceCreatedAdapter(Context context, ArrayList<MktplaceItem> mktplaceList,
                                  MktplaceCreatedAdapter.OnItemClickListener listener) {
        this.mContext = context;
        mMktplaceList = mktplaceList;
        mMktplaceListFull = new ArrayList<>(mktplaceList);
        mAdapterListener = listener;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserRef = mFirebaseDatabase.getReference("Users");
    }

    //inflate the items in a MktplaceViewHolder
    @NonNull
    @Override
    public MktplaceCreatedAdapter.MktplaceCreatedViewHolder onCreateViewHolder(
        @NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.marketplace_item_edit,
            parent, false);
        MktplaceCreatedAdapter.MktplaceCreatedViewHolder evh = new MktplaceCreatedAdapter
            .MktplaceCreatedViewHolder(mContext, v, mAdapterListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MktplaceCreatedAdapter.MktplaceCreatedViewHolder holder, int position) {
        final MktplaceItem uploadCurrent = mMktplaceList.get(position);
        final String imageUrl = uploadCurrent.getImageUrl();
        final String mktplaceID = uploadCurrent.getMktPlaceID();
        final String creatorUid = uploadCurrent.getCreatorID();
        holder.setCreatorUid(creatorUid);
        final String title = uploadCurrent.getName();
        final String location = uploadCurrent.getLocation();
        final String description = uploadCurrent.getDescription();

        holder.mTitle.setText(uploadCurrent.getName());

        holder.mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditMarketPlaceActivity.class);
                intent.putExtra("imageurl", imageUrl);
                intent.putExtra("mktplaceID", mktplaceID);
                intent.putExtra("title", title);
                intent.putExtra("location", location);
                intent.putExtra("description", description);
                intent.putExtra("creatorUid", creatorUid);
                mContext.startActivity(intent);
            }
        });

        holder.mPeopleLikedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CreatorViewLikeNamesMktplace.class);
                String occID = uploadCurrent.getMktPlaceID();
                intent.putExtra("occID", occID);
                mContext.startActivity(intent);
            }
        });

        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserItem selected = snapshot.getValue(UserItem.class);
                    String id = selected.getId();

                    if (creatorUid.equals(id)) {
                        String name = selected.getName();
                        holder.mCreatorName.setText(name);
                        holder.setCreatorName(name);
                        int res = selected.getResidential();
                        String telegram = selected.getTelegram();
                        String email = selected.getEmail();
                        String dpUri = selected.getProfilePictureUri();
                        long phone = selected.getPhone();
                        holder.setCreatorName(name);
                        holder.setCreatorResidence(res);
                        holder.setTelegram(telegram);
                        holder.setEmail(email);
                        holder.setProfilePictureUri(dpUri);
                        holder.setPhone(phone);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Glide.with(holder.mImageView.getContext()).load(imageUrl).into(holder.mImageView);
    }


    @Override
    public int getItemCount() {
        return mMktplaceList.size();
    }

    public void resetAdapter() {
        this.mMktplaceList = mMktplaceListFull;
    }
}
