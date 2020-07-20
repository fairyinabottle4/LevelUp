package com.Mylist.LevelUp.ui.mylist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.Events.LevelUp.ui.events.DatePickerFragment;
import com.Events.LevelUp.ui.events.EventsAdder;
import com.Events.LevelUp.ui.events.EventsItem;
import com.Events.LevelUp.ui.events.TimePickerFragment;
import com.Jios.LevelUp.ui.jios.JiosItem;
import com.example.LevelUp.ui.events.EventsFragment;
import com.example.tryone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;

public class EditOccasionInfoActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {
    private ImageButton editDateBtn;
    private ImageButton editTimeBtn;
    private Button saveBtn;
    private ToggleButton deleteBtn;
    private TextView dateTextView;
    private TextView timeTextView;
    private String occID;
    private String creatorID;
    private FirebaseDatabase mFirebaseDatabase;

    // public EventsItem(String eventID, String creatorID, Date dateInfo, String timeInfo, int hourOfDay, int minute, String locationInfo, String title, String description)

    private String updatedTimeInfo;
    private int updatedHourOfDay;
    private int updatedMinute;
    private String updatedLocationInfo;
    private String updatedTitle;
    private String updatedDescription;

    private boolean validDate;

    private DateFormat df = DateFormat.getDateInstance();

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
        creatorID = intent.getStringExtra("creatorID");

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        final TextView titleTextView = findViewById(R.id.occ_editTitle);
        final TextView locationTextView = findViewById(R.id.occ_editLocation);
        final TextView descriptionTextView = findViewById(R.id.occ_editDescription);
        saveBtn = findViewById(R.id.save_btn);
        deleteBtn = findViewById(R.id.delete_btn);
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

        final DatabaseReference mDatabaseReferenceEvents = mFirebaseDatabase.getReference().child("Events");
        final DatabaseReference mDatabaseReferenceJios = mFirebaseDatabase.getReference().child("Jios");

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // find the thing in event, if cannot, find in jio
                // search thru, if by the end selected = null, do nth

                mDatabaseReferenceEvents.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            EventsItem selected = snapshot.getValue(EventsItem.class);
                            String selectedOccID = selected.getEventID();
                            if (selectedOccID.equals(occID)) {
                                // Checking Valid Input
                                boolean factors = !locationTextView.getText().toString().equals("")
                                        && !titleTextView.getText().toString().equals("")
                                        && !dateTextView.getText().toString().equals("")
                                        && !timeTextView.getText().toString().equals("")
                                        && !dateTextView.getText().toString().equals("No Time Selected")
                                        && !timeTextView.getText().toString().equals("No Date Selected");
                                try {
                                    validDate = df.parse(DateFormat.getDateInstance(DateFormat.MEDIUM).format(Calendar.getInstance().getTime()))
                                            .compareTo(df.parse(dateTextView.getText().toString())) > 0;
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                // Saving User Input
                                updatedLocationInfo = locationTextView.getText().toString().trim();
                                updatedTitle = titleTextView.getText().toString().trim();
                                updatedDescription = descriptionTextView.getText().toString().trim();
                                updatedTimeInfo = timeTextView.getText().toString().trim();

                                // Replacing Item with New Values
                                EventsItem updatedEventsItem = null;
                                try {
                                    updatedEventsItem = new EventsItem(occID, creatorID,
                                            df.parse((String) dateTextView.getText()),
                                            updatedTimeInfo, updatedHourOfDay, updatedMinute,
                                            updatedLocationInfo, updatedTitle, updatedDescription);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                // Toast.makeText(EditOccasionInfoActivity.this, dateTextView.getText(), Toast.LENGTH_SHORT).show();
                                // mDatabaseReferenceEvents.child(occID).setValue(neww);
                                if (validDate) {
                                    Toast.makeText(EditOccasionInfoActivity.this, "Date selected cannot be before current date", Toast.LENGTH_LONG).show();
                                } else if (!factors) {
                                    Toast.makeText(EditOccasionInfoActivity.this, "Please check all fields and try again", Toast.LENGTH_LONG).show();
                                } else if (factors && updatedEventsItem != null) {
                                    mDatabaseReferenceEvents.child(occID).setValue(updatedEventsItem);
                                    Toast.makeText(EditOccasionInfoActivity.this, "Successfully Changed", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                mDatabaseReferenceJios.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            JiosItem selected = snapshot.getValue(JiosItem.class);
                            String selectedOccID = selected.getJioID();
                            if (selectedOccID.equals(occID)) {
                                // Checking Valid Input
                                boolean factors = !locationTextView.getText().toString().equals("")
                                        && !titleTextView.getText().toString().equals("")
                                        && !dateTextView.getText().toString().equals("")
                                        && !timeTextView.getText().toString().equals("")
                                        && !dateTextView.getText().toString().equals("No Time Selected")
                                        && !timeTextView.getText().toString().equals("No Date Selected");
                                try {
                                    validDate = df.parse(DateFormat.getDateInstance(DateFormat.MEDIUM).format(Calendar.getInstance().getTime()))
                                            .compareTo(df.parse(dateTextView.getText().toString())) > 0;
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                // Saving User Input
                                updatedLocationInfo = locationTextView.getText().toString().trim();
                                updatedTitle = titleTextView.getText().toString().trim();
                                updatedDescription = descriptionTextView.getText().toString().trim();
                                updatedTimeInfo = timeTextView.getText().toString().trim();

                                // Replacing Item with New Values
                                JiosItem updatedJiosItem = null;
                                try {
                                    updatedJiosItem = new JiosItem(occID, creatorID,
                                            df.parse((String) dateTextView.getText()),
                                            updatedTimeInfo, updatedHourOfDay, updatedMinute,
                                            updatedLocationInfo, updatedTitle, updatedDescription);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                // Toast.makeText(EditOccasionInfoActivity.this, dateTextView.getText(), Toast.LENGTH_SHORT).show();
                                // mDatabaseReferenceEvents.child(occID).setValue(neww);
                                if (validDate) {
                                    Toast.makeText(EditOccasionInfoActivity.this, "Date selected cannot be before current date", Toast.LENGTH_LONG).show();
                                } else if (!factors) {
                                    Toast.makeText(EditOccasionInfoActivity.this, "Please check all fields and try again", Toast.LENGTH_LONG).show();
                                } else if (factors && updatedJiosItem != null) {
                                    mDatabaseReferenceJios.child(occID).setValue(updatedJiosItem);
                                    Toast.makeText(EditOccasionInfoActivity.this, "Successfully Changed", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        final Handler handler = new Handler();
        final Runnable myRun = new Runnable() {
            @Override
            public void run() {
                mDatabaseReferenceEvents.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            EventsItem selected = snapshot.getValue(EventsItem.class);
                            String selectedOccID = selected.getEventID();
                            if (selectedOccID.equals(occID)) {
                                String key = snapshot.getKey();
                                mDatabaseReferenceEvents.child(key).removeValue();
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                mDatabaseReferenceJios.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            JiosItem selected = snapshot.getValue(JiosItem.class);
                            String selectedOccID = selected.getJioID();
                            if (selectedOccID.equals(occID)) {
                                String key = snapshot.getKey();
                                mDatabaseReferenceJios.child(key).removeValue();
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
                    Toast.makeText(EditOccasionInfoActivity.this, "Cancelled Delete", Toast.LENGTH_SHORT).show();

                }
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
        // Toast.makeText(this, currentDateString, Toast.LENGTH_SHORT).show();
        dateTextView.setText(currentDateString);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String initial = hourOfDay < 10 ? "0" : "";
        String after = minute < 10 ? "0" : "";
        String currentTimeString = initial + hourOfDay + after + minute;
        timeTextView.setText(currentTimeString);

        updatedTimeInfo = currentTimeString;
        this.updatedHourOfDay = hourOfDay;
        this.updatedMinute = minute;
    }
}
