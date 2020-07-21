package com.Mylist.LevelUp.ui.mylist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Mktplace.LevelUp.ui.mktplace.MktplaceAdapter;
import com.Mktplace.LevelUp.ui.mktplace.MktplaceItem;
import com.UserItem;
import com.bumptech.glide.Glide;
import com.example.tryone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MktplaceCreatedAdapter extends RecyclerView.Adapter<MktplaceCreatedAdapter.MktplaceCreatedViewHolder>{
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
    public static class MktplaceCreatedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public String creatorUid;
        public String creatorName;
        public String mktPlaceID;

        public ImageView mImageView;
        public TextView mTitle;
        public TextView mCreatorName;
        private MktplaceCreatedAdapter.OnItemClickListener mListener;

        public MktplaceCreatedViewHolder(final Context context, View itemView, final MktplaceCreatedAdapter.OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTitle = itemView.findViewById(R.id.textView);
            mCreatorName = itemView.findViewById(R.id.creatorTextView);
            mListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(getAdapterPosition());
        }

        public void setCreatorName(String creatorName) {
            this.creatorName = creatorName;
        }
    }

    //Constructor for MktplaceAdapter class. This ArrayList contains the
    //complete list of items that we want to add to the View.
    public MktplaceCreatedAdapter(Context context, ArrayList<MktplaceItem> MktplaceList, MktplaceCreatedAdapter.OnItemClickListener listener) {
        this.mContext = context;
        mMktplaceList = MktplaceList;
        mMktplaceListFull = new ArrayList<>(MktplaceList);
        mAdapterListener = listener;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserRef = mFirebaseDatabase.getReference("Users");
    }

    //inflate the items in a MktplaceViewHolder
    @NonNull
    @Override
    public MktplaceCreatedAdapter.MktplaceCreatedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.marketplace_item_edit, parent, false);
        MktplaceCreatedAdapter.MktplaceCreatedViewHolder evh = new MktplaceCreatedAdapter.MktplaceCreatedViewHolder(mContext,v, mAdapterListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MktplaceCreatedAdapter.MktplaceCreatedViewHolder holder, int position) {
        MktplaceItem uploadCurrent = mMktplaceList.get(position);
        String imageUrl = uploadCurrent.getImageUrl();
        holder.mTitle.setText(uploadCurrent.getName());

        final String creatorUID = uploadCurrent.getCreatorID();

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
