package com.Mktplace.LevelUp.ui.mktplace;

import java.util.ArrayList;
import java.util.List;

import com.LikeOccasionItem;
import com.MainActivity;
import com.UserItem;
import com.UserProfile;
import com.bumptech.glide.Glide;
import com.example.tryone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

public class MktplaceAdapter extends RecyclerView.Adapter<MktplaceAdapter.MktplaceViewHolder> implements Filterable {
    //ArrayList is passed in from MktplaceItem.java
    private FragmentActivity mktplaceContext;

    private ArrayList<MktplaceItem> mktplaceList;
    private ArrayList<MktplaceItem> mktplaceListFull;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;

    //the ViewHolder holds the content of the card
    public static class MktplaceViewHolder extends RecyclerView.ViewHolder {
        private String creatorUid;
        private String creatorName;
        private String mktPlaceID;
        private int creatorResidence;
        private String profilePictureUri;
        private String email;
        private long phone;
        private String telegram;

        private ImageView imageView;
        private TextView titleView;
        private TextView creatorView;
        private String description;
        private String location;
        private String imageUrl;

        private ToggleButton likeButton;
        private TextView numLikesView;
        private boolean isLiked;
        private int numLikes;

        /**
         * Constructor for the view holder that will be used to display information of individual
         * MktplaceItem listings
         *
         * @param context Context of the Fragment where the adapter is placed
         * @param itemView View of the items that will be placed in the adapter
         */
        public MktplaceViewHolder(final Context context, View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            numLikesView = itemView.findViewById(R.id.numlikes_textview);
            titleView = itemView.findViewById(R.id.textView);
            creatorView = itemView.findViewById(R.id.creatorTextView);
            creatorView.setOnClickListener(new View.OnClickListener() {
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
                    intent.putExtra("titleView", titleView.getText().toString());
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
            likeButton = itemView.findViewById(R.id.image_like);
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

        public void setLiked(boolean liked) {
            isLiked = liked;
        }

        public void setNumLikes(int numLikes) {
            this.numLikes = numLikes;
        }
    }
    private Filter mktplaceFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<MktplaceItem> filteredList = new ArrayList<>(); // initially empty list

            if (constraint == null || constraint.length() == 0) { // search input field empty
                filteredList.addAll(mktplaceListFull); // to show everything
            } else {
                String userSearchInput = constraint.toString().toLowerCase().trim();

                for (MktplaceItem item : mktplaceListFull) {
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
            mktplaceList.clear();
            mktplaceList.addAll((List) results.values); // data list contains filtered items
            notifyDataSetChanged(); // tell adapter list has changed
        }
    };

    //Constructor for MktplaceAdapter class. This ArrayList contains the
    //complete list of items that we want to add to the View.

    /**
     * Constructor for the MktplaceAdapter class.
     *
     * @param context Context that belongs to the Fragment
     * @param mktplaceList List of items that are added to the View
     */
    public MktplaceAdapter(FragmentActivity context, ArrayList<MktplaceItem> mktplaceList) {
        this.mktplaceContext = context;
        this.mktplaceList = mktplaceList;
        mktplaceListFull = new ArrayList<>(mktplaceList);
        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference("Users");
    }

    //inflate the items in a MktplaceViewHolder
    @NonNull
    @Override
    public MktplaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.marketplace_item, parent, false);
        MktplaceViewHolder evh = new MktplaceViewHolder(mktplaceContext, v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MktplaceViewHolder holder, final int position) {
        MktplaceItem uploadCurrent = mktplaceList.get(position);
        String imageUrl = uploadCurrent.getImageUrl();
        holder.titleView.setText(uploadCurrent.getName());
        holder.setLiked(MainActivity.getLikeMktplaceIDs().contains(uploadCurrent.getMktPlaceID()));
        holder.description = uploadCurrent.getDescription();
        holder.location = uploadCurrent.getLocation();
        holder.imageUrl = uploadCurrent.getImageUrl();
        holder.numLikes = uploadCurrent.getNumLikes();
        holder.mktPlaceID = uploadCurrent.getMktPlaceID();
        final String creatorUid = uploadCurrent.getCreatorID();
        holder.setCreatorUid(creatorUid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserItem selected = snapshot.getValue(UserItem.class);
                    String id = selected.getId();

                    if (creatorUid.equals(id)) {
                        String name = selected.getName();
                        holder.creatorView.setText(name);
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

        Glide.with(holder.imageView.getContext()).load(imageUrl).into(holder.imageView);

        holder.numLikesView.setText(Integer.toString(uploadCurrent.getNumLikes()));
        holder.setNumLikes(uploadCurrent.getNumLikes());
        String mktplaceID = uploadCurrent.getMktPlaceID();
        if (MainActivity.getLikeMktplaceIDs().contains(mktplaceID)) {
            holder.likeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
            holder.setLiked(true);
            holder.likeButton.setChecked(true);
        } else {
            holder.likeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
            holder.setLiked(false);
            holder.likeButton.setChecked(false);
        }

        holder.likeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder.likeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
                    holder.setLiked(true);

                    // send to LikeDatabase
                    MktplaceItem item = mktplaceList.get(position);
                    UserItem user = MainActivity.getCurrUser();
                    final String mktplaceID = item.getMktPlaceID();
                    final String userID = user.getId();
                    DatabaseReference likeMktplaceRef = firebaseDatabase.getReference("LikeMktplace");
                    LikeOccasionItem likeOccasionItem = new LikeOccasionItem(mktplaceID, userID);
                    likeMktplaceRef.push().setValue(likeOccasionItem);

                    // +1 to the Likes on the MktplaceItem
                    int currLikes = item.getNumLikes();
                    DatabaseReference mktplaceRef = firebaseDatabase.getReference("mktplace uploads");
                    mktplaceRef.child(mktplaceID).child("numLikes").setValue(currLikes + 1);
                    item.setNumLikes(currLikes + 1);
                    holder.setNumLikes(currLikes + 1);

                    // for display only
                    holder.numLikesView.setText(Integer.toString(currLikes + 1));

                } else {
                    holder.likeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                    holder.setLiked(false);

                    // Delete the entry from LikeDatabase
                    MktplaceItem item = mktplaceList.get(position);
                    UserItem user = MainActivity.getCurrUser();
                    final String mktplaceID = item.getMktPlaceID();
                    final String userID = user.getId();
                    final DatabaseReference likeMktplaceRef = firebaseDatabase.getReference("LikeMktplace");
                    likeMktplaceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                                if (mktplaceID.equals(selected.getOccasionID())
                                    && userID.equals(selected.getUserID())) {
                                    String key = snapshot.getKey();
                                    likeMktplaceRef.child(key).removeValue();
                                    Toast.makeText(mktplaceContext, "Unliked", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    // -1 to the Likes on the eventItem
                    int currLikes = item.getNumLikes();
                    DatabaseReference mktplaceRef = firebaseDatabase.getReference("mktplace uploads");
                    mktplaceRef.child(mktplaceID).child("numLikes").setValue(currLikes - 1);
                    item.setNumLikes(currLikes - 1);
                    holder.setNumLikes(currLikes - 1);

                    // for display only
                    holder.numLikesView.setText(Integer.toString(currLikes - 1));

                    MainActivity.getLikeMktplaceIDs().remove(mktplaceID);

                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mktplaceList.size();
    }

    @Override
    public Filter getFilter() { // for the 'implements Filterable'
        return mktplaceFilter;
    }


    public void resetAdapter() {
        this.mktplaceList = mktplaceListFull;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
