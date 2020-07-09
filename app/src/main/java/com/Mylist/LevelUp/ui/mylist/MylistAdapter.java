package com.Mylist.LevelUp.ui.mylist;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.Events.LevelUp.ui.events.EventsItem;
import com.MainActivity;
import com.example.LevelUp.ui.Occasion;
import com.example.tryone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MylistAdapter extends RecyclerView.Adapter<MylistAdapter.MylistViewHolder> implements Filterable {
    //ArrayList is passed in from Occasion.java
    private ArrayList<Occasion> mMylistList;
    private MylistAdapter.OnItemClickListener mListener;
    private DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);

    private StorageReference mProfileStorageRef;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(MylistAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    //the ViewHolder holds the content of the card
    public static class MylistViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;
        public TextView mTextView4;
        public TextView mTextView5;

        public MylistViewHolder(View itemView, final MylistAdapter.OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.title);
            mTextView2 = itemView.findViewById(R.id.event_description);
            mTextView3 = itemView.findViewById(R.id.time);
            mTextView4 = itemView.findViewById(R.id.location);
            mTextView5 = itemView.findViewById(R.id.date);
        }
    }

    //Constructor for MylistAdapter class. This ArrayList contains the
    //complete list of items that we want to add to the View.
    public MylistAdapter(ArrayList<Occasion> MylistList) {

        mMylistList = MylistList;
        mProfileStorageRef = FirebaseStorage.getInstance()
                .getReference("profile picture uploads");
    }

    //inflate the items in a MylistViewHolder
    @NonNull
    @Override
    public MylistAdapter.MylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        MylistAdapter.MylistViewHolder evh = new MylistAdapter.MylistViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull MylistAdapter.MylistViewHolder holder, int position) {
        Occasion currentItem = mMylistList.get(position);

        final MylistAdapter.MylistViewHolder holder1 = holder;
        String creatorUID = currentItem.getCreatorID();
        StorageReference mProfileStorageRefIndiv = mProfileStorageRef.child(creatorUID);
        mProfileStorageRefIndiv.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder1.mImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                holder1.mImageView.setImageResource(R.drawable.fake_user_dp);
            }
        });

        holder.mTextView1.setText(currentItem.getTitle());
        holder.mTextView2.setText(currentItem.getDescription());
        holder.mTextView3.setText(currentItem.getTimeInfo());
        holder.mTextView4.setText(currentItem.getLocationInfo());
        holder.mTextView5.setText(df.format(currentItem.getDateInfo()));
    }


    @Override
    public int getItemCount() {
        return mMylistList.size();
    }

    @Override
    public Filter getFilter() { // for the 'implements Filterable'
        return myListFilter;
    }

    private Filter myListFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Occasion> filteredList = new ArrayList<>(); // initially empty list

            if (constraint == null || constraint.length() == 0) { // search input field empty
                filteredList.addAll(MainActivity.mOccasionListRealFull); // to show everything
            } else {
                String userSearchInput = constraint.toString().toLowerCase().trim();

                for (Occasion item : MainActivity.mOccasionListRealFull) {
                    // contains can be changed to StartsWith
                    if (item.getTitle().toLowerCase().contains(userSearchInput)) {
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
            MainActivity.mOccasionListReal.clear();
            MainActivity.mOccasionListReal.addAll((List) results.values); // data list contains filtered items
            notifyDataSetChanged(); // tell adapter list has changed

        }
    };
}
