package com.Mylist.LevelUp.ui.mylist;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.MainActivity;
import com.UserItem;
import com.UserProfile;
import com.example.LevelUp.ui.Occasion;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MylistCreatedAdapter extends RecyclerView.Adapter<MylistCreatedAdapter.MylistCreatedViewHolder> {
    private ArrayList<Occasion> mylistList;
    private MylistCreatedAdapter.OnItemClickListener mylistListener;
    private DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
    private DateFormat df2 = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);

    private Context fragContext;

    private StorageReference profileStorageRef;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;

    private Occasion item;

    /**
     * Constructor for the MylistCreatedAdapter class
     *
     * @param context Context of the fragment it is in
     * @param mylistList List of all the items to be displayed
     */
    public MylistCreatedAdapter(Context context, ArrayList<Occasion> mylistList) {
        fragContext = context;
        this.mylistList = mylistList;
        profileStorageRef = FirebaseStorage.getInstance()
            .getReference("profile picture uploads");
        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference("Users");
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(MylistCreatedAdapter.OnItemClickListener listener) {
        mylistListener = listener;
    }

    public static class MylistCreatedViewHolder extends RecyclerView.ViewHolder {
        private String creatorName;
        private String creatorUid;
        private int creatorResidence;
        private String profilePictureUri;
        private String email;
        private long phone;
        private String telegram;
        private int category;

        private String occID;
        private boolean isJio;
        private Date date;
        private int numLikes;

        private Button editButton;
        private Button peopleButton;
        private Button peopleLikedButton;
        private TextView numLikesView;
        private ImageView imageView;
        private TextView titleView;
        private TextView descView;
        private TextView timeView;
        private TextView locationView;
        private TextView dateView;
        private TextView creatorView;

        /**
         * Constructor for the MylistCreatedViewHolder class
         *
         * @param context Context of the fragment it is in
         * @param itemView View of the item to be displayed
         * @param listener Listener which activates upon being clicked
         */
        public MylistCreatedViewHolder(final Context context, View itemView,
                                       final MylistCreatedAdapter.OnItemClickListener listener) {
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
            editButton = itemView.findViewById(R.id.image_edit);
            peopleButton = itemView.findViewById(R.id.image_people);
            peopleLikedButton = itemView.findViewById(R.id.image_people_liked);
            numLikesView = itemView.findViewById(R.id.numlikes_textview);
            titleView = itemView.findViewById(R.id.title);
            descView = itemView.findViewById(R.id.event_description);
            timeView = itemView.findViewById(R.id.time);
            locationView = itemView.findViewById(R.id.location);
            dateView = itemView.findViewById(R.id.date);
            creatorView = itemView.findViewById(R.id.eventCreator);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, CreatedOccasionPage.class);

                    intent.putExtra("uid", creatorUid);
                    intent.putExtra("creatorName", creatorName);
                    intent.putExtra("title", titleView.getText().toString());
                    intent.putExtra("description", descView.getText().toString());
                    intent.putExtra("date", dateView.getText().toString());
                    intent.putExtra("dateToShow", DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK)
                        .format(date));
                    intent.putExtra("location", locationView.getText().toString());
                    intent.putExtra("time", timeView.getText().toString());
                    intent.putExtra("position", getAdapterPosition());
                    intent.putExtra("numLikes", numLikes);
                    intent.putExtra("occID", occID);
                    intent.putExtra("isJio", isJio);
                    intent.putExtra("residence", creatorResidence);
                    intent.putExtra("dpUri", profilePictureUri);
                    intent.putExtra("telegram", telegram);
                    intent.putExtra("email", email);
                    intent.putExtra("phone", phone);
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

        public void setDate(Date date) {
            this.date = date;
        }

        public void setNumLikes(int numLikes) {
            this.numLikes = numLikes;
        }

        public void setCategory(int category) {
            this.category = category;
        }
    } // static class ends here

    @NonNull
    @Override
    public MylistCreatedAdapter.MylistCreatedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.occ_item_editmode, parent, false);
        MylistCreatedAdapter.MylistCreatedViewHolder evh = new MylistCreatedAdapter
            .MylistCreatedViewHolder(fragContext, v, mylistListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull MylistCreatedAdapter.MylistCreatedViewHolder holder, final int position) {
        final Occasion currentItem = mylistList.get(position);
        UserItem user = MainActivity.getCurrUser();
        final String userID = user.getId();
        final String occID = currentItem.getOccasionID();
        final DatabaseReference activityJioRef = firebaseDatabase.getReference("ActivityJio");
        final DatabaseReference activityEventRef = firebaseDatabase.getReference("ActivityEvent");

        final MylistCreatedAdapter.MylistCreatedViewHolder viewHolder = holder;
        final String creatorUid = currentItem.getCreatorID();
        viewHolder.setCreatorUid(creatorUid);
        viewHolder.setOccID(occID);
        viewHolder.setIsJio(currentItem.isJio());
        viewHolder.setDate(currentItem.getDateInfo());

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

        int numLikes = currentItem.getNumLikes();
        viewHolder.numLikesView.setText(Integer.toString(numLikes));
        viewHolder.setNumLikes(numLikes);

        viewHolder.setCategory(currentItem.getCategory());

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



        viewHolder.peopleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // new Activity
                Intent intent = new Intent(fragContext, CreatorViewNames.class);
                String occID = currentItem.getOccasionID();
                boolean isJio = currentItem.isJio();
                intent.putExtra("occID", occID);
                intent.putExtra("isJio", isJio);
                fragContext.startActivity(intent);

            }
        });

        viewHolder.peopleLikedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // new Activity
                Intent intent = new Intent(fragContext, CreatorViewLikeNames.class);
                String occID = currentItem.getOccasionID();
                boolean isJio = currentItem.isJio();
                intent.putExtra("occID", occID);
                intent.putExtra("isJio", isJio);
                fragContext.startActivity(intent);

            }
        });

        viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open Activity to Edit Info
                // pass title location description date time
                // pass item ID as well
                // reuse the create events page UI

                Intent intent = new Intent(fragContext, EditOccasionInfoActivity.class);
                String title = currentItem.getTitle();
                String location = currentItem.getLocationInfo();
                String description = currentItem.getDescription();
                String date = df2.format(currentItem.getDateInfo());
                String time = currentItem.getTimeInfo();
                String occID = currentItem.getOccasionID();
                String creatorID = currentItem.getCreatorID();
                int category = currentItem.getCategory();

                intent.putExtra("title", title);
                intent.putExtra("location", location);
                intent.putExtra("description", description);
                intent.putExtra("date", date);
                intent.putExtra("time", time);
                intent.putExtra("occID", occID);
                intent.putExtra("creatorID", creatorID);
                intent.putExtra("category", category);

                fragContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mylistList.size();
    }
}
