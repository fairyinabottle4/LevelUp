package com.Events.LevelUp.ui.events;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewHolder> implements Filterable {
    private ArrayList<EventsItem> eventsList;
    private ArrayList<EventsItem> eventsListFull;
    private StorageReference profileStorageRef;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;


    private FragmentActivity eventsContext;
    private DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);


    // ViewHolder holds the content of the card
    public static class EventsViewHolder extends RecyclerView.ViewHolder {
        private String creatorUid;
        private String creatorName;
        private int creatorResidence;
        private String profilePictureUri;
        private String email;
        private long phone;
        private String telegram;

        private String eventID;
        private boolean isChecked;
        private boolean isLiked;
        private int numLikes;

        private ImageView imageView;
        private ToggleButton addButton;
        private ToggleButton likeButton;
        private TextView titleView;
        private TextView eventDesc;
        private TextView dateView;
        private TextView locationView;
        private TextView timeView;
        private TextView creatorView;
        private TextView numLikesView;

        private View itemView;

        /**
         * Constructor of the view holder that will hold the information displayed for Events
         *
         * @param context Context of the fragment that will contain the view holder
         * @param itemView Containers of information that will be displayed
         */
        public EventsViewHolder(final Context context, View itemView) {
            super(itemView);
            this.itemView = itemView;
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
            eventDesc = itemView.findViewById(R.id.event_description);
            dateView = itemView.findViewById(R.id.date);
            locationView = itemView.findViewById(R.id.location);
            timeView = itemView.findViewById(R.id.time);
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
                    intent.putExtra("uid", creatorUid);
                    intent.putExtra("creatorName", creatorName);
                    intent.putExtra("title", titleView.getText().toString());
                    intent.putExtra("description", eventDesc.getText().toString());
                    intent.putExtra("date", dateView.getText().toString());
                    intent.putExtra("location", locationView.getText().toString());
                    intent.putExtra("time", timeView.getText().toString());
                    intent.putExtra("position", getAdapterPosition());
                    intent.putExtra("stateChecked", isChecked);
                    intent.putExtra("stateLiked", isLiked);
                    intent.putExtra("numLikes", numLikes);
                    intent.putExtra("eventID", eventID);
                    intent.putExtra("residence", creatorResidence);
                    intent.putExtra("dpUri", profilePictureUri);
                    intent.putExtra("telegram", telegram);
                    intent.putExtra("email", email);
                    intent.putExtra("phone", phone);
                    context.startActivity(intent);
                }
            });

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

        public void setChecked(boolean toSet) {
            this.isChecked = toSet;
        }

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

    private Filter eventsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<EventsItem> filteredList = new ArrayList<>(); // initially empty list

            if (constraint == null || constraint.length() == 0) { // search input field empty
                filteredList.addAll(eventsListFull); // to show everything
            } else {
                String userSearchInput = constraint.toString().toLowerCase().trim();

                for (EventsItem item : eventsListFull) {
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
            eventsList.clear();
            eventsList.addAll((List) results.values); // data list contains filtered items
            notifyDataSetChanged(); // tell adapter list has changed
        }
    };


    /**
     * Constructor for EventsAdapter class
     *
     * @param context Context of the Fragment which will contain the adapter
     * @param eventsList Contains the complete list of items that are added to the view
     */
    public EventsAdapter(FragmentActivity context, ArrayList<EventsItem> eventsList) {
        this.eventsList = eventsList;
        eventsContext = context;
        eventsListFull = new ArrayList<>(eventsList);
        profileStorageRef = FirebaseStorage.getInstance()
                .getReference("profile picture uploads");
        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference("Users");
    }

    //inflate the items in a EventsViewHolder
    @NonNull
    @Override
    public EventsAdapter.EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.occ_item, parent, false);
        EventsAdapter.EventsViewHolder evh = new EventsAdapter.EventsViewHolder(eventsContext, v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull EventsAdapter.EventsViewHolder holder, final int position) {
        final EventsItem currentItem = eventsList.get(position);
        final EventsViewHolder eventsHolder = holder;
        final String creatorUid = currentItem.getCreatorID();
        eventsHolder.setCreatorUid(creatorUid);
        StorageReference profileStorageRefIndiv = profileStorageRef.child(creatorUid);
        profileStorageRefIndiv.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(eventsHolder.imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                eventsHolder.imageView.setImageResource(R.drawable.fake_user_dp);
            }
        });

        eventsHolder.titleView.setText(currentItem.getTitle());
        eventsHolder.eventDesc.setText(currentItem.getDescription());
        eventsHolder.locationView.setText(currentItem.getLocationInfo());

        String date = df.format(currentItem.getDateInfo());
        eventsHolder.dateView.setText(date);

        String time = currentItem.getTimeInfo();
        eventsHolder.timeView.setText(time);

        eventsHolder.numLikesView.setText(Integer.toString(currentItem.getNumLikes()));

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserItem selected = snapshot.getValue(UserItem.class);
                    String id = selected.getId();

                    if (creatorUid.equals(id)) {
                        String name = selected.getName();
                        int res = selected.getResidential();
                        String telegram = selected.getTelegram();
                        String email = selected.getEmail();
                        String dpUri = selected.getProfilePictureUri();
                        long phone = selected.getPhone();
                        eventsHolder.creatorView.setText(name);
                        eventsHolder.setCreatorName(name);
                        eventsHolder.setCreatorResidence(res);
                        eventsHolder.setTelegram(telegram);
                        eventsHolder.setEmail(email);
                        eventsHolder.setProfilePictureUri(dpUri);
                        eventsHolder.setPhone(phone);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // if currentItem is contained in Main's Event List, then addButton set state
        final String eventID = currentItem.getEventID();
        eventsHolder.setEventID(eventID);
        if (MainActivity.getEventIDs().contains(eventID)) {
            eventsHolder.addButton.setBackgroundResource(R.drawable.ic_done_black_24dp);
            eventsHolder.setChecked(true);
            eventsHolder.addButton.setChecked(true);
        } else {
            eventsHolder.addButton.setBackgroundResource(R.drawable.ic_add_black_24dp);
            eventsHolder.setChecked(false);
            eventsHolder.addButton.setChecked(false);
        }

        eventsHolder.addButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // change to tick
                    eventsHolder.addButton.setBackgroundResource(R.drawable.ic_done_black_24dp);
                    eventsHolder.setChecked(true);

                    EventsItem ei = eventsList.get(position);

                    //int index = EventsFragment.getEventsItemListCopy().indexOf(ei);
                    //MylistFragment.setNumberEvents(index);

                    // add to ActivityEvent firebase
                    UserItem user = MainActivity.getCurrUser();
                    String eventID = ei.getEventID();
                    String userID = user.getId();
                    DatabaseReference activityEventRef = firebaseDatabase.getReference("ActivityEvent");
                    ActivityOccasionItem activityOccasionItem = new ActivityOccasionItem(eventID, userID);
                    activityEventRef.push().setValue(activityOccasionItem);


                    Toast.makeText(eventsContext, "Event added to your list!", Toast.LENGTH_SHORT).show();
                } else {
                    // change back to plus
                    eventsHolder.addButton.setBackgroundResource(R.drawable.ic_add_black_24dp);
                    eventsHolder.setChecked(false);

                    // delete the entry from activity DB
                    EventsItem ei = eventsList.get(position);
                    UserItem user = MainActivity.getCurrUser();
                    final String eventID = ei.getEventID();
                    final String userID = user.getId();
                    final DatabaseReference activityEventRef = firebaseDatabase.getReference("ActivityEvent");
                    activityEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                                if (eventID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
                                    String key = snapshot.getKey();
                                    activityEventRef.child(key).removeValue();
                                    Toast.makeText(eventsContext, "Event removed from your list",
                                        Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    MainActivity.getEventIDs().remove(eventID);
                }
            }
        });

        if (MainActivity.getLikeEventIDs().contains(eventID)) {
            eventsHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
            eventsHolder.setLiked(true);
            eventsHolder.likeButton.setChecked(true);
        } else {
            eventsHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
            eventsHolder.setLiked(false);
            eventsHolder.likeButton.setChecked(false);
        }

        eventsHolder.setNumLikes(currentItem.getNumLikes());

        eventsHolder.likeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    eventsHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
                    eventsHolder.setLiked(true);

                    // send to LikeDatabase
                    EventsItem ei = eventsList.get(position);
                    UserItem user = MainActivity.getCurrUser();
                    final String eventID = ei.getEventID();
                    final String userID = user.getId();
                    DatabaseReference likeEventRef = firebaseDatabase.getReference("LikeEvent");
                    LikeOccasionItem likeOccasionItem = new LikeOccasionItem(eventID, userID);
                    likeEventRef.push().setValue(likeOccasionItem);

                    // +1 to the Likes on the eventItem
                    int currLikes = ei.getNumLikes();
                    DatabaseReference mEventRef = firebaseDatabase.getReference("Events");
                    mEventRef.child(eventID).child("numLikes").setValue(currLikes + 1);
                    ei.setNumLikes(currLikes + 1);
                    eventsHolder.setNumLikes(currLikes + 1);

                    // for display only
                    eventsHolder.numLikesView.setText(Integer.toString(currLikes + 1));

                } else {
                    eventsHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                    eventsHolder.setLiked(false);

                    // Delete the entry from LikeDatabse
                    EventsItem ei = eventsList.get(position);
                    UserItem user = MainActivity.getCurrUser();
                    final String eventID = ei.getEventID();
                    final String userID = user.getId();
                    final DatabaseReference likeEventRef = firebaseDatabase.getReference("LikeEvent");
                    likeEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                                if (eventID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
                                    String key = snapshot.getKey();
                                    likeEventRef.child(key).removeValue();
                                    Toast.makeText(eventsContext, "Unliked", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    // -1 to the Likes on the eventItem
                    int currLikes = ei.getNumLikes();
                    DatabaseReference mEventRef = firebaseDatabase.getReference("Events");
                    mEventRef.child(eventID).child("numLikes").setValue(currLikes - 1);
                    ei.setNumLikes(currLikes - 1);
                    eventsHolder.setNumLikes(currLikes - 1);

                    // for display only
                    eventsHolder.numLikesView.setText(Integer.toString(currLikes - 1));

                    MainActivity.getLikeEventIDs().remove(eventID);
                }

            }
        });

    }

    //imageView.setImageResource(R.drawable.ic_favorite_red_24dp);
    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    @Override
    public Filter getFilter() { // for the 'implements Filterable'
        return eventsFilter;
    }


    public void resetAdapter() {
        this.eventsList = eventsListFull;
    }


}
