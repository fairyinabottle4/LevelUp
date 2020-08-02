package com.Events.LevelUp.ui.events;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ActivityOccasionItem;
import com.LikeOccasionItem;
import com.MainActivity;
import com.UserItem;
import com.UserProfile;
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

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewHolder> implements Filterable {
    private ArrayList<EventsItem> mEventsList;
    private ArrayList<EventsItem> mEventsListFull;
    private StorageReference mProfileStorageRef;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserRef;


    private FragmentActivity mContext;
    private DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);


    // ViewHolder holds the content of the card
    public static class EventsViewHolder extends RecyclerView.ViewHolder {
        public String creatorUid;
        public String creatorName;
        public int creatorResidence;
        public String profilePictureUri;
        public String email;
        public long phone;
        public String telegram;

        public String eventID;
        public boolean isChecked;
        public boolean isLiked;
        public int numLikes;

        public ImageView mImageView;
        public ToggleButton mAddButton;
        public ToggleButton mLikeButton;
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;
        public TextView mTextView4;
        public TextView mTextView5;
        public TextView mTextView6;
        public TextView mNumLikes;

        public View itemView;

        public EventsViewHolder(final Context context, View itemView) {
            super(itemView);
            this.itemView = itemView;
            mImageView = itemView.findViewById(R.id.imageView);
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserProfile.class);
                    intent.putExtra("creatorid", creatorUid);
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
            mTextView3 = itemView.findViewById(R.id.date);
            mTextView4 = itemView.findViewById(R.id.location);
            mTextView5 = itemView.findViewById(R.id.time);
            mTextView6 = itemView.findViewById(R.id.eventCreator);
            mNumLikes = itemView.findViewById(R.id.numlikes_textview);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     // Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show();
                     Intent intent = new Intent(context, EventPage.class);
                     intent.putExtra("uid", creatorUid);
                     intent.putExtra("creatorName", creatorName);
                     intent.putExtra("title", mTextView1.getText().toString());
                     intent.putExtra("description", mTextView2.getText().toString());
                     intent.putExtra("date", mTextView3.getText().toString());
                     intent.putExtra("location", mTextView4.getText().toString());
                     intent.putExtra("time", mTextView5.getText().toString());
                     intent.putExtra("position", getAdapterPosition());
                     intent.putExtra("stateChecked", isChecked);
                     intent.putExtra("stateLiked", isLiked);
                     intent.putExtra("numLikes", numLikes);
                     intent.putExtra("eventID", eventID);
                     context.startActivity(intent);
                }
            });

        }

        public void setCreatorUid(String newUID) {
            this.creatorUid = newUID;
        }

        public void setCreatorName(String creatorName) {
            this.creatorName = creatorName;
        }

        public void setCreatorResidence(int creatorResidence) {this.creatorResidence = creatorResidence;}

        public void setProfilePictureUri(String profilePictureUri) { this.profilePictureUri = profilePictureUri;}

        public void setEmail(String email) {this.email = email;}

        public void setTelegram(String telegram) { this.telegram = telegram;}

        public void setPhone(long phone) {this.phone = phone;}

        public void setChecked(boolean toSet) {this.isChecked = toSet; }

        public void setEventID(String eventID) {
            this.eventID = eventID;
        }

        public void setLiked(boolean liked) {
            isLiked = liked;
        }

        public void setNumLikes(int numLikes) {
            this.numLikes = numLikes;
        }
    } // static class EventsViewHolder ends here

    //Constructor for EventsAdapter class. This ArrayList contains the
    //complete list of items that we want to add to the View.
    public EventsAdapter(FragmentActivity context, ArrayList<EventsItem> EventsList) {
        mEventsList = EventsList;
        mContext = context;
        mEventsListFull = new ArrayList<>(EventsList);
        mProfileStorageRef = FirebaseStorage.getInstance()
                .getReference("profile picture uploads");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserRef = mFirebaseDatabase.getReference("Users");
    }

    //inflate the items in a EventsViewHolder
    @NonNull
    @Override
    public EventsAdapter.EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.occ_item, parent, false);
        EventsAdapter.EventsViewHolder evh = new EventsAdapter.EventsViewHolder(mContext, v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull EventsAdapter.EventsViewHolder holder, final int position) {
        final EventsItem currentItem = mEventsList.get(position);
//        holder.mImageView.setImageResource(currentItem.getProfilePicture());
        final EventsViewHolder holder1 = holder;
        final String creatorUID = currentItem.getCreatorID();
        holder1.setCreatorUid(creatorUID);
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
        holder1.mTextView4.setText(currentItem.getLocationInfo());

        String date = df.format(currentItem.getDateInfo());
        holder1.mTextView3.setText(date);

        String time = currentItem.getTimeInfo();
        holder1.mTextView5.setText(time);

        holder1.mNumLikes.setText(Integer.toString(currentItem.getNumLikes()));

        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserItem selected = snapshot.getValue(UserItem.class);
                    String id = selected.getId();

                    if (creatorUID.equals(id)) {
                        String name = selected.getName();
                        int res = selected.getResidential();
                        String telegram = selected.getTelegram();
                        String email = selected.getEmail();
                        String dpUri = selected.getProfilePictureUri();
                        long phone = selected.getPhone();
                        holder1.mTextView6.setText(name);
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


//        holder1.mAddButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EventsItem ei = mEventsList.get(position);
//
////                int index = EventsFragment.getEventsItemListCopy().indexOf(ei);
////                MylistFragment.setNumberEvents(index);
//
//                // add to ActivityEvent firebase
//                UserItem user = MainActivity.currUser;
//                String eventID = ei.getEventID();
//                String userID = user.getId();
//                DatabaseReference mActivityEventRef = mFirebaseDatabase.getReference("ActivityEvent");
//                ActivityOccasionItem activityOccasionItem = new ActivityOccasionItem(eventID, userID);
//                mActivityEventRef.push().setValue(activityOccasionItem);
//
//
//                Toast.makeText(mContext, "Event added to your list!", Toast.LENGTH_SHORT).show();
//            }
//        });

        // if currentItem is contained in Main's Event List, then mAddButton set state
        final String eventID = currentItem.getEventID();
        holder1.setEventID(eventID);
        if (MainActivity.mEventIDs.contains(eventID)) {
            holder1.mAddButton.setBackgroundResource(R.drawable.ic_done_black_24dp);
            holder1.setChecked(true);
            holder1.mAddButton.setChecked(true);
        } else {
            holder1.mAddButton.setBackgroundResource(R.drawable.ic_add_black_24dp);
            holder1.setChecked(false);
            holder1.mAddButton.setChecked(false);
        }

        holder1.mAddButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // change to tick
                    holder1.mAddButton.setBackgroundResource(R.drawable.ic_done_black_24dp);
                    holder1.setChecked(true);

                    EventsItem ei = mEventsList.get(position);

//                int index = EventsFragment.getEventsItemListCopy().indexOf(ei);
//                MylistFragment.setNumberEvents(index);

                    // add to ActivityEvent firebase
                    UserItem user = MainActivity.currUser;
                    String eventID = ei.getEventID();
                    String userID = user.getId();
                    DatabaseReference mActivityEventRef = mFirebaseDatabase.getReference("ActivityEvent");
                    ActivityOccasionItem activityOccasionItem = new ActivityOccasionItem(eventID, userID);
                    mActivityEventRef.push().setValue(activityOccasionItem);


                    Toast.makeText(mContext, "Event added to your list!", Toast.LENGTH_SHORT).show();
                } else {
                    // change back to plus
                    holder1.mAddButton.setBackgroundResource(R.drawable.ic_add_black_24dp);
                    holder1.setChecked(false);

                    // delete the entry from activity DB
                    EventsItem ei = mEventsList.get(position);
                    UserItem user = MainActivity.currUser;
                    final String eventID = ei.getEventID();
                    final String userID = user.getId();
                    final DatabaseReference mActivityEventRef = mFirebaseDatabase.getReference("ActivityEvent");
                    mActivityEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                                if (eventID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
                                    String key = snapshot.getKey();
                                    mActivityEventRef.child(key).removeValue();
                                    Toast.makeText(mContext, "Event removed from your list", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    MainActivity.mEventIDs.remove(eventID);
                }
            }
        });

        if (MainActivity.mLikeEventIDs.contains(eventID)) {
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
                if (isChecked) {
                    holder1.mLikeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
                    holder1.setLiked(true);

                    // send to LikeDatabase
                    EventsItem ei = mEventsList.get(position);
                    UserItem user = MainActivity.currUser;
                    final String eventID = ei.getEventID();
                    final String userID = user.getId();
                    DatabaseReference mLikeEventRef = mFirebaseDatabase.getReference("LikeEvent");
                    LikeOccasionItem likeOccasionItem = new LikeOccasionItem(eventID, userID);
                    mLikeEventRef.push().setValue(likeOccasionItem);

                    // +1 to the Likes on the eventItem
                    int currLikes = ei.getNumLikes();
                    DatabaseReference mEventRef = mFirebaseDatabase.getReference("Events");
                    mEventRef.child(eventID).child("numLikes").setValue(currLikes + 1);
                    ei.setNumLikes(currLikes + 1);
                    holder1.setNumLikes(currLikes + 1);

                    // for display only
                    holder1.mNumLikes.setText(Integer.toString(currLikes + 1));

                } else {
                    holder1.mLikeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                    holder1.setLiked(false);

                    // Delete the entry from LikeDatabse
                    EventsItem ei = mEventsList.get(position);
                    UserItem user = MainActivity.currUser;
                    final String eventID = ei.getEventID();
                    final String userID = user.getId();
                    final DatabaseReference mLikeEventRef = mFirebaseDatabase.getReference("LikeEvent");
                    mLikeEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                                if (eventID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
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
                    int currLikes = ei.getNumLikes();
                    DatabaseReference mEventRef = mFirebaseDatabase.getReference("Events");
                    mEventRef.child(eventID).child("numLikes").setValue(currLikes - 1);
                    ei.setNumLikes(currLikes - 1);
                    holder1.setNumLikes(currLikes -1);

                    // for display only
                    holder1.mNumLikes.setText(Integer.toString(currLikes - 1));

                    MainActivity.mLikeEventIDs.remove(eventID);
                }

            }
        });

    }

    //imageView.setImageResource(R.drawable.ic_favorite_red_24dp);
    @Override
    public int getItemCount() {
        return mEventsList.size();
    }

    @Override
    public Filter getFilter() { // for the 'implements Filterable'
        return eventsFilter;
    }

    private Filter eventsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<EventsItem> filteredList = new ArrayList<>(); // initially empty list

            if (constraint == null || constraint.length() == 0) { // search input field empty
                filteredList.addAll(mEventsListFull); // to show everything
            } else {
                String userSearchInput = constraint.toString().toLowerCase().trim();

                for (EventsItem item : mEventsListFull) {
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
            mEventsList.clear();
            mEventsList.addAll((List) results.values); // data list contains filtered items
            notifyDataSetChanged(); // tell adapter list has changed
        }
    };

    public void resetAdapter() {
        this.mEventsList = mEventsListFull;
    }


}
