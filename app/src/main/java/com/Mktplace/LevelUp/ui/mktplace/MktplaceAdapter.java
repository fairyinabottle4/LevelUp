package com.Mktplace.LevelUp.ui.mktplace;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.Events.LevelUp.ui.events.EventsItem;
import com.Jios.LevelUp.ui.jios.JiosPage;
import com.bumptech.glide.Glide;
import com.example.tryone.R;

import java.util.ArrayList;
import java.util.List;

public class MktplaceAdapter extends RecyclerView.Adapter<MktplaceAdapter.MktplaceViewHolder> implements Filterable {
    //ArrayList is passed in from MktplaceItem.java
    private Context mContext;

    private ArrayList<MktplaceItem> mMktplaceList;
    private ArrayList<MktplaceItem> mMktplaceListFull;
    private OnItemClickListener mListener;

    private static String imageUrl;
    private static String title;
    private static String location;
    private static String description;

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

        public MktplaceViewHolder(final Context context, View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTitle = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, MktplaceAdapter.getTitle(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, MktplacePage.class);
                    intent.putExtra("title", MktplaceAdapter.getTitle());
                    intent.putExtra("description", MktplaceAdapter.getDescription());
                    intent.putExtra("location", MktplaceAdapter.getLocation());
                    intent.putExtra("imageurl", MktplaceAdapter.getImageUrl());
                    context.startActivity(intent);

                }
            });
        }
    }

    //Constructor for MktplaceAdapter class. This ArrayList contains the
    //complete list of items that we want to add to the View.
    public MktplaceAdapter(Context context, ArrayList<MktplaceItem> MktplaceList) {
        this.mContext = context;
        mMktplaceList = MktplaceList;
        mMktplaceListFull = new ArrayList<>(MktplaceList);
    }

    //inflate the items in a MktplaceViewHolder
    @NonNull
    @Override
    public MktplaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.marketplace_item, parent, false);
        MktplaceViewHolder evh = new MktplaceViewHolder(mContext,v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull MktplaceViewHolder holder, int position) {
        MktplaceItem uploadCurrent = mMktplaceList.get(position);
        imageUrl = uploadCurrent.getImageUrl();
        title = uploadCurrent.getName();
        description = uploadCurrent.getDescription();
        location = uploadCurrent.getLocation();
        String url = uploadCurrent.getImageUrl();
        holder.mTitle.setText(uploadCurrent.getName());
        Glide.with(holder.mImageView.getContext()).load(url).into(holder.mImageView);
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

    public static String getTitle() {
        return title;
    }

    public static String getImageUrl() {
        return imageUrl;
    }

    public static String getLocation() {
        return location;
    }

    public static String getDescription() {
        return description;
    }
}
