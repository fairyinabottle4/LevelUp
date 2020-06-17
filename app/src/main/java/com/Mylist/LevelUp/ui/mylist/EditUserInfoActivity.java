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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.mylist_edituserinfo);
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        TextView logOut = (TextView) findViewById(R.id.log_out);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finish();
                Intent intent = new Intent(EditUserInfoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
