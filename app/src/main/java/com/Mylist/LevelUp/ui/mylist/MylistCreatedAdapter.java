package com.Mylist.LevelUp.ui.mylist;

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

import com.Events.LevelUp.ui.events.EventPage;
import com.Jios.LevelUp.ui.jios.JiosPage;
import com.MainActivity;
import com.UserItem;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MylistCreatedAdapter extends RecyclerView.Adapter<MylistCreatedAdapter.MylistCreatedViewHolder> {
    private ArrayList<Occasion> mMylistList;
    private MylistCreatedAdapter.OnItemClickListener mListener;
    private DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
    private DateFormat df2 = DateFormat.getDateInstance(DateFormat.MEDIUM);

    private Context mContext;

    private StorageReference mProfileStorageRef;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserRef;

    private Occasion item;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(MylistCreatedAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public static class MylistCreatedViewHolder extends RecyclerView.ViewHolder {
        public String creatorName;
        public String creatorUid;
        public String occID;
        public boolean isJio;
        public Date date;

        public Button mEditButton;
        public Button mPeopleButton;
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;
        public TextView mTextView4;
        public TextView mTextView5;
        public TextView mTextView6;

        public MylistCreatedViewHolder(final Context context, View itemView, final MylistCreatedAdapter.OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mEditButton = itemView.findViewById(R.id.image_edit);
            mPeopleButton = itemView.findViewById(R.id.image_people);
            mTextView1 = itemView.findViewById(R.id.title);
            mTextView2 = itemView.findViewById(R.id.event_description);
            mTextView3 = itemView.findViewById(R.id.time);
            mTextView4 = itemView.findViewById(R.id.location);
            mTextView5 = itemView.findViewById(R.id.date);
            mTextView6 = itemView.findViewById(R.id.eventCreator);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, CreatedOccasionPage.class);
//                    if (isJio) {
//                        intent = new Intent(context, JiosPage.class);
//                        intent.putExtra("jioID", occID);
//                        // Toast.makeText(context, "JIO!", Toast.LENGTH_SHORT).show();
//                    }
                    intent.putExtra("uid", creatorUid);
                    intent.putExtra("creatorName", creatorName);
                    intent.putExtra("title", mTextView1.getText().toString());
                    intent.putExtra("description", mTextView2.getText().toString());
                    intent.putExtra("date", mTextView5.getText().toString());
                    intent.putExtra("dateToShow", DateFormat.getDateInstance(DateFormat.MEDIUM).format(date));
                    intent.putExtra("location", mTextView4.getText().toString());
                    intent.putExtra("time", mTextView3.getText().toString());
                    intent.putExtra("position", getAdapterPosition());
                    intent.putExtra("occID", occID);
                    intent.putExtra("isJio", isJio);
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

        public void setOccID(String occID) {
            this.occID = occID;
        }

        public void setIsJio(boolean jio) {
            isJio = jio;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    } // static class ends here

    //Constructor
    public MylistCreatedAdapter(Context context, ArrayList<Occasion> MylistList) {
        mContext = context;
        mMylistList = MylistList;
        mProfileStorageRef = FirebaseStorage.getInstance()
                .getReference("profile picture uploads");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserRef = mFirebaseDatabase.getReference("Users");
    }

    @NonNull
    @Override
    public MylistCreatedAdapter.MylistCreatedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.occ_item_editmode, parent, false);
        MylistCreatedAdapter.MylistCreatedViewHolder evh = new MylistCreatedAdapter.MylistCreatedViewHolder(mContext, v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull MylistCreatedAdapter.MylistCreatedViewHolder holder, final int position) {
        final Occasion currentItem = mMylistList.get(position);
        UserItem user = MainActivity.currUser;
        final String userID = user.getId();
        final String occID = currentItem.getOccasionID();
        final DatabaseReference mActivityJioRef = mFirebaseDatabase.getReference("ActivityJio");
        final DatabaseReference mActivityEventRef = mFirebaseDatabase.getReference("ActivityEvent");

        final MylistCreatedAdapter.MylistCreatedViewHolder holder1 = holder;
        final String creatorUID = currentItem.getCreatorID();
        holder1.setCreatorUid(creatorUID);
        holder1.setOccID(occID);
        holder1.setIsJio(currentItem.isJio());
        holder1.setDate(currentItem.getDateInfo());

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



        holder1.mPeopleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // new Activity
                Intent intent = new Intent(mContext, CreatorViewNames.class);
                String occID = currentItem.getOccasionID();
                boolean isJio = currentItem.isJio();
                intent.putExtra("occID", occID);
                intent.putExtra("isJio", isJio);
                mContext.startActivity(intent);

            }
        });

        holder1.mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open Activity to Edit Info
                // pass title location description date time
                // pass item ID as well
                // reuse the create events page UI

                Intent intent = new Intent(mContext, EditOccasionInfoActivity.class);
                String title = currentItem.getTitle();
                String location = currentItem.getLocationInfo();
                String description = currentItem.getDescription();
                String date = df2.format(currentItem.getDateInfo());
                String time = currentItem.getTimeInfo();
                String occID = currentItem.getOccasionID();
                String creatorID = currentItem.getCreatorID();

                intent.putExtra("title", title);
                intent.putExtra("location", location);
                intent.putExtra("description", description);
                intent.putExtra("date", date);
                intent.putExtra("time", time);
                intent.putExtra("occID", occID);
                intent.putExtra("creatorID", creatorID);

                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mMylistList.size();
    }
}
