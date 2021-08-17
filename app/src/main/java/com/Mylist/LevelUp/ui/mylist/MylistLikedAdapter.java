package com.Mylist.LevelUp.ui.mylist;

import java.text.DateFormat;
import java.util.ArrayList;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class MylistLikedAdapter extends RecyclerView.Adapter<MylistLikedAdapter.MylistLikedViewHolder> {
    // ArrayList is passed in from Occasion.java
    // ?? isnt it  passed in from MylistFragment -yien
    private ArrayList<Occasion> myListList;
    private ArrayList<Occasion> myListListFull;
    private MylistAdapter.OnItemClickListener itemListener;
    private DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
    private Context fragmentContext;

    private StorageReference profileStorageRef;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;

    //Constructor for MylistAdapter class. This ArrayList contains the
    //complete list of items that we want to add to the View.

    /**
     * Constructor for MylistLikedAdapter
     *
     * @param context Context of the fragment
     * @param mylistList Contains the complete list of items, which are liked Jios and Events,
     *                  to be added to the View
     */
    public MylistLikedAdapter(Context context, ArrayList<Occasion> mylistList) {
        fragmentContext = context;
        this.myListList = mylistList;
        myListListFull = new ArrayList<>(mylistList);
        profileStorageRef = FirebaseStorage.getInstance()
            .getReference("profile picture uploads");
        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference("Users");
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(MylistAdapter.OnItemClickListener listener) {
        itemListener = listener;
    }

    //the ViewHolder holds the content of the card
    public static class MylistLikedViewHolder extends RecyclerView.ViewHolder {
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
        private boolean isChecked;
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
         * Constructor for the ViewHolder that is used in MylistLikedAdapter
         *
         * @param context Context of the fragment it is placed in
         * @param itemView The view that will contain the item
         * @param listener Listener that acts upon user input
         */
        public MylistLikedViewHolder(final Context context, View itemView,
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
                    intent.putExtra("stateChecked", isChecked);
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

        public void setChecked(boolean toSet) {
            this.isChecked = toSet;
        }

        public void setNumLikes(int numLikes) {
            this.numLikes = numLikes;
        }
    } // static class ends here

    //inflate the items in a MylistViewHolder
    @NonNull
    @Override
    public MylistLikedAdapter.MylistLikedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.occ_item, parent, false);
        MylistLikedAdapter.MylistLikedViewHolder evh = new MylistLikedAdapter
            .MylistLikedViewHolder(fragmentContext, v, itemListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull MylistLikedAdapter.MylistLikedViewHolder holder, final int position) {
        final Occasion currentItem = myListList.get(position);
        UserItem user = MainActivity.getCurrUser();
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String occID = currentItem.getOccasionID();
        final DatabaseReference mActivityJioRef = firebaseDatabase.getReference("ActivityJio");
        final DatabaseReference mActivityEventRef = firebaseDatabase.getReference("ActivityEvent");

        final MylistLikedAdapter.MylistLikedViewHolder likedHolder = holder;
        final String creatorUid = currentItem.getCreatorID();
        likedHolder.setCreatorUid(creatorUid);
        likedHolder.setOccID(occID);
        likedHolder.setIsJio(currentItem.isJio());
        StorageReference mProfileStorageRefIndiv = profileStorageRef.child(creatorUid);
        mProfileStorageRefIndiv.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(likedHolder.imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                likedHolder.imageView.setImageResource(R.drawable.fake_user_dp);
            }
        });

        likedHolder.titleView.setText(currentItem.getTitle());
        likedHolder.descView.setText(currentItem.getDescription());
        likedHolder.timeView.setText(currentItem.getTimeInfo());
        likedHolder.locationView.setText(currentItem.getLocationInfo());
        likedHolder.dateView.setText(df.format(currentItem.getDateInfo()));
        holder.numLikesView.setText(Integer.toString(currentItem.getNumLikes()));

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserItem selected = snapshot.getValue(UserItem.class);
                    String id = selected.getId();

                    if (creatorUid.equals(id)) {
                        String name = selected.getName();
                        likedHolder.creatorView.setText(name);
                        likedHolder.setCreatorName(name);
                        int res = selected.getResidential();
                        String telegram = selected.getTelegram();
                        String email = selected.getEmail();
                        String dpUri = selected.getProfilePictureUri();
                        long phone = selected.getPhone();
                        likedHolder.setCreatorName(name);
                        likedHolder.setCreatorResidence(res);
                        likedHolder.setTelegram(telegram);
                        likedHolder.setEmail(email);
                        likedHolder.setProfilePictureUri(dpUri);
                        likedHolder.setPhone(phone);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (MainActivity.getJioIds().contains(occID) || MainActivity.getEventIDs().contains(occID)) {
            likedHolder.addButton.setBackgroundResource(R.drawable.ic_done_black_24dp);
            likedHolder.setChecked(true);
            likedHolder.addButton.setChecked(true);
        } else {
            likedHolder.addButton.setBackgroundResource(R.drawable.ic_add_black_24dp);
            likedHolder.setChecked(false);
            likedHolder.addButton.setChecked(false);
        }
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

                if (MainActivity.getJioIds().contains(occID)) {
                    MainActivity.getJioIds().remove(occID);
                }

                if (MainActivity.getEventIDs().contains(occID)) {
                    MainActivity.getEventIDs().remove(occID);
                }
            }
        };

        likedHolder.addButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    likedHolder.addButton.setBackgroundResource(R.drawable.ic_done_black_24dp);
                    // handler.removeCallbacks(myRun);
                    Toast.makeText(buttonView.getContext(), "Item added to your list.", Toast.LENGTH_SHORT).show();
                    if (currentItem.isJio()) {
                        // add to jioActiivityDB
                        DatabaseReference mActivityJioRef = firebaseDatabase.getReference("ActivityJio");
                        ActivityOccasionItem activityOccasionItem = new ActivityOccasionItem(occID, userID);
                        mActivityJioRef.push().setValue(activityOccasionItem);

                        MainActivity.getJioIds().add(currentItem.getOccasionID());
                    } else {
                        // add to eventActivityDB
                        DatabaseReference mActivityJioRef = firebaseDatabase.getReference("ActivityEvent");
                        ActivityOccasionItem activityOccasionItem = new ActivityOccasionItem(occID, userID);
                        mActivityJioRef.push().setValue(activityOccasionItem);

                        MainActivity.getEventIDs().add(currentItem.getOccasionID());
                    }

                } else {
                    likedHolder.addButton.setBackgroundResource(R.drawable.ic_add_black_24dp);
                    handler.postDelayed(myRun, 0000);
                    Toast.makeText(buttonView.getContext(), "Item removed from your list.", Toast.LENGTH_SHORT).show();

                }
            }
        });

        if (MainActivity.getLikeEventIDs().contains(occID) || MainActivity.getLikeJioIDs().contains(occID)) {
            likedHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
            likedHolder.setLiked(true);
            likedHolder.likeButton.setChecked(true);
        } else {
            likedHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
            likedHolder.setLiked(false);
            likedHolder.likeButton.setChecked(false);
        }

        likedHolder.setNumLikes(currentItem.getNumLikes());

        likedHolder.likeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int currLikes = currentItem.getNumLikes();

                if (isChecked) {
                    likedHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
                    likedHolder.numLikesView.setText(Integer.toString(currLikes + 1)); // for display only

                    if (currentItem.isJio()) {
                        // push to LikeJio Database
                        DatabaseReference likeJioRef = firebaseDatabase.getReference("LikeJio");
                        LikeOccasionItem likeOccasionItem = new LikeOccasionItem(occID, userID);
                        likeJioRef.push().setValue(likeOccasionItem);

                        // +1 to the Likes on the jiosItem
                        DatabaseReference jioRef = firebaseDatabase.getReference("Jios");
                        jioRef.child(occID).child("numLikes").setValue(currLikes + 1);
                        likedHolder.setNumLikes(currLikes + 1);

                    } else {
                        // push to LikeEvent Database
                        DatabaseReference likeEventRef = firebaseDatabase.getReference("LikeEvent");
                        LikeOccasionItem likeOccasionItem = new LikeOccasionItem(occID, userID);
                        likeEventRef.push().setValue(likeOccasionItem);

                        // +1 to the Likes on the eventsItem
                        DatabaseReference eventRef = firebaseDatabase.getReference("Events");
                        eventRef.child(occID).child("numLikes").setValue(currLikes + 1);
                        likedHolder.setNumLikes(currLikes + 1);
                    }

                } else {
                    likedHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                    likedHolder.numLikesView.setText(Integer.toString(currLikes - 1)); // for display only

                    myListList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, myListList.size());

                    // delete and -1 from both jio and events
                    if (currentItem.isJio()) {
                        // Delete the entry from LikeJio
                        final DatabaseReference likeJioRef = firebaseDatabase.getReference("LikeJio");
                        likeJioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                                    if (occID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
                                        String key = snapshot.getKey();
                                        likeJioRef.child(key).removeValue();
                                        Toast.makeText(fragmentContext, "Unliked", Toast.LENGTH_SHORT).show();
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
                        likedHolder.setNumLikes(currLikes - 1);

                        MainActivity.getLikeJioIDs().remove(occID);

                    } else {
                        // delete item from LikeEvent
                        final DatabaseReference likeEventRef = firebaseDatabase.getReference("LikeEvent");
                        likeEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                                    if (occID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
                                        String key = snapshot.getKey();
                                        likeEventRef.child(key).removeValue();
                                        Toast.makeText(fragmentContext, "Unliked", Toast.LENGTH_SHORT).show();
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
                        likedHolder.setNumLikes(currLikes - 1);

                        MainActivity.getLikeEventIDs().remove(occID);

                    }

                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public MylistLikedAdapter resetAdapter() {
        return new MylistLikedAdapter(fragmentContext, myListListFull);
    }
}
