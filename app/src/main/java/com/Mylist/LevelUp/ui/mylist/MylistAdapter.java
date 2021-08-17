package com.Mylist.LevelUp.ui.mylist;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.ActivityOccasionItem;
import com.Events.LevelUp.ui.events.EventPage;
import com.Jios.LevelUp.ui.jios.JiosPage;
import com.LikeOccasionItem;
import com.MainActivity;
import com.UserItem;
import com.UserProfile;
import com.example.LevelUp.ui.Occasion;
import com.example.tryone.R;
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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
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

public class MylistAdapter extends RecyclerView.Adapter<MylistAdapter.MylistViewHolder> implements Filterable {
    // ArrayList is passed in from Occasion.java
    // ?? isnt it  passed in from MylistFragment -yien
    private ArrayList<Occasion> mylistList;
    private ArrayList<Occasion> mylistListFull;
    private MylistAdapter.OnItemClickListener mylistListener;
    private DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
    private Context fragContext;

    private StorageReference profileStorageRef;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;

    private Filter myListFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Occasion> filteredList = new ArrayList<>(); // initially empty list

            if (constraint == null || constraint.length() == 0) { // search input field empty
                filteredList.addAll(mylistListFull); // to show everything
            } else {
                String userSearchInput = constraint.toString().toLowerCase().trim();

                for (Occasion item : mylistListFull) {
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
            mylistList.clear();
            mylistList.addAll((List) results.values); // data list contains filtered items
            notifyDataSetChanged(); // tell adapter list has changed
        }
    };

    /**
     * Constructor for the MylistAdapter class.
     *
     * @param context Context of the fragment
     * @param mylistList Contains the complete list of items to be added to the View
     */
    public MylistAdapter(Context context, ArrayList<Occasion> mylistList) {
        fragContext = context;
        this.mylistList = mylistList;
        mylistListFull = new ArrayList<>(mylistList);
        profileStorageRef = FirebaseStorage.getInstance()
            .getReference("profile picture uploads");
        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference("Users");
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(MylistAdapter.OnItemClickListener listener) {
        mylistListener = listener;
    }

    //the ViewHolder holds the content of the card
    public static class MylistViewHolder extends RecyclerView.ViewHolder {
        private String creatorName;
        private String creatorUid;
        private int creatorResidence;
        private String profilePictureUri;
        private String email;
        private long phone;
        private String telegram;
        private String occID;
        private boolean isJio;
        private boolean isLiked;
        private int numLikes;

        private ToggleButton addButton;
        private ToggleButton likeButton;
        private ImageView imageView;
        private TextView titleView;
        private TextView descView;
        private TextView timeView;
        private TextView locationView;
        private TextView dateView;
        private TextView creatorView;
        private TextView numLikesView;

        /**
         * Constructor of the MylistViewHolder class
         *
         * @param context Context of the fragment it is in
         * @param itemView Item to be displayed
         * @param listener Listener which activates when the user clicks the item
         */
        public MylistViewHolder(final Context context, View itemView,
                                final MylistAdapter.OnItemClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
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
            addButton = itemView.findViewById(R.id.image_add);
            likeButton = itemView.findViewById(R.id.image_like);
            titleView = itemView.findViewById(R.id.title);
            descView = itemView.findViewById(R.id.event_description);
            timeView = itemView.findViewById(R.id.time);
            locationView = itemView.findViewById(R.id.location);
            dateView = itemView.findViewById(R.id.date);
            creatorView = itemView.findViewById(R.id.eventCreator);
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
            numLikesView = itemView.findViewById(R.id.numlikes_textview);

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
                    intent.putExtra("title", titleView.getText().toString());
                    intent.putExtra("description", descView.getText().toString());
                    intent.putExtra("date", dateView.getText().toString());
                    intent.putExtra("location", locationView.getText().toString());
                    intent.putExtra("time", timeView.getText().toString());
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

    //inflate the items in a MylistViewHolder
    @NonNull
    @Override
    public MylistAdapter.MylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.occ_item, parent, false);
        MylistAdapter.MylistViewHolder evh = new MylistAdapter.MylistViewHolder(fragContext, v, mylistListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull MylistAdapter.MylistViewHolder holder, final int position) {
        final Occasion currentItem = mylistList.get(position);
        UserItem user = MainActivity.getCurrUser();
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String occID = currentItem.getOccasionID();
        final DatabaseReference activityJioRef = firebaseDatabase.getReference("ActivityJio");
        final DatabaseReference activityEventRef = firebaseDatabase.getReference("ActivityEvent");

        final MylistAdapter.MylistViewHolder viewHolder = holder;
        final String creatorUid = currentItem.getCreatorID();
        viewHolder.setCreatorUid(creatorUid);
        viewHolder.setOccID(occID);
        viewHolder.setIsJio(currentItem.isJio());
        StorageReference profileStorageRefIndiv = profileStorageRef.child(creatorUid);
        profileStorageRefIndiv.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(viewHolder.imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                viewHolder.imageView.setImageResource(R.drawable.fake_user_dp);
            }
        });

        viewHolder.titleView.setText(currentItem.getTitle());
        viewHolder.descView.setText(currentItem.getDescription());
        viewHolder.timeView.setText(currentItem.getTimeInfo());
        viewHolder.locationView.setText(currentItem.getLocationInfo());
        viewHolder.dateView.setText(df.format(currentItem.getDateInfo()));
        holder.numLikesView.setText(Integer.toString(currentItem.getNumLikes()));

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserItem selected = snapshot.getValue(UserItem.class);
                    String id = selected.getId();

                    if (creatorUid.equals(id)) {
                        String name = selected.getName();
                        viewHolder.creatorView.setText(name);
                        viewHolder.setCreatorName(name);
                        int res = selected.getResidential();
                        String telegram = selected.getTelegram();
                        String email = selected.getEmail();
                        String dpUri = selected.getProfilePictureUri();
                        long phone = selected.getPhone();
                        viewHolder.setCreatorName(name);
                        viewHolder.setCreatorResidence(res);
                        viewHolder.setTelegram(telegram);
                        viewHolder.setEmail(email);
                        viewHolder.setProfilePictureUri(dpUri);
                        viewHolder.setPhone(phone);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewHolder.addButton.setBackgroundResource(R.drawable.ic_done_black_24dp);
        viewHolder.addButton.setChecked(true);
        final Handler handler = new Handler();
        final Runnable myRun = new Runnable() {
            @Override
            public void run() {
                // delete from Database
                // Jio
                activityJioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                            if (occID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
                                String key = snapshot.getKey();
                                activityJioRef.child(key).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //Events
                activityEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                            if (occID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
                                String key = snapshot.getKey();
                                activityEventRef.child(key).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if (MainActivity.getJioIds().contains(occID)) {
                    MainActivity.getJioIds().remove(occID);
                }

                if (MainActivity.getEventIDs().contains(occID)) {
                    MainActivity.getEventIDs().remove(occID);
                }


                mylistList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mylistList.size());

            }
        };

        viewHolder.addButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    viewHolder.addButton.setBackgroundResource(R.drawable.ic_done_black_24dp);
                    // handler.removeCallbacks(myRun);
                    Toast.makeText(buttonView.getContext(),
                        "Item added back to your list.", Toast.LENGTH_SHORT).show();

                } else {
                    viewHolder.addButton.setBackgroundResource(R.drawable.ic_add_black_24dp);
                    handler.postDelayed(myRun, 0000);
                    Toast.makeText(buttonView.getContext(),
                        "Item removed from your list.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (MainActivity.getLikeEventIDs().contains(occID) || MainActivity.getLikeJioIDs().contains(occID)) {
            viewHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
            viewHolder.setLiked(true);
            viewHolder.likeButton.setChecked(true);
        } else {
            viewHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
            viewHolder.setLiked(false);
            viewHolder.likeButton.setChecked(false);
        }

        viewHolder.setNumLikes(currentItem.getNumLikes());

        viewHolder.likeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int currLikes = currentItem.getNumLikes();

                if (isChecked) {
                    viewHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
                    viewHolder.numLikesView.setText(Integer.toString(currLikes + 1)); // for display only

                    if (currentItem.isJio()) {
                        // push to LikeJio Database
                        DatabaseReference mLikeJioRef = firebaseDatabase.getReference("LikeJio");
                        LikeOccasionItem likeOccasionItem = new LikeOccasionItem(occID, userID);
                        mLikeJioRef.push().setValue(likeOccasionItem);

                        // +1 to the Likes on the jiosItem
                        DatabaseReference jioRef = firebaseDatabase.getReference("Jios");
                        jioRef.child(occID).child("numLikes").setValue(currLikes + 1);
                        viewHolder.setNumLikes(currLikes + 1);

                    } else {
                        // push to LikeEvent Database
                        DatabaseReference mLikeEventRef = firebaseDatabase.getReference("LikeEvent");
                        LikeOccasionItem likeOccasionItem = new LikeOccasionItem(occID, userID);
                        mLikeEventRef.push().setValue(likeOccasionItem);

                        // +1 to the Likes on the eventsItem
                        DatabaseReference eventRef = firebaseDatabase.getReference("Events");
                        eventRef.child(occID).child("numLikes").setValue(currLikes + 1);
                        viewHolder.setNumLikes(currLikes + 1);
                    }

                } else {
                    viewHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                    viewHolder.numLikesView.setText(Integer.toString(currLikes - 1)); // for display only

                    // delete and -1 from both jio and events
                    if (currentItem.isJio()) {
                        // Delete the entry from LikeJio
                        final DatabaseReference mLikeJioRef = firebaseDatabase.getReference("LikeJio");
                        mLikeJioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                                    if (occID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
                                        String key = snapshot.getKey();
                                        mLikeJioRef.child(key).removeValue();
                                        Toast.makeText(fragContext, "Unliked", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        // -1 from jioItem
                        DatabaseReference jioRef = firebaseDatabase.getReference("Jios");
                        jioRef.child(occID).child("numLikes").setValue(currLikes - 1);
                        viewHolder.setNumLikes(currLikes - 1);

                        MainActivity.getLikeJioIDs().remove(occID);
                    } else {
                        // delete item from LikeEvent
                        final DatabaseReference mLikeEventRef = firebaseDatabase.getReference("LikeEvent");
                        mLikeEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                                    if (occID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
                                        String key = snapshot.getKey();
                                        mLikeEventRef.child(key).removeValue();
                                        Toast.makeText(fragContext, "Unliked", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        // -1 to the Likes on the eventItem
                        DatabaseReference eventRef = firebaseDatabase.getReference("Events");
                        eventRef.child(occID).child("numLikes").setValue(currLikes - 1);
                        viewHolder.setNumLikes(currLikes - 1);

                        MainActivity.getLikeEventIDs().remove(occID);
                    }
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mylistList.size();
    }

    @Override
    public Filter getFilter() { // for the 'implements Filterable'
        return myListFilter;
    }

    public MylistAdapter resetAdapter() {
        return new MylistAdapter(fragContext, mylistListFull);
    }
}
