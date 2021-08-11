package com.levelup.ui.dashboard;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.levelup.R;
import com.levelup.activity.MainActivity;
import com.levelup.occasion.Occasion;
import com.levelup.ui.events.EventPage;
import com.levelup.user.UserItem;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder> {

    private ArrayList<Occasion> occasionList;
    private StorageReference profileStorageRef;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;
    private DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
    private FragmentActivity dashContext;

    public static class DashboardViewHolder extends RecyclerView.ViewHolder {
        private String creatorUid;
        private String creatorName;
        private int creatorResidence;
        private String profilePictureUri;
        private String email;
        private long phone;
        private String telegram;
        private String description;

        private String eventID;
        private boolean isChecked;
        private boolean isLiked;
        private int numLikes;

        private ImageView imageView;
        private TextView titleView;
        private TextView dateView;
        private TextView locationView;
        private TextView timeView;

        /**
         * Constructor for the DashboardViewHolder class
         *
         * @param context Context of the fragment that the dashboard is placed in
         * @param itemView View of the item that will be displayed
         */
        public DashboardViewHolder (final Context context, View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.category_imageView);
            titleView = itemView.findViewById(R.id.title);
            dateView = itemView.findViewById(R.id.date);
            locationView = itemView.findViewById(R.id.location);
            timeView = itemView.findViewById(R.id.time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, EventPage.class);
                    intent.putExtra("uid", creatorUid);
                    intent.putExtra("creatorName", creatorName);
                    intent.putExtra("title", titleView.getText().toString());
                    intent.putExtra("description", description);
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
    }

    /**
     * Constructor for the DashboardAdapter class
     *
     * @param context Context of the fragment the DashboardAdapter is placed in
     * @param occasionList List of items to be displayed in the adapter
     */
    public DashboardAdapter(FragmentActivity context, ArrayList<Occasion> occasionList) {
        this.occasionList = occasionList;
        profileStorageRef = FirebaseStorage.getInstance()
                .getReference("profile picture uploads");
        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference("Users");
        this.dashContext = context;
    }


    @NonNull
    @Override
    public DashboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.occ_item_dashboard, parent, false);
        DashboardAdapter.DashboardViewHolder dvh = new DashboardAdapter.DashboardViewHolder(dashContext, v);
        return dvh;
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardViewHolder holder, final int position) {
        final Occasion currentItem = occasionList.get(position);
        final DashboardAdapter.DashboardViewHolder dashHolder = holder;
        final String creatorUid = currentItem.getCreatorID();
        dashHolder.setCreatorUid(creatorUid);

        int category = currentItem.getCategory();

        if (category == 0) { // arts
            dashHolder.imageView.setImageResource(R.drawable.arts);
        }
        if (category == 1) { // sports
            dashHolder.imageView.setImageResource(R.drawable.sports);
        }
        if (category == 2) { // talks
            dashHolder.imageView.setImageResource(R.drawable.talks);
        }
        if (category == 3) { // volunteering
            dashHolder.imageView.setImageResource(R.drawable.volunteering);
        }
        if (category == 4) { // food
            dashHolder.imageView.setImageResource(R.drawable.food);
        }
        if (category == 5) { // food
            dashHolder.imageView.setImageResource(R.drawable.others);
        }

        dashHolder.titleView.setText(currentItem.getTitle());
        dashHolder.description = currentItem.getDescription();
        dashHolder.locationView.setText(currentItem.getLocationInfo());

        String date = df.format(currentItem.getDateInfo());
        dashHolder.dateView.setText(date);

        String time = currentItem.getTimeInfo();
        dashHolder.timeView.setText(time);

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
                        dashHolder.setCreatorName(name);
                        dashHolder.setCreatorResidence(res);
                        dashHolder.setTelegram(telegram);
                        dashHolder.setEmail(email);
                        dashHolder.setProfilePictureUri(dpUri);
                        dashHolder.setPhone(phone);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String eventID = currentItem.getOccasionID();
        dashHolder.setEventID(eventID);

        // set stateChecked and stateLiked to pass into the intent
        if (currentItem.isJio()) {
            if (MainActivity.mJioIDs.contains(currentItem.getOccasionID())) {
                dashHolder.setChecked(true);
            }
            if (MainActivity.mLikeJioIDs.contains(currentItem.getOccasionID())) {
                dashHolder.setLiked(true);
            }
        } else {
            if (MainActivity.mEventIDs.contains(currentItem.getOccasionID())) {
                dashHolder.setChecked(true);
            }
            if (MainActivity.mLikeEventIDs.contains(currentItem.getOccasionID())) {
                dashHolder.setLiked(true);
            }
        }

        dashHolder.setNumLikes(currentItem.getNumLikes());

    }

    @Override
    public int getItemCount() {
        return occasionList.size();
    }


}

