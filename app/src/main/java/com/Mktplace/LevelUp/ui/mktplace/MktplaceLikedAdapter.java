package com.Mktplace.LevelUp.ui.mktplace;

import java.util.ArrayList;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class MktplaceLikedAdapter extends RecyclerView.Adapter<MktplaceLikedAdapter.MktplaceLikedViewHolder> {
    //ArrayList is passed in from MktplaceItem.java
    private FragmentActivity mktplaceContext;

    private ArrayList<MktplaceItem> mktplaceLikedList;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;

    //the ViewHolder holds the content of the card
    public static class MktplaceLikedViewHolder extends RecyclerView.ViewHolder {
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
         * Constructor for the View holder that will display the information of the Mktplaceitem
         * @param context Context of the fragment
         * @param itemView The view that will be contained in the view holder
         */
        public MktplaceLikedViewHolder(final Context context, View itemView) {
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
                    intent.putExtra("title", titleView.getText().toString());
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

    /**
     * Constructor for MktplaceLikedAdapter class. This ArrayList contains the complete
     * list of items that are added to the View
     * @param context Context of the fragment
     * @param mktplaceList List of items
     */
    public MktplaceLikedAdapter(FragmentActivity context, ArrayList<MktplaceItem> mktplaceList) {
        this.mktplaceContext = context;
        this.mktplaceLikedList = mktplaceList;
        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference("Users");
    }

    //inflate the items in a MktplaceViewHolder
    @NonNull
    @Override
    public MktplaceLikedAdapter.MktplaceLikedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.marketplace_item, parent,
            false);
        MktplaceLikedAdapter.MktplaceLikedViewHolder evh =
            new MktplaceLikedAdapter.MktplaceLikedViewHolder(mktplaceContext, v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MktplaceLikedAdapter.MktplaceLikedViewHolder holder,
                                 final int position) {
        MktplaceItem uploadCurrent = mktplaceLikedList.get(position);
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
                    MktplaceItem item = mktplaceLikedList.get(position);
                    UserItem user = MainActivity.getCurrUser();
                    final String mktplaceID = item.getMktPlaceID();
                    final String userID = user.getId();
                    DatabaseReference likeMktplaceRef = firebaseDatabase.getReference("LikeMktplace");
                    LikeOccasionItem likeOccasionItem = new LikeOccasionItem(mktplaceID, userID);
                    likeMktplaceRef.push().setValue(likeOccasionItem);

                    // +1 to the Likes on the MktplaceItem
                    int currLikes = item.getNumLikes();
                    DatabaseReference mMktplaceRef = firebaseDatabase.getReference("mktplace uploads");
                    mMktplaceRef.child(mktplaceID).child("numLikes").setValue(currLikes + 1);
                    item.setNumLikes(currLikes + 1);
                    holder.setNumLikes(currLikes + 1);

                    // for display only
                    holder.numLikesView.setText(Integer.toString(currLikes + 1));

                } else {
                    holder.likeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                    holder.setLiked(false);

                    // Delete the entry from LikeDatabase
                    MktplaceItem item = mktplaceLikedList.get(position);
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
                    DatabaseReference mMktplaceRef = firebaseDatabase.getReference("mktplace uploads");
                    mMktplaceRef.child(mktplaceID).child("numLikes").setValue(currLikes - 1);
                    item.setNumLikes(currLikes - 1);
                    holder.setNumLikes(currLikes - 1);

                    // for display only
                    holder.numLikesView.setText(Integer.toString(currLikes - 1));

                    MainActivity.getLikeMktplaceIDs().remove(mktplaceID);

                    mktplaceLikedList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mktplaceLikedList.size());

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mktplaceLikedList.size();
    }

}
