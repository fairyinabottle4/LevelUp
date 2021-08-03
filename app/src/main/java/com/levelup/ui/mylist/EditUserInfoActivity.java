package com.levelup.ui.mylist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.levelup.R;
import com.levelup.activity.MainActivity;
import com.levelup.fragment.mylist.MylistFragment;

public class EditUserInfoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private FirebaseAuth firebaseAuth;
    private static final int PICK_IMAGE_REQUEST = 1;

    private String name;
    private int residence;
    private String telegram;
    private String email;
    private long phone;

    private EditText editTextName;
    private int finalResidence;
    private Button saveButton;

    private EditText editTelegramHandle;
    private EditText editEmailAddress;
    private EditText editPhoneNumber;

    public static Uri profileImageUri;
    private ImageView editProfileImage;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private Spinner spinner;
    private static final String[] residentials = {"I don't stay on campus",
            "Cinnamon", "Tembusu", "CAPT", "RC4", "RVRC",
            "Eusoff", "Kent Ridge", "King Edward VII", "Raffles",
            "Sheares", "Temasek", "PGP House", "PGP Residences", "UTown Residence",
            "Select Residence"};

    private boolean deleteProfilePicture = false;
    private boolean changes = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.mylist_edituserinfo);

        super.onCreate(savedInstanceState);

        name = MainActivity.display_name;
        residence = MainActivity.currUser.getResidential();
        telegram = MainActivity.display_telegram;
        phone = MainActivity.display_phone;


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

        //For updating Contact Information
        editTelegramHandle = findViewById(R.id.edit_telegram_handle);
        editTelegramHandle.setText(MainActivity.display_telegram);
        editPhoneNumber = findViewById(R.id.edit_phone_number);
        editPhoneNumber.setText(Long.toString(MainActivity.display_phone));



        // For Setting Name and Residence
        saveButton = findViewById(R.id.saveEditedDetailsBtn); // Button
        editTextName = findViewById(R.id.editTextDisplayName); // Text View
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateName();
                updateResidence();
                updateTelegramHandle();
                updatePhoneNumber();
                boolean pass = true;
                if (deleteProfilePicture) {
                    deleteProfilePicture();
                }
                if (name.isEmpty()) {
                    editTextName.setError("Please enter your name");
                    editTextName.requestFocus();
                    pass = false;
                }

                if (pass) {
                    MylistFragment.setRefreshUserDetails(true);
                    finish();
                }
            }
        });

        // For Setting Profile Picture
        final ImageView editProfileImage = findViewById(R.id.edit_profile_picture);
        final ImageView editProfileImagePencil = findViewById(R.id.edit_profile_picture_pencil);
        this.editProfileImage = editProfileImage;
        if (profileImageUri != null) {
            editProfileImage.setImageURI(profileImageUri);
        }
        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                openGalleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(openGalleryIntent, PICK_IMAGE_REQUEST);
            }
        });

        editProfileImagePencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                openGalleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(openGalleryIntent, PICK_IMAGE_REQUEST);
            }
        });

        // For displaying profile picture

        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(editProfileImage);
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
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AlertDialog.Builder builder = new AlertDialog.Builder(EditUserInfoActivity.this);
        builder.setMessage("Set profile picture?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (requestCode == PICK_IMAGE_REQUEST) { // means data is the image selected
                            if (resultCode == Activity.RESULT_OK) {
                                deleteProfilePicture = false;
                                Uri imageUri = data.getData();
                                editProfileImage.setImageURI(imageUri);
                                profileImageUri = imageUri;
                                uploadImageToFireBase();
                            }
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
        spinner.setSelection(residence);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                finalResidence = 0;
                break;
            case 1:
                // Toast.makeText(this, "Cinnamon", Toast.LENGTH_SHORT).show();
                finalResidence = 1;
                break;
            case 2:
                // Toast.makeText(this, "Tembusu", Toast.LENGTH_SHORT).show();
                finalResidence = 2;
                break;
            case 3:
                // Toast.makeText(this, "CAPT", Toast.LENGTH_SHORT).show();
                finalResidence = 3;
                break;
            case 4:
                // Toast.makeText(this, "RC4", Toast.LENGTH_SHORT).show();
                finalResidence = 4;
                break;
            case 5:
                finalResidence = 5;
                break;
            case 6:
                finalResidence = 6;
                break;
            case 7:
                finalResidence = 7;
                break;
            case 8:
                finalResidence = 8;
                break;
            case 9:
                finalResidence = 9;
                break;
            case 10:
                finalResidence = 10;
                break;
            case 11:
                finalResidence = 11;
                break;
            case 12:
                finalResidence = 12;
                break;
            case 13:
                finalResidence = 13;
                break;
            case 14:
                finalResidence = 14;
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void updateName() {
        String inputName = editTextName.getText().toString().trim();
        // if the name in the text view != name in main, update then send to DB
        if (!inputName.equals(name)) {
            // update the DB
            String updatedName = inputName;
            name = inputName;
            MainActivity.display_name = updatedName;
            mDatabaseRef
                    .child(MainActivity.currUser.getId())
                    .child("name")
                    .setValue(updatedName);
            changes = true;
        }
    }

    //incomplete
    private void updateTelegramHandle() {
        String inputHandle = editTelegramHandle.getText().toString().trim();
        if (!inputHandle.equals(telegram)) {
            MainActivity.display_telegram = inputHandle;
            mDatabaseRef
                    .child(MainActivity.currUser.getId())
                    .child("TelegramHandle")
                    .setValue(inputHandle);
            changes = true;
        }
    }

//    private void updateEmailAddress() {
//        String inputAddress = editEmailAddress.getText().toString().trim();
//        if (!inputAddress.equals(email)) {
//            MainActivity.display_email = inputAddress;
//            mDatabaseRef
//                    .child(MainActivity.currUser.getId())
//                    .child("email")
//                    .setValue(inputAddress);
//            changes = true;
//        }
//    }

    private void updatePhoneNumber() {
        String inputNumberString = editPhoneNumber.getText().toString().trim();
        long inputNumber = Long.parseLong(inputNumberString);
        MainActivity.display_phone = inputNumber;
        if (inputNumber != phone) {
            mDatabaseRef
                    .child(MainActivity.currUser.getId())
                    .child("PhoneNumber")
                    .setValue(inputNumber);
            changes = true;
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
            changes = true;
        }
    }

    public String intToRes(int x) {
        String residence_name = "";
        if (x == 0) {
            residence_name = "Off Campus";
        }
        if (x == 1) {
            residence_name = "Cinnamon";
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
        if (x == 5) {
            residence_name = "RVRC";
        }
        if (x == 6) {
            residence_name = "Eusoff";
        }
        if (x == 7) {
            residence_name = "Kent Ridge";
        }
        if (x == 8) {
            residence_name = "King Edward VII";
        }
        if (x == 9) {
            residence_name = "Raffles";
        }
        if (x == 10) {
            residence_name = "Sheares";
        }
        if (x == 11) {
            residence_name = "Temasek";
        }
        if (x == 12) {
            residence_name = "PGP House";
        }
        if (x == 13) {
            residence_name = "PGP Residences";
        }
        if (x == 14) {
            residence_name = "UTown Residence";
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
