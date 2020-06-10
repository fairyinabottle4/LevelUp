package com.Mktplace.LevelUp.ui.mktplace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tryone.R;

import java.util.ArrayList;

public class MktplaceAdapter extends RecyclerView.Adapter<MktplaceAdapter.MktplaceViewHolder> {
    //ArrayList is passed in from MktplaceItem.java
    private Context mContext;
    private ArrayList<MktplaceItem> mMktplaceList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    //the ViewHolder holds the content of the card
    public static class MktplaceViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTitle;

        public MktplaceViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTitle = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    //Constructor for MktplaceAdapter class. This ArrayList contains the
    //complete list of items that we want to add to the View.
    public MktplaceAdapter(Context context, ArrayList<MktplaceItem> MktplaceList) {
        this.mContext = context;
        mMktplaceList = MktplaceList;
    }

    //inflate the items in a MktplaceViewHolder
    @NonNull
    @Override
    public MktplaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.marketplace_item, parent, false);
        MktplaceViewHolder evh = new MktplaceViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull MktplaceViewHolder holder, int position) {
        MktplaceItem uploadCurrent = mMktplaceList.get(position);
        String url = uploadCurrent.getImageUrl();
        holder.mTitle.setText(uploadCurrent.getName());
        Glide.with(holder.mImageView.getContext()).load(url).into(holder.mImageView);

    }


    @Override
    public int getItemCount() {
        return mMktplaceList.size();
    }
}
