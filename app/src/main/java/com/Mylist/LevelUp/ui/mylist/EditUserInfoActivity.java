package com.Mylist.LevelUp.ui.mylist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.MainActivity;
import com.example.tryone.R;
import com.google.firebase.auth.FirebaseAuth;

public class EditUserInfoActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    public static String name;
    public static String residence_name;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.mylist_edituserinfo);
        super.onCreate(savedInstanceState);

        TextView display_name = findViewById(R.id.editTextDisplayName);
        TextView residential_name = findViewById(R.id.editTextResidenceName);

        display_name.setText(name);
        residential_name.setText(residence_name);

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
}
