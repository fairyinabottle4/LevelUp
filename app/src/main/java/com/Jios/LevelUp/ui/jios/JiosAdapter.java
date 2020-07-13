package com.Jios.LevelUp.ui.jios;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ActivityOccasionItem;
import com.MainActivity;
import com.UserItem;
import com.example.tryone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JiosAdapter extends RecyclerView.Adapter<JiosAdapter.JiosViewHolder> implements Filterable {
    //ArrayList is passed in from JiosItem.java
    private ArrayList<JiosItem> mJiosList;
    private ArrayList<JiosItem> mJiosListFull;
    private StorageReference mProfileStorageRef;
    private DatabaseReference mUserRef;
    private FirebaseDatabase mFirebaseDatabase;

    private Context mContext;
    private DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);

    //the ViewHolder holds the content of the card
    public static class JiosViewHolder extends RecyclerView.ViewHolder {
        public String uid;
        public String creatorName;

        public ImageView mAddButton;
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;
        public TextView mTextView4;
        public TextView mTextView5;
        public TextView mTextView6;

        public JiosViewHolder(final Context context, View itemView) {
            super(itemView);
            final Context context1 = context;
            mImageView = itemView.findViewById(R.id.imageView);
            mAddButton = itemView.findViewById(R.id.image_add);
            mTextView1 = itemView.findViewById(R.id.title);
            mTextView2 = itemView.findViewById(R.id.event_description);
            mTextView3 = itemView.findViewById(R.id.date);
            mTextView4 = itemView.findViewById(R.id.location);
            mTextView5 = itemView.findViewById(R.id.time);
            mTextView6 = itemView.findViewById(R.id.eventCreator);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, JiosPage.class);
                    intent.putExtra("uid", uid);
                    intent.putExtra("creatorName", creatorName);
                    intent.putExtra("title", mTextView1.getText().toString());
                    intent.putExtra("description", mTextView2.getText().toString());
                    intent.putExtra("date", mTextView3.getText().toString());
                    intent.putExtra("location", mTextView4.getText().toString());
                    intent.putExtra("time", mTextView5.getText().toString());
                    intent.putExtra("position", getAdapterPosition());
                    context.startActivity(intent);
                }
            });
        }

        public void setUid(String newUID) {
            this.uid = newUID;
        }

        public void setCreatorName(String creatorName) {
            this.creatorName = creatorName;
        }
    }

    //Constructor for JiosAdapter class. This ArrayList contains the
    //complete list of items that we want to add to the View.
    public JiosAdapter(Context context, ArrayList<JiosItem> JiosList) {
        mContext = context;
        mJiosList = JiosList;
        mJiosListFull = new ArrayList<>(JiosList); // copy of JiosList for SearchView
        mProfileStorageRef = FirebaseStorage.getInstance()
                .getReference("profile picture uploads");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserRef = mFirebaseDatabase.getReference("Users");
    }

    //inflate the items in a JiosViewHolder
    @NonNull
    @Override
    public JiosAdapter.JiosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        JiosAdapter.JiosViewHolder evh = new JiosAdapter.JiosViewHolder(mContext, v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull JiosAdapter.JiosViewHolder holder, final int position) {
        JiosItem currentItem = mJiosList.get(position);
//        holder.mImageView.setImageResource(currentItem.getProfilePicture());
        final JiosViewHolder holder1 = holder;
        final String creatorUID = currentItem.getCreatorID();
        holder1.setUid(creatorUID);
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

        holder1.mTextView1.setText(currentItem.getTitle());
        holder1.mTextView2.setText(currentItem.getDescription());
        holder1.mTextView3.setText(df.format(currentItem.getDateInfo()));
        holder1.mTextView4.setText(currentItem.getLocationInfo());
        holder1.mTextView5.setText(currentItem.getTimeInfo());

        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserItem selected = snapshot.getValue(UserItem.class);
                    String id = selected.getId();

                    if (creatorUID.equals(id)) {
                        String name = selected.getName();
                        holder1.mTextView6.setText(name);
                        holder1.setCreatorName(name);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder1.mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JiosItem ji = mJiosList.get(position);
//                //Now I am getting the unsorted copy from MainActivity instead of JiosFragment.
//                //This is because I need to have a sorted ArrayList in MainActivity to send to Mylist
//                int index = JiosFragment.getJiosItemListCopy().indexOf(ji);
//                MylistFragment.setNumberJios(index);
//                Toast.makeText(mContext, "Jio added to your list!", Toast.LENGTH_SHORT).show();

                // add to ActivityEvent firebase
                UserItem user = MainActivity.currUser;
                String jioID = ji.getJioID();
                String userID = user.getId();
                DatabaseReference mActivityJioRef = mFirebaseDatabase.getReference("ActivityJio");
                ActivityOccasionItem activityOccasionItem = new ActivityOccasionItem(jioID, userID);
                mActivityJioRef.push().setValue(activityOccasionItem);
                Toast.makeText(mContext, "Jio added to your list!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return mJiosList.size();
    }

    @Override
    public Filter getFilter() { // for the 'implements Filterable'
        return jiosFilter;
    }

    private Filter jiosFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<JiosItem> filteredList = new ArrayList<>(); // initially empty list

            if (constraint == null || constraint.length() == 0) { // search input field empty
                filteredList.addAll(mJiosListFull); // to show everything
            } else {
                String userSearchInput = constraint.toString().toLowerCase().trim();

                for (JiosItem item : mJiosListFull) {
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

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mJiosList.clear();
            mJiosList.addAll((List) results.values); // data list contains filtered items
            notifyDataSetChanged(); // tell adapter list has changed

        }
    };
}