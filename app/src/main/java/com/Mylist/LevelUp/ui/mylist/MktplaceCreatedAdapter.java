package com.Mylist.LevelUp.ui.mylist;

import java.util.ArrayList;

import com.Mktplace.LevelUp.ui.mktplace.MktplaceItem;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MktplaceCreatedAdapter extends RecyclerView.Adapter<MktplaceCreatedAdapter.MktplaceCreatedViewHolder> {
    //ArrayList is passed in from MktplaceItem.java
    private Context mktplaceContext;

    private ArrayList<MktplaceItem> mktplaceList;
    private ArrayList<MktplaceItem> mktplaceListFull;
    private MktplaceCreatedAdapter.OnItemClickListener adapterListener;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    //the ViewHolder holds the content of the card
    public static class MktplaceCreatedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
        private Button editButton;
        private Button peopleLikedButton;
        private MktplaceCreatedAdapter.OnItemClickListener itemListener;

        /**
         * Constructor for the ViewHolder that is used in MktplaceCreatedAdapter
         *
         * @param context Context of the fragment it is placed in
         * @param itemView The view that will contain the item
         * @param listener Listener that acts upon user input
         */
        public MktplaceCreatedViewHolder(final Context context, View itemView,
                                         final MktplaceCreatedAdapter.OnItemClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
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
            editButton = itemView.findViewById(R.id.mktplace_edit_btn);
            peopleLikedButton = itemView.findViewById(R.id.image_people_liked);
            itemListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemListener.onItemClick(getAdapterPosition());
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

    /**
     * Constructor for MktplaceCreatedAdapter
     *
     * @param context Context of the fragment it is placed in.
     * @param mktplaceList List of items to be displayed, namely the list of Marketplace listings
     *                     made by the user.
     * @param listener Listener that acts upon user selecting an item
     */
    public MktplaceCreatedAdapter(Context context, ArrayList<MktplaceItem> mktplaceList,
                                  MktplaceCreatedAdapter.OnItemClickListener listener) {
        this.mktplaceContext = context;
        this.mktplaceList = mktplaceList;
        mktplaceListFull = new ArrayList<>(mktplaceList);
        adapterListener = listener;
        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference("Users");
    }

    //inflate the items in a MktplaceViewHolder
    @NonNull
    @Override
    public MktplaceCreatedAdapter.MktplaceCreatedViewHolder onCreateViewHolder(
        @NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.marketplace_item_edit,
            parent, false);
        MktplaceCreatedAdapter.MktplaceCreatedViewHolder evh = new MktplaceCreatedAdapter
            .MktplaceCreatedViewHolder(mktplaceContext, v, adapterListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MktplaceCreatedAdapter.MktplaceCreatedViewHolder holder, int position) {
        final MktplaceItem uploadCurrent = mktplaceList.get(position);
        final String imageUrl = uploadCurrent.getImageUrl();
        final String mktplaceID = uploadCurrent.getMktPlaceID();
        final String creatorUid = uploadCurrent.getCreatorID();
        holder.setCreatorUid(creatorUid);
        final String titleView = uploadCurrent.getName();
        final String location = uploadCurrent.getLocation();
        final String description = uploadCurrent.getDescription();

        holder.titleView.setText(uploadCurrent.getName());

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mktplaceContext, EditMarketPlaceActivity.class);
                intent.putExtra("imageurl", imageUrl);
                intent.putExtra("mktplaceID", mktplaceID);
                intent.putExtra("titleView", titleView);
                intent.putExtra("location", location);
                intent.putExtra("description", description);
                intent.putExtra("creatorUid", creatorUid);
                mktplaceContext.startActivity(intent);
            }
        });

        holder.peopleLikedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mktplaceContext, CreatorViewLikeNamesMktplace.class);
                String occID = uploadCurrent.getMktPlaceID();
                intent.putExtra("occID", occID);
                mktplaceContext.startActivity(intent);
            }
        });

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
    }


    @Override
    public int getItemCount() {
        return mktplaceList.size();
    }

    public void resetAdapter() {
        this.mktplaceList = mktplaceListFull;
    }
}
