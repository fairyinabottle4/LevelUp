package com.Mylist.LevelUp.ui.mylist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.Gravity;
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
import androidx.recyclerview.widget.RecyclerView;


import com.ActivityOccasionItem;
import com.Events.LevelUp.ui.events.EventPage;
import com.Jios.LevelUp.ui.jios.JiosItem;
import com.Jios.LevelUp.ui.jios.JiosPage;
import com.LikeOccasionItem;
import com.MainActivity;
import com.UserItem;
import com.UserProfile;
import com.example.LevelUp.ui.Occasion;
import com.example.tryone.R;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

public class MylistAdapter extends RecyclerView.Adapter<MylistAdapter.MylistViewHolder> implements Filterable {
    // ArrayList is passed in from Occasion.java
    // ?? isnt it  passed in from MylistFragment -yien
    private ArrayList<Occasion> mMylistList;
    private ArrayList<Occasion> mMylistListFull;
    private MylistAdapter.OnItemClickListener mListener;
    private DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
    private Context mContext;

    private StorageReference mProfileStorageRef;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserRef;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(MylistAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    //the ViewHolder holds the content of the card
    public static class MylistViewHolder extends RecyclerView.ViewHolder {
        public String creatorName;
        public String creatorUid;
        public int creatorResidence;
        public String profilePictureUri;
        public String email;
        public long phone;
        public String telegram;
        public String occID;
        public boolean isJio;
        public boolean isLiked;
        public int numLikes;

        public ToggleButton mAddButton;
        public ToggleButton mLikeButton;
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;
        public TextView mTextView4;
        public TextView mTextView5;
        public TextView mTextView6;
        public TextView mNumLikes;

        public MylistViewHolder(final Context context, View itemView, final MylistAdapter.OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mImageView.setOnClickListener(new View.OnClickListener() {
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
            mAddButton = itemView.findViewById(R.id.image_add);
            mLikeButton = itemView.findViewById(R.id.image_like);
            mTextView1 = itemView.findViewById(R.id.title);
            mTextView2 = itemView.findViewById(R.id.event_description);
            mTextView3 = itemView.findViewById(R.id.time);
            mTextView4 = itemView.findViewById(R.id.location);
            mTextView5 = itemView.findViewById(R.id.date);
            mTextView6 = itemView.findViewById(R.id.eventCreator);
            mTextView6.setOnClickListener(new View.OnClickListener() {
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
            mNumLikes = itemView.findViewById(R.id.numlikes_textview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, EventPage.class);
                    if (isJio) {
                        intent = new Intent(context, JiosPage.class);
                        intent.putExtra("jioID", occID);
                        // Toast.makeText(context, "JIO!", Toast.LENGTH_SHORT).show();
                    }
                    intent.putExtra("uid", creatorUid);
                    intent.putExtra("creatorName", creatorName);
                    intent.putExtra("title", mTextView1.getText().toString());
                    intent.putExtra("description", mTextView2.getText().toString());
                    intent.putExtra("date", mTextView5.getText().toString());
                    intent.putExtra("location", mTextView4.getText().toString());
                    intent.putExtra("time", mTextView3.getText().toString());
                    intent.putExtra("position", getAdapterPosition());
                    intent.putExtra("eventID", occID);
                    intent.putExtra("stateLiked", isLiked);
                    intent.putExtra("numLikes", numLikes);
                    context.startActivity(intent);
                }
            });
        }

        public void setCreatorName(String creatorName) {
            this.creatorName = creatorName;
        }

        public void setCreatorUid(String creatorUid) {
            this.creatorUid = creatorUid;
        }

        public void setCreatorResidence(int creatorResidence) {this.creatorResidence = creatorResidence;}

        public void setProfilePictureUri(String profilePictureUri) { this.profilePictureUri = profilePictureUri;}

        public void setEmail(String email) {this.email = email;}

        public void setTelegram(String telegram) { this.telegram = telegram;}

        public void setPhone(long phone) {this.phone = phone;}

        public void setOccID(String occID) {
            this.occID = occID;
        }

        public void setIsJio(boolean jio) {
            isJio = jio;
        }

        public void setLiked(boolean liked) {
            isLiked = liked;
        }

        public void setNumLikes(int numLikes) {
            this.numLikes = numLikes;
        }
    } // static class ends here

    //Constructor for MylistAdapter class. This ArrayList contains the
    //complete list of items that we want to add to the View.
    public MylistAdapter(Context context, ArrayList<Occasion> MylistList) {
        mContext = context;
        mMylistList = MylistList;
        mMylistListFull = new ArrayList<>(MylistList);
        mProfileStorageRef = FirebaseStorage.getInstance()
                .getReference("profile picture uploads");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserRef = mFirebaseDatabase.getReference("Users");
    }

    //inflate the items in a MylistViewHolder
    @NonNull
    @Override
    public MylistAdapter.MylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.occ_item, parent, false);
        MylistAdapter.MylistViewHolder evh = new MylistAdapter.MylistViewHolder(mContext, v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull MylistAdapter.MylistViewHolder holder, final int position) {
        final Occasion currentItem = mMylistList.get(position);
        UserItem user = MainActivity.currUser;
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String occID = currentItem.getOccasionID();
        final DatabaseReference mActivityJioRef = mFirebaseDatabase.getReference("ActivityJio");
        final DatabaseReference mActivityEventRef = mFirebaseDatabase.getReference("ActivityEvent");

        final MylistAdapter.MylistViewHolder holder1 = holder;
        final String creatorUID = currentItem.getCreatorID();
        holder1.setCreatorUid(creatorUID);
        holder1.setOccID(occID);
        holder1.setIsJio(currentItem.isJio());
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
        holder1.mTextView3.setText(currentItem.getTimeInfo());
        holder1.mTextView4.setText(currentItem.getLocationInfo());
        holder1.mTextView5.setText(df.format(currentItem.getDateInfo()));
        holder.mNumLikes.setText(Integer.toString(currentItem.getNumLikes()));

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
                        int res = selected.getResidential();
                        String telegram = selected.getTelegram();
                        String email = selected.getEmail();
                        String dpUri = selected.getProfilePictureUri();
                        long phone = selected.getPhone();
                        holder1.setCreatorName(name);
                        holder1.setCreatorResidence(res);
                        holder1.setTelegram(telegram);
                        holder1.setEmail(email);
                        holder1.setProfilePictureUri(dpUri);
                        holder1.setPhone(phone);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder1.mAddButton.setBackgroundResource(R.drawable.ic_done_black_24dp);
        holder1.mAddButton.setChecked(true);
        final Handler handler = new Handler();
        final Runnable myRun = new Runnable() {
            @Override
            public void run() {
                // delete from Database
                // Jio
                mActivityJioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                            if (occID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
                                String key = snapshot.getKey();
                                mActivityJioRef.child(key).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //Events
                mActivityEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                            if (occID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
                                String key = snapshot.getKey();
                                mActivityEventRef.child(key).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if (MainActivity.mJioIDs.contains(occID)) {
                    MainActivity.mJioIDs.remove(occID);
                }

                if(MainActivity.mEventIDs.contains(occID)) {
                    MainActivity.mEventIDs.remove(occID);
                }


                mMylistList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mMylistList.size());

            }
        };

        holder1.mAddButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder1.mAddButton.setBackgroundResource(R.drawable.ic_done_black_24dp);
                    // handler.removeCallbacks(myRun);
                    Toast.makeText(buttonView.getContext(), "Item added back to your list.", Toast.LENGTH_SHORT).show();

                } else {
                    holder1.mAddButton.setBackgroundResource(R.drawable.ic_add_black_24dp);
                    handler.postDelayed(myRun, 0000);
                    Toast.makeText(buttonView.getContext(), "Item removed from your list.", Toast.LENGTH_SHORT).show();

//                    String msg = "Item will be removed in 5s." + "\n" + "Press the + to add it back to your list.";
////                    Toast toast = Toast.makeText(buttonView.getContext(), msg, Toast.LENGTH_SHORT);
////                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
////                    v.setGravity(Gravity.CENTER);
////                    toast.show();


                }
            }
        });

        if (MainActivity.mLikeEventIDs.contains(occID) || MainActivity.mLikeJioIDs.contains(occID)) {
            holder1.mLikeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
            holder1.setLiked(true);
            holder1.mLikeButton.setChecked(true);
        } else {
            holder1.mLikeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
            holder1.setLiked(false);
            holder1.mLikeButton.setChecked(false);
        }

        holder1.setNumLikes(currentItem.getNumLikes());

        holder1.mLikeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int currLikes = currentItem.getNumLikes();

                if (isChecked) {
                    holder1.mLikeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
                    holder1.mNumLikes.setText(Integer.toString(currLikes + 1)); // for display only

                    if (currentItem.isJio()) {
                        // push to LikeJio Database
                        DatabaseReference mLikeJioRef = mFirebaseDatabase.getReference("LikeJio");
                        LikeOccasionItem likeOccasionItem = new LikeOccasionItem(occID, userID);
                        mLikeJioRef.push().setValue(likeOccasionItem);

                        // +1 to the Likes on the jiosItem
                        DatabaseReference mJioRef = mFirebaseDatabase.getReference("Jios");
                        mJioRef.child(occID).child("numLikes").setValue(currLikes + 1);
                        holder1.setNumLikes(currLikes + 1);

                    } else {
                        // push to LikeEvent Database
                        DatabaseReference mLikeEventRef = mFirebaseDatabase.getReference("LikeEvent");
                        LikeOccasionItem likeOccasionItem = new LikeOccasionItem(occID, userID);
                        mLikeEventRef.push().setValue(likeOccasionItem);

                        // +1 to the Likes on the eventsItem
                        DatabaseReference mEventRef = mFirebaseDatabase.getReference("Events");
                        mEventRef.child(occID).child("numLikes").setValue(currLikes + 1);
                        holder1.setNumLikes(currLikes + 1);
                    }

                } else {
                    holder1.mLikeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                    holder1.mNumLikes.setText(Integer.toString(currLikes - 1)); // for display only

                    // delete and -1 from both jio and events
                    if (currentItem.isJio()) {
                        // Delete the entry from LikeJio
                        final DatabaseReference mLikeJioRef = mFirebaseDatabase.getReference("LikeJio");
                        mLikeJioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                                    if (occID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
                                        String key = snapshot.getKey();
                                        mLikeJioRef.child(key).removeValue();
                                        Toast.makeText(mContext, "Unliked", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        // -1 from jioItem
                        DatabaseReference mJioRef = mFirebaseDatabase.getReference("Jios");
                        mJioRef.child(occID).child("numLikes").setValue(currLikes - 1);
                        holder1.setNumLikes(currLikes - 1);

                        MainActivity.mLikeJioIDs.remove(occID);
                    } else {
                        // delete item from LikeEvent
                        final DatabaseReference mLikeEventRef = mFirebaseDatabase.getReference("LikeEvent");
                        mLikeEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                                    if (occID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
                                        String key = snapshot.getKey();
                                        mLikeEventRef.child(key).removeValue();
                                        Toast.makeText(mContext, "Unliked", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        // -1 to the Likes on the eventItem
                        DatabaseReference mEventRef = mFirebaseDatabase.getReference("Events");
                        mEventRef.child(occID).child("numLikes").setValue(currLikes - 1);
                        holder1.setNumLikes(currLikes - 1);

                        MainActivity.mLikeEventIDs.remove(occID);
                    }
                }
            }
        });
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
                filteredList.addAll(mMylistListFull); // to show everything
            } else {
                String userSearchInput = constraint.toString().toLowerCase().trim();

                for (Occasion item : mMylistListFull) {
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
            mMylistList.clear();
            mMylistList.addAll((List) results.values); // data list contains filtered items
            notifyDataSetChanged(); // tell adapter list has changed
        }
    };

    public MylistAdapter resetAdapter() {
        return new MylistAdapter(mContext, mMylistListFull);
    }
}