package com.Mylist.LevelUp.ui.mylist;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.MainActivity;
import com.UserItem;
import com.example.LevelUp.ui.mylist.MylistFragment;
import com.example.tryone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class EditUserInfoActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    public static Uri profileImageUri;
    private ImageView editProfileImage;

    private StorageReference mStorageRef;
    // private DatabaseReference mDatabaseRef;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.mylist_edituserinfo);

        super.onCreate(savedInstanceState);

        mStorageRef = FirebaseStorage.getInstance().getReference("profile picture uploads");
        // mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");

        // For Displaying Name and Residence
        TextView display_name = findViewById(R.id.editTextDisplayName);
        TextView residential_name = findViewById(R.id.editTextResidenceName);
        display_name.setText(MainActivity.display_name);
        residential_name.setText(MainActivity.display_residential);

        // For Setting Profile Picture
        final ImageView editProfileImage = findViewById(R.id.edit_profile_picture);
        this.editProfileImage = editProfileImage;
        if (profileImageUri != null) {
            editProfileImage.setImageURI(profileImageUri);
        }
        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        final String fbUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // For displaying profile picture
        mStorageRef = mStorageRef.child(fbUID);
        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(editProfileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                editProfileImage.setImageResource(R.drawable.fake_user_dp);
            }
        });

        // Log Out
        firebaseAuth = FirebaseAuth.getInstance();
        TextView logOut = (TextView) findViewById(R.id.log_out);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(EditUserInfoActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // For picking profile picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) { // means data is the image selected
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                editProfileImage.setImageURI(imageUri);
                profileImageUri = imageUri;

                uploadImageToFireBase();
            }
        }
    }

    private void uploadImageToFireBase() {
        String profilePicNameInStorage = MainActivity.currUser.getId();
        StorageReference fileReference = mStorageRef;
        fileReference.putFile(profileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(EditUserInfoActivity.this, "Profile Picture Changed", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditUserInfoActivity.this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

        MainActivity.currUser.setProfilePictureUri(profileImageUri.toString());
        // send update to database
        String newUri = profileImageUri.toString();
        FirebaseDatabase.getInstance().getReference("Users")
            .child(MainActivity.currUser.getId())
            .child("profilePictureUri")
            .setValue(newUri);

//        UserItem updatedUser = MainActivity.currUser;
//        FirebaseDatabase.getInstance().getReference("Users")
//                .child(MainActivity.currUser.getId())
//                .setValue(updatedUser);

    }

}
