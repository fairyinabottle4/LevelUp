package com.Mylist.LevelUp.ui.mylist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.Events.LevelUp.ui.events.DatePickerFragment;
import com.Events.LevelUp.ui.events.TimePickerFragment;
import com.example.tryone.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;

public class EditOccasionInfoActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {
    private ImageButton editDateBtn;
    private ImageButton editTimeBtn;
    private Button saveBtn;
    private TextView dateTextView;
    private TextView timeTextView;
    private String occID;
    private FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.occ_edit_page);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String location = intent.getStringExtra("location");
        String description = intent.getStringExtra("description");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        occID = intent.getStringExtra("occID");

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        TextView titleTextView = findViewById(R.id.occ_editTitle);
        TextView locationTextView = findViewById(R.id.occ_editLocation);
        TextView descriptionTextView = findViewById(R.id.occ_editDescription);
        saveBtn = findViewById(R.id.save_btn);
        dateTextView = findViewById(R.id.date_selected);
        timeTextView = findViewById(R.id.time_selected);

        titleTextView.setText(title);
        locationTextView.setText(location);
        descriptionTextView.setText(description);
        dateTextView.setText(date);
        timeTextView.setText(time);

        editDateBtn = findViewById(R.id.change_date_btn);
        editTimeBtn = findViewById(R.id.change_time_btn);

        editDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        editTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mDatabaseReferenceEvents = mFirebaseDatabase.getReference().child("Events");
                DatabaseReference mDatabaseReferenceJios = mFirebaseDatabase.getReference().child("Jios");

            }
        });

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.MEDIUM).format(c.getTime());
        dateTextView.setText(currentDateString);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String initial = hourOfDay < 10 ? "0" : "";
        String after = minute < 10 ? "0" : "";
        String currentTimeString = initial + hourOfDay + after + minute;
        timeTextView.setText(currentTimeString);
//        this.hourOfDay = hourOfDay;
//        this.minute = minute;
    }
}
