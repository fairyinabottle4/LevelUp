package com.Jios.LevelUp.ui.jios;

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
import androidx.recyclerview.widget.RecyclerView;

public class JiosAdapter extends RecyclerView.Adapter<JiosAdapter.JiosViewHolder> implements Filterable {
    //ArrayList is passed in from JiosItem.java
    private ArrayList<JiosItem> jiosList;
    private ArrayList<JiosItem> jiosListFull;
    private StorageReference profileStorageRef;
    private DatabaseReference userRef;
    private FirebaseDatabase firebaseDatabase;

    private Context jiosContext;
    private DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);

    private Filter jiosFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<JiosItem> filteredList = new ArrayList<>(); // initially empty list

            if (constraint == null || constraint.length() == 0) { // search input field empty
                filteredList.addAll(jiosListFull); // to show everything
            } else {
                String userSearchInput = constraint.toString().toLowerCase().trim();

                for (JiosItem item : jiosListFull) {
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
            jiosList.clear();
            jiosList.addAll((List) results.values); // data list contains filtered items
            notifyDataSetChanged(); // tell adapter list has changed

        }
    };

    //the ViewHolder holds the content of the card
    public static class JiosViewHolder extends RecyclerView.ViewHolder {
        private String creatorUid;
        private String creatorName;
        private int creatorResidence;
        private String profilePictureUri;
        private String email;
        private long phone;
        private String telegram;

        private String jioID;
        private boolean isChecked;
        private boolean isLiked;
        private int numLikes;

        private ToggleButton addButton;
        private ToggleButton likeButton;
        private ImageView imageView;
        private TextView titleView;
        private TextView descView;
        private TextView dateView;
        private TextView locationView;
        private TextView timeView;
        private TextView eventView;
        private TextView numLikesView;

        /**
         * Constructor for the JiosViewHolder class
         *
         * @param context Context of the fragment
         * @param itemView View of the item to be displayed
         */
        public JiosViewHolder(final Context context, View itemView) {
            super(itemView);
            final Context context1 = context;
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
            dateView = itemView.findViewById(R.id.date);
            locationView = itemView.findViewById(R.id.location);
            timeView = itemView.findViewById(R.id.time);
            eventView = itemView.findViewById(R.id.eventCreator);
            eventView.setOnClickListener(new View.OnClickListener() {
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
                    Intent intent = new Intent(context, JiosPage.class);
                    intent.putExtra("uid", creatorUid);
                    intent.putExtra("creatorName", creatorName);
                    intent.putExtra("title", titleView.getText().toString());
                    intent.putExtra("description", descView.getText().toString());
                    intent.putExtra("date", dateView.getText().toString());
                    intent.putExtra("location", locationView.getText().toString());
                    intent.putExtra("time", timeView.getText().toString());
                    intent.putExtra("position", getAdapterPosition());
                    intent.putExtra("stateChecked", isChecked);
                    intent.putExtra("stateLiked", isLiked);
                    intent.putExtra("numLikes", numLikes);
                    intent.putExtra("jioID", jioID);
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

        public void setJioID(String jioID) {
            this.jioID = jioID;
        }

        public void setLiked(boolean liked) {
            isLiked = liked;
        }

        public void setNumLikes(int numLikes) {
            this.numLikes = numLikes;
        }
    }

    //Constructor for JiosAdapter class. This ArrayList contains the
    //complete list of items that we want to add to the View.

    /**
     * Constructor for the JiosAdapter class
     *
     * @param context Context of the fragment
     * @param jiosList List of items to be added to the View
     */
    public JiosAdapter(Context context, ArrayList<JiosItem> jiosList) {
        jiosContext = context;
        this.jiosList = jiosList;
        jiosListFull = new ArrayList<>(jiosList); // copy of jiosList for SearchView
        profileStorageRef = FirebaseStorage.getInstance()
            .getReference("profile picture uploads");
        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference("Users");
    }

    //inflate the items in a JiosViewHolder
    @NonNull
    @Override
    public JiosAdapter.JiosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.occ_item, parent, false);
        JiosAdapter.JiosViewHolder evh = new JiosAdapter.JiosViewHolder(jiosContext, v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull JiosAdapter.JiosViewHolder holder, final int position) {
        JiosItem currentItem = jiosList.get(position);
        //holder.imageView.setImageResource(currentItem.getProfilePicture());
        final JiosViewHolder jiosViewHolder = holder;
        final String creatorUid = currentItem.getCreatorID();
        jiosViewHolder.setCreatorUid(creatorUid);
        StorageReference profileStorageRefIndiv = profileStorageRef.child(creatorUid);
        profileStorageRefIndiv.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(jiosViewHolder.imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                jiosViewHolder.imageView.setImageResource(R.drawable.fake_user_dp);
            }
        });

        jiosViewHolder.titleView.setText(currentItem.getTitle());
        jiosViewHolder.descView.setText(currentItem.getDescription());
        jiosViewHolder.dateView.setText(df.format(currentItem.getDateInfo()));
        jiosViewHolder.locationView.setText(currentItem.getLocationInfo());
        jiosViewHolder.timeView.setText(currentItem.getTimeInfo());
        jiosViewHolder.numLikesView.setText(Integer.toString(currentItem.getNumLikes()));

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserItem selected = snapshot.getValue(UserItem.class);
                    String id = selected.getId();

                    if (creatorUid.equals(id)) {
                        String name = selected.getName();
                        jiosViewHolder.eventView.setText(name);
                        jiosViewHolder.setCreatorName(name);
                        int res = selected.getResidential();
                        String telegram = selected.getTelegram();
                        String email = selected.getEmail();
                        String dpUri = selected.getProfilePictureUri();
                        long phone = selected.getPhone();
                        jiosViewHolder.setCreatorName(name);
                        jiosViewHolder.setCreatorResidence(res);
                        jiosViewHolder.setTelegram(telegram);
                        jiosViewHolder.setEmail(email);
                        jiosViewHolder.setProfilePictureUri(dpUri);
                        jiosViewHolder.setPhone(phone);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String jioID = currentItem.getJioID();
        jiosViewHolder.setJioID(jioID);
        if (MainActivity.getJioIds().contains(jioID)) {
            jiosViewHolder.addButton.setBackgroundResource(R.drawable.ic_done_black_24dp);
            jiosViewHolder.setChecked(true);
            jiosViewHolder.addButton.setChecked(true);
        } else {
            jiosViewHolder.addButton.setBackgroundResource(R.drawable.ic_add_black_24dp);
            jiosViewHolder.setChecked(false);
            jiosViewHolder.addButton.setChecked(false);
        }

        jiosViewHolder.addButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // change to tick
                    jiosViewHolder.addButton.setBackgroundResource(R.drawable.ic_done_black_24dp);
                    jiosViewHolder.setChecked(true);

                    JiosItem jiosItem = jiosList.get(position);

                    //int index = EventsFragment.getEventsItemListCopy().indexOf(ei);
                    //MylistFragment.setNumberEvents(index);

                    // add to ActivityEvent firebase
                    UserItem user = MainActivity.getCurrUser();
                    String eventID = jiosItem.getJioID();
                    String userID = user.getId();
                    DatabaseReference activityJioRef = firebaseDatabase.getReference("ActivityJio");
                    ActivityOccasionItem activityOccasionItem = new ActivityOccasionItem(eventID, userID);
                    activityJioRef.push().setValue(activityOccasionItem);

                    Toast.makeText(jiosContext, "Jio added to your list!", Toast.LENGTH_SHORT).show();
                } else {
                    // change back to plus
                    jiosViewHolder.addButton.setBackgroundResource(R.drawable.ic_add_black_24dp);
                    jiosViewHolder.setChecked(false);

                    // delete the entry from activity DB
                    JiosItem jiosItem = jiosList.get(position);
                    UserItem user = MainActivity.getCurrUser();
                    final String jioID = jiosItem.getJioID();
                    final String userID = user.getId();
                    final DatabaseReference activityJioRef = firebaseDatabase.getReference("ActivityJio");
                    activityJioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ActivityOccasionItem selected = snapshot.getValue(ActivityOccasionItem.class);
                                if (jioID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
                                    String key = snapshot.getKey();
                                    activityJioRef.child(key).removeValue();
                                    Toast.makeText(jiosContext, "Jio removed from your list",
                                        Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    MainActivity.getJioIds().remove(jioID);
                }
            }
        });

        if (MainActivity.getLikeJioIDs().contains(jioID)) {
            jiosViewHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
            jiosViewHolder.setLiked(true);
            jiosViewHolder.likeButton.setChecked(true);
        } else {
            jiosViewHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
            jiosViewHolder.setLiked(false);
            jiosViewHolder.likeButton.setChecked(false);
        }

        jiosViewHolder.setNumLikes(currentItem.getNumLikes());

        jiosViewHolder.likeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    jiosViewHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
                    jiosViewHolder.setLiked(true);

                    // send to LikeDatabase
                    JiosItem jiosItem = jiosList.get(position);
                    UserItem user = MainActivity.getCurrUser();
                    final String eventID = jiosItem.getJioID();
                    final String userID = user.getId();
                    DatabaseReference likeJioRef = firebaseDatabase.getReference("LikeJio");
                    LikeOccasionItem likeOccasionItem = new LikeOccasionItem(eventID, userID);
                    likeJioRef.push().setValue(likeOccasionItem);

                    // +1 to the Likes on the eventItem
                    int currLikes = jiosItem.getNumLikes();
                    DatabaseReference jioRef = firebaseDatabase.getReference("Jios");
                    jioRef.child(eventID).child("numLikes").setValue(currLikes + 1);
                    jiosItem.setNumLikes(currLikes + 1);
                    jiosViewHolder.setNumLikes(currLikes + 1);

                    // for display only
                    jiosViewHolder.numLikesView.setText(Integer.toString(currLikes + 1));
                } else {
                    jiosViewHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                    jiosViewHolder.setLiked(false);

                    // Delete the entry from LikeDatabse
                    JiosItem jiosItem = jiosList.get(position);
                    UserItem user = MainActivity.getCurrUser();
                    final String eventID = jiosItem.getJioID();
                    final String userID = user.getId();
                    final DatabaseReference likeJioRef = firebaseDatabase.getReference("LikeJio");
                    likeJioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                LikeOccasionItem selected = snapshot.getValue(LikeOccasionItem.class);
                                if (eventID.equals(selected.getOccasionID()) && userID.equals(selected.getUserID())) {
                                    String key = snapshot.getKey();
                                    likeJioRef.child(key).removeValue();
                                    Toast.makeText(jiosContext, "Unliked", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    // -1 to the Likes on the eventItem
                    int currLikes = jiosItem.getNumLikes();
                    DatabaseReference jioRef = firebaseDatabase.getReference("Jios");
                    jioRef.child(eventID).child("numLikes").setValue(currLikes - 1);
                    jiosItem.setNumLikes(currLikes - 1);
                    jiosViewHolder.setNumLikes(currLikes - 1);

                    // for display only
                    jiosViewHolder.numLikesView.setText(Integer.toString(currLikes - 1));

                    MainActivity.getLikeJioIDs().remove(jioID);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return jiosList.size();
    }

    @Override
    public Filter getFilter() { // for the 'implements Filterable'
        return jiosFilter;
    }

    public void resetAdapter() {
        this.jiosList = jiosListFull;
    }
}
