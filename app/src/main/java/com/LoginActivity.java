package com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tryone.R;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    // eventually add halls
    private Spinner spinner;
    private static final String[] residentials = {"Cinnamon", "Tembusu", "CAPT", "RC4", "Select Residence"};
    private final int listsize = residentials.length - 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        initializeSpinner();

        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        initializeFirebase();

        editTextName = findViewById(R.id.editTextLoginName);

        findViewById(R.id.Register_Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();

                sendToDatabase();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

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

    private void registerUser() {
         name = editTextName.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError("Please enter your name");
            editTextName.requestFocus();
        }
    }

    private void sendToDatabase() {
        // new user(id, email, name) (eventually rc and profile pic)
        String userID = fbUser.getUid();
        String email = fbUser.getEmail();
        UserItem userItem = new UserItem(userID, name, email, residence);
        mReferenceUsers.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .setValue(userItem);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please enter your details", Toast.LENGTH_SHORT).show();
    }



}