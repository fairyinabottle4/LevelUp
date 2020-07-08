package com.Mylist.LevelUp.ui.mylist;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.LoginActivity;
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

public class EditUserInfoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private FirebaseAuth firebaseAuth;

    private String name;
    private int residence;

    private EditText editTextName;
    private int finalResidence;
    private Button saveButton;

    public static Uri profileImageUri;
    private ImageView editProfileImage;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    // eventually add halls
    private Spinner spinner;
    private static final String[] residentials = {"Cinnamon", "Tembusu", "CAPT", "RC4"};

    private boolean deleteProfilePicture = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.mylist_edituserinfo);

        super.onCreate(savedInstanceState);

        name = MainActivity.display_name;
        residence = MainActivity.currUser.getResidential();

        final String fbUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference("profile picture uploads").child(fbUID);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");

        // For Displaying Name and Residence
        TextView display_name = findViewById(R.id.editTextDisplayName);
        display_name.setText(name);

        initializeSpinner();

        // For Deleting Profile Picture
        TextView deleteDP = findViewById(R.id.deleteProfilePictureTextView);
        deleteDP.setPaintFlags(deleteDP.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        deleteDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileImage.setImageResource(R.drawable.fake_user_dp);
                deleteProfilePicture = true;
            }
        });

        // For Setting Name and Residence
        saveButton = findViewById(R.id.saveEditedDetailsBtn); // Button
        editTextName = findViewById(R.id.editTextDisplayName); // Text View
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateName();
                updateResidence();
                if (deleteProfilePicture) {
                    deleteProfilePicture();
                }

                finish();
            }
        });

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

        // For displaying profile picture

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
                MainActivity.currUser = null;
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
        // String profilePicNameInStorage = MainActivity.currUser.getId();
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
        mDatabaseRef
            .child(MainActivity.currUser.getId())
            .child("profilePictureUri")
            .setValue(newUri);

//        UserItem updatedUser = MainActivity.currUser;
//        FirebaseDatabase.getInstance().getReference("Users")
//                .child(MainActivity.currUser.getId())
//                .setValue(updatedUser);

    }

    private void initializeSpinner() {
        spinner = findViewById(R.id.editSpinnerResidenceName);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditUserInfoActivity.this,
                android.R.layout.simple_spinner_item, residentials);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(residence - 1);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                // Toast.makeText(this, "Cinnamon", Toast.LENGTH_SHORT).show();
                finalResidence = 1;
                break;
            case 1:
                // Toast.makeText(this, "Tembusu", Toast.LENGTH_SHORT).show();
                finalResidence = 2;
                break;
            case 2:
                // Toast.makeText(this, "CAPT", Toast.LENGTH_SHORT).show();
                finalResidence = 3;
                break;
            case 3:
                // Toast.makeText(this, "RC4", Toast.LENGTH_SHORT).show();
                finalResidence = 4;
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private String finalName() {
        String inputName = editTextName.getText().toString().trim();
        if (name.isEmpty()) {
            editTextName.setError("Please enter your name");
            editTextName.requestFocus();
        }
        return inputName;
    }

    private void updateName() {
        // if the name in the text view != name in main, update then send to DB
        if (!finalName().equals(name)) {
            // update the DB
            String updatedName = finalName();
            MainActivity.display_name = updatedName;
            mDatabaseRef
                    .child(MainActivity.currUser.getId())
                    .child("name")
                    .setValue(updatedName);
        }
    }

    private void updateResidence() {
        if (finalResidence != residence) { // these are ints
            // update the DB
            MainActivity.display_residential = intToRes(finalResidence);
            mDatabaseRef
                    .child(MainActivity.currUser.getId())
                    .child("residential")
                    .setValue(finalResidence);
        }
    }

    public String intToRes(int x) {
        String residence_name = "";
        if (x == 1) {
            residence_name = "Cinammon";
        }
        if (x == 2) {
            residence_name = "Tembusu";
        }
        if (x == 3) {
            residence_name = "CAPT";
        }
        if (x == 4) {
            residence_name = "RC4";
        }
        return residence_name;
    }

    public void deleteProfilePicture() {
        mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        mDatabaseRef
                .child(MainActivity.currUser.getId())
                .child("profilePictureUri")
                .setValue("");

        // onClick
        // editProfileImage.setImageResource(R.drawable.fake_user_dp);
        // set Boolean True
        // if True deleteProfilePic
    }


}
