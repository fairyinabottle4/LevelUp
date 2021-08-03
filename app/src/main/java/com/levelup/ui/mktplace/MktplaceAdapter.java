package com.levelup.ui.mktplace;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.levelup.R;
import com.levelup.activity.MainActivity;
import com.levelup.occasion.LikeOccasionItem;
import com.levelup.user.UserItem;
import com.levelup.user.UserProfile;

import java.util.ArrayList;
import java.util.List;

public class MktplaceAdapter extends RecyclerView.Adapter<MktplaceAdapter.MktplaceViewHolder> implements Filterable {
    //ArrayList is passed in from MktplaceItem.java
    private FragmentActivity mContext;

    private ArrayList<MktplaceItem> mMktplaceList;
    private ArrayList<MktplaceItem> mMktplaceListFull;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserRef;

    //the ViewHolder holds the content of the card
    public static class MktplaceViewHolder extends RecyclerView.ViewHolder {
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
        public String description;
        public String location;
        public String imageUrl;

        public ToggleButton mLikeButton;
        public TextView mNumLikes;
        public boolean isLiked;
        public int numLikes;

        public MktplaceViewHolder(final Context context, View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mNumLikes = itemView.findViewById(R.id.numlikes_textview);
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MktplacePage.class);
                    intent.putExtra("description", description);
                    intent.putExtra("title", mTitle.getText().toString());
                    intent.putExtra("location", location);
                    intent.putExtra("imageurl", imageUrl);
                    intent.putExtra("creatorID", creatorUid);
                    intent.putExtra("mktplaceID", mktPlaceID);
                    intent.putExtra("creatorName", creatorName);
                    intent.putExtra("stateLiked", isLiked);
                    intent.putExtra("creatorfid", creatorUid);
                    intent.putExtra("name", creatorName);
                    intent.putExtra("residence", creatorResidence);
                    intent.putExtra("dpUri", profilePictureUri);
                    intent.putExtra("telegram", telegram);
                    intent.putExtra("email", email);
                    intent.putExtra("phone", phone);
                    intent.putExtra("numLikes", numLikes);

                    context.startActivity(intent);

                }
            });
            mLikeButton = itemView.findViewById(R.id.image_like);
        }

        public void setCreatorUid(String newUID) {
            this.creatorUid = newUID;
        }

        public void setCreatorName(String creatorName) {
            this.creatorName = creatorName;
        }

        public void setCreatorResidence(int creatorResidence) {this.creatorResidence = creatorResidence;}

        public void setProfilePictureUri(String profilePictureUri) { this.profilePictureUri = profilePictureUri;}

        public void setEmail(String email) {this.email = email;}

        public void setTelegram(String telegram) { this.telegram = telegram;}

        public void setPhone(long phone) {this.phone = phone;}

        public void setLiked(boolean liked) {
            isLiked = liked;
        }

        public void setNumLikes(int numLikes) {
            this.numLikes = numLikes;
        }
    }

    //Constructor for MktplaceAdapter class. This ArrayList contains the
    //complete list of items that we want to add to the View.
    public MktplaceAdapter(FragmentActivity context, ArrayList<MktplaceItem> MktplaceList) {
        this.mContext = context;
        mMktplaceList = MktplaceList;
        mMktplaceListFull = new ArrayList<>(MktplaceList);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserRef = mFirebaseDatabase.getReference("Users");
    }

    //inflate the items in a MktplaceViewHolder
    @NonNull
    @Override
    public MktplaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.marketplace_item, parent, false);
        MktplaceViewHolder evh = new MktplaceViewHolder(mContext,v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MktplaceViewHolder holder, final int position) {
        MktplaceItem uploadCurrent = mMktplaceList.get(position);
        String imageUrl = uploadCurrent.getImageUrl();
        holder.mTitle.setText(uploadCurrent.getName());
        holder.setLiked(MainActivity.mLikeMktplaceIDs.contains(uploadCurrent.getMktPlaceID()));
        holder.description = uploadCurrent.getDescription();
        holder.location = uploadCurrent.getLocation();
        holder.imageUrl = uploadCurrent.getImageUrl();
        holder.numLikes = uploadCurrent.getNumLikes();
        holder.mktPlaceID = uploadCurrent.getMktPlaceID();
        final String creatorUID = uploadCurrent.getCreatorID();
        holder.setCreatorUid(creatorUID);
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserItem selected = snapshot.getValue(UserItem.class);
                    String id = selected.getId();

                    if (creatorUID.equals(id)) {
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

        holder.mNumLikes.setText(Integer.toString(uploadCurrent.getNumLikes()));
        holder.setNumLikes(uploadCurrent.getNumLikes());
        String mktplaceID = uploadCurrent.getMktPlaceID();
        if (MainActivity.mLikeMktplaceIDs.contains(mktplaceID)) {
            holder.mLikeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
            holder.setLiked(true);
            holder.mLikeButton.setChecked(true);
        } else {
            holder.mLikeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
            holder.setLiked(false);
            holder.mLikeButton.setChecked(false);
        }

        holder.mLikeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder.mLikeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
                    holder.setLiked(true);

                    // send to LikeDatabase
                    MktplaceItem mi = mMktplaceList.get(position);
                    UserItem user = MainActivity.currUser;
                    final String mktplaceID = mi.getMktPlaceID();
                    final String userID = user.getId();
                    DatabaseReference mLikeMktplaceRef = mFirebaseDatabase.getReference("LikeMktplace");
                    LikeOccasionItem likeOccasionItem = new LikeOccasionItem(mktplaceID, userID);
                    mLikeMktplaceRef.push().setValue(likeOccasionItem);

                    // +1 to the Likes on the MktplaceItem
                    int currLikes = mi.getNumLikes();
                    DatabaseReference mMktplaceRef = mFirebaseDatabase.getReference("mktplace uploads");
                    mMktplaceRef.child(mktplaceID).child("numLikes").setValue(currLikes + 1);
                    mi.setNumLikes(currLikes + 1);
                    holder.setNumLikes(currLikes + 1);

                    // for display only
                    holder.mNumLikes.setText(Integer.toString(currLikes + 1));

                } else {
                    holder.mLikeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                    holder.setLiked(false);

                    // Delete the entry from LikeDatabase
                    MktplaceItem mi = mMktplaceList.get(position);
                    UserItem user = MainActivity.currUser;
                    final String mktplaceID = mi.getMktPlaceID();
                    final String userID = user.getId();
                    final DatabaseReference mLikeMktplaceRef = mFirebaseDatabase.getReference("LikeMktplace");
                    mLikeMktplaceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                                if (mktplaceID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
                                    String key = snapshot.getKey();
                                    mLikeMktplaceRef.child(key).removeValue();
                                    Toast.makeText(mContext, "Unliked", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    // -1 to the Likes on the eventItem
                    int currLikes = mi.getNumLikes();
                    DatabaseReference mMktplaceRef = mFirebaseDatabase.getReference("mktplace uploads");
                    mMktplaceRef.child(mktplaceID).child("numLikes").setValue(currLikes - 1);
                    mi.setNumLikes(currLikes - 1);
                    holder.setNumLikes(currLikes -1);

                    // for display only
                    holder.mNumLikes.setText(Integer.toString(currLikes - 1));

                    MainActivity.mLikeMktplaceIDs.remove(mktplaceID);

                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mMktplaceList.size();
    }

    @Override
    public Filter getFilter() { // for the 'implements Filterable'
        return mktplaceFilter;
    }

    private Filter mktplaceFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<MktplaceItem> filteredList = new ArrayList<>(); // initially empty list

            if (constraint == null || constraint.length() == 0) { // search input field empty
                filteredList.addAll(mMktplaceListFull); // to show everything
            } else {
                String userSearchInput = constraint.toString().toLowerCase().trim();

                for (MktplaceItem item : mMktplaceListFull) {
                    // contains can be changed to StartsWith
                    if (item.getName().toLowerCase().contains(userSearchInput)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mMktplaceList.clear();
            mMktplaceList.addAll((List) results.values); // data list contains filtered items
            notifyDataSetChanged(); // tell adapter list has changed
        }
    };

    public void resetAdapter() {
        this.mMktplaceList = mMktplaceListFull;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
