package com;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.Mylist.LevelUp.ui.mylist.EditUserInfoActivity;
import com.example.tryone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // For Login
    private EditText editTextName;
    private FirebaseUser fbUser;
    private String name;
    private int residence;
    // private static FirebaseAuth mAuth;
    // private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReferenceUsers;

    private ImageButton setDP;
    private Uri profileImageUri;
    private boolean allowed;

    // eventually add halls
    private Spinner spinner;
    private static final String[] residentials = {"Cinnamon", "Tembusu", "CAPT", "RC4", "Select Residence"};
    private final int listsize = residentials.length - 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        setDP = findViewById(R.id.setProfilePictureLogin);
        setDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);

            }
        });

        initializeSpinner();

        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        initializeFirebase();

        editTextName = findViewById(R.id.editTextLoginName);

        findViewById(R.id.Register_Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (residence < 1 || residence > 4) {
                    allowed = false;
                } else {
                    allowed = true;
                }

                registerName();

                if (allowed) {
                    sendToDatabase();
                    // Toast.makeText(LoginActivity.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(LoginActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter your details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) { // means data is the image selected
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                setDP.setImageURI(imageUri);
                profileImageUri = imageUri;

                uploadImageToFireBase();
            }
        }
    }

    private void uploadImageToFireBase() {
        String currUserId = MainActivity.currUser.getId();
        StorageReference fileReference = FirebaseStorage.getInstance().getReference("profile picture uploads").child(currUserId);
        fileReference.putFile(profileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(LoginActivity.this, "Profile Picture Changed", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
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

    private void initializeSpinner() {
        spinner = findViewById(R.id.residential_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoginActivity.this,
                android.R.layout.simple_spinner_item, residentials) {
            @Override
            public int getCount() {
                return listsize;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(listsize);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                Toast.makeText(this, "Cinnamon", Toast.LENGTH_SHORT).show();
                residence = 1;
                break;
            case 1:
                Toast.makeText(this, "Tembusu", Toast.LENGTH_SHORT).show();
                residence = 2;
                break;
            case 2:
                Toast.makeText(this, "CAPT", Toast.LENGTH_SHORT).show();
                residence = 3;
                break;
            case 3:
                Toast.makeText(this, "RC4", Toast.LENGTH_SHORT).show();
                residence = 4;
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void initializeFirebase() {
//        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReferenceUsers = mFirebaseDatabase.getReference("Users");
//        mReferenceJios = mFirebaseDatabase.getReference().child("Jios");
//        mReferenceEvents = mFirebaseDatabase.getReference().child("Events");
//        mReferenceMktplace = mFirebaseDatabase.getReference().child("mktplace uploads");
    }

    private void registerName() {
         name = editTextName.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError("Please enter your name");
            editTextName.requestFocus();
            allowed = false;
        }
    }

    private void sendToDatabase() {
        // new user(id, email, name) (eventually rc and profile pic)
        String userID = fbUser.getUid();
        String email = fbUser.getEmail();
        String imageURI = "";

        // must add the set dp
        if (profileImageUri != null) {
            imageURI = profileImageUri.toString();
        }
        UserItem userItem = new UserItem(userID, imageURI, name, email, residence);
        mReferenceUsers.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .setValue(userItem);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please enter your details", Toast.LENGTH_SHORT).show();
    }



}