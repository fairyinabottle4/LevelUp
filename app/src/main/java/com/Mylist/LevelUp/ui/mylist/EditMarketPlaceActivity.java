package com.Mylist.LevelUp.ui.mylist;

import com.Mktplace.LevelUp.ui.mktplace.MktplaceItem;
import com.bumptech.glide.Glide;
import com.example.tryone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditMarketPlaceActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button saveBtn;
    private Button changeImageBtn;
    private ToggleButton deleteBtn;
    private String updatedLocationInfo;
    private String updatedTitle;
    private String updatedDescription;

    private String mktplaceID;
    private String creatorUid;
    private String imageurl;

    private ImageView imageView;
    private TextView titleTv;
    private TextView locationTv;
    private TextView descTv;
    private ProgressBar progressBar;

    private StorageReference storageRef;
    private DatabaseReference databaseRef;

    private StorageTask mUploadTask;
    private Uri mImageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.marketplace_edit);

        Intent intent = getIntent();
        imageurl = intent.getStringExtra("imageurl");
        mktplaceID = intent.getStringExtra("mktplaceID");
        final String title = intent.getStringExtra("title");
        final String location = intent.getStringExtra("location");
        final String description = intent.getStringExtra("description");
        creatorUid = intent.getStringExtra("creatorUID");

        final TextView titleTextView = findViewById(R.id.listing_title);
        titleTv = titleTextView;
        final TextView locationTextView = findViewById(R.id.meetup_location);
        locationTv = locationTextView;
        final TextView descriptionTextView = findViewById(R.id.listing_description);
        descTv = descriptionTextView;
        imageView = findViewById(R.id.selected_image);
        progressBar = findViewById(R.id.progress_bar);

        // Setting existing values
        titleTextView.setText(title);
        locationTextView.setText(location);
        descriptionTextView.setText(description);
        Glide.with(imageView.getContext()).load(imageurl).into(imageView);

        saveBtn = findViewById(R.id.save_changes_btn);
        changeImageBtn = findViewById(R.id.photo_selector);
        deleteBtn = findViewById(R.id.delete_btn);
        storageRef = FirebaseStorage.getInstance().getReference("mktplace uploads");
        databaseRef = FirebaseDatabase.getInstance().getReference("mktplace uploads");
        // final DatabaseReference mDatabaseReferenceMktplace = databaseRef;

        changeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

        final Handler handler = new Handler();
        final Runnable myRun = new Runnable() {
            @Override
            public void run() {
                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            MktplaceItem selected = snapshot.getValue(MktplaceItem.class);
                            String selectedMktPlaceID = selected.getMktPlaceID();
                            if (selectedMktPlaceID.equals(mktplaceID)) {
                                String key = snapshot.getKey();
                                databaseRef.child(key).removeValue();
                                MktplaceCreatedFragment.setRefresh(true);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };

        deleteBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Run the runnable
                    handler.postDelayed(myRun, 10000);
                    Toast.makeText(buttonView.getContext(), "Item will be deleted in 10s", Toast.LENGTH_SHORT).show();

                } else {
                    // Cancel runnable
                    handler.removeCallbacks(myRun);
                    Toast.makeText(EditMarketPlaceActivity.this, "Cancelled Delete", Toast.LENGTH_SHORT).show();

                }
            }
        });


        super.onCreate(savedInstanceState);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Glide.with(imageView.getContext()).load(mImageUri).into(imageView);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // if mImageUri is null means image didnt change i can just use back the current imageURL (string)
    private void uploadFile() {
        boolean factors = !locationTv.getText().toString().equals("")
                && !titleTv.getText().toString().equals(""); // && !listingDescription.getText().toString().equals("");

        if (mImageUri == null && factors) {
            MktplaceItem title = new MktplaceItem(0, mktplaceID, creatorUid, titleTv.getText().toString().trim(),
                    imageurl,
                    locationTv.getText().toString().trim(),
                    descTv.getText().toString().trim());
            databaseRef.child(mktplaceID).setValue(title);
            Toast.makeText(EditMarketPlaceActivity.this, "Successfully Changed", Toast.LENGTH_SHORT).show();
            MktplaceCreatedFragment.setRefresh(true);
            finish();

        } else if (factors) {
            StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 5000);
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            MktplaceItem title = new MktplaceItem(0, mktplaceID, creatorUid,
                                titleTv.getText().toString().trim(),
                                    downloadUrl.toString(),
                                    locationTv.getText().toString().trim(),
                                    descTv.getText().toString().trim());
                            databaseRef.child(mktplaceID).setValue(title);
                            Toast.makeText(EditMarketPlaceActivity.this, "Successfully Changed",
                                Toast.LENGTH_SHORT).show();

                            MktplaceCreatedFragment.setRefresh(true);
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditMarketPlaceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()
                                / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "Please check all fields and try again", Toast.LENGTH_SHORT).show();
        }
    }
}
