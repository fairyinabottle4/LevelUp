package com.Events.LevelUp.ui.events;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.MainActivity;
import com.example.tryone.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;

public class EventsAdder extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;

    private DateFormat df = DateFormat.getDateInstance();
    TextView mDateSelected;
    TextView mTimeSelected;
    EditText mEventTitle;
    Button mDateSelector;
    Button mTimeSelector;
    Button mSaveJio;
    EditText mEventDescription;
    EditText mEventLocation;
    Uri currentUri;
    private int hourOfDay;
    private int minute;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.occasion_adder);
        currentUri = getIntent().getData();

        mEventTitle = findViewById(R.id.event_title);

        mDateSelector = findViewById(R.id.event_date);
        mDateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        mDateSelected = findViewById(R.id.date_selected);

        mTimeSelector = findViewById(R.id.event_time);
        mTimeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        mTimeSelected = findViewById(R.id.time_selected);

        mEventDescription = findViewById(R.id.event_description);
        mEventLocation = findViewById(R.id.location);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("Events");
        mSaveJio = findViewById(R.id.save_jio);
        mSaveJio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventsItem EventsItem = null;
                try {
                    EventsItem = new EventsItem(R.drawable.fui_ic_twitter_bird_white_24dp,
                            df.parse((String) mDateSelected.getText()), (String) mTimeSelected.getText(),
                            hourOfDay, minute, mEventLocation.getText().toString(),
                            mEventTitle.getText().toString(), mEventDescription.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mDatabaseReference.push().setValue(EventsItem);
                Toast.makeText(EventsAdder.this, "Jio saved successfully", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EventsAdder.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.MEDIUM).format(c.getTime());
        mDateSelected.setText(currentDateString);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String initial = hourOfDay < 10 ? "0" : "";
        String after = minute < 10 ? "0" : "";
        String currentTimeString = initial + hourOfDay + after + minute;
        mTimeSelected.setText(currentTimeString);
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }
}
