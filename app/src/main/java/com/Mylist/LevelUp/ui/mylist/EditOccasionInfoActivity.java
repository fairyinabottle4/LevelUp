package com.Mylist.LevelUp.ui.mylist;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

import com.Events.LevelUp.ui.events.DatePickerFragment;
import com.Events.LevelUp.ui.events.EventsItem;
import com.Events.LevelUp.ui.events.TimePickerFragment;
import com.Jios.LevelUp.ui.jios.JiosItem;
import com.example.tryone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class EditOccasionInfoActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {
    private static final String[] categories = {
        "Arts", "Sports", "Talks", "Volunteering", "Food", "Others"};
    private ImageButton editDateBtn;
    private ImageButton editTimeBtn;
    private Button saveBtn;
    private ToggleButton deleteBtn;
    private TextView dateTextView;
    private TextView timeTextView;
    private String occID;
    private String creatorID;
    private Spinner categorySpinner;
    private FirebaseDatabase firebaseDatabase;

    private String updatedTimeInfo;
    private int updatedHourOfDay;
    private int updatedMinute;
    private String updatedLocationInfo;
    private String updatedTitle;
    private String updatedDescription;
    private int updatedCategory;

    private int category;

    private boolean validDate;

    private DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.occ_edit_page);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String location = intent.getStringExtra("location");
        String description = intent.getStringExtra("description");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        category = intent.getIntExtra("category", -1);
        // Toast.makeText(this, Integer.toString(category), Toast.LENGTH_SHORT).show();
        occID = intent.getStringExtra("occID");
        creatorID = intent.getStringExtra("creatorID");

        firebaseDatabase = FirebaseDatabase.getInstance();

        final TextView titleTextView = findViewById(R.id.occ_editTitle);
        final TextView locationTextView = findViewById(R.id.occ_editLocation);
        final TextView descriptionTextView = findViewById(R.id.occ_editDescription);
        saveBtn = findViewById(R.id.save_btn);
        deleteBtn = findViewById(R.id.delete_btn);
        dateTextView = findViewById(R.id.date_selected);
        timeTextView = findViewById(R.id.time_selected);

        initializeOccSpinner();

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

        final DatabaseReference databaseReferenceEvents = firebaseDatabase.getReference().child("Events");
        final DatabaseReference databaseReferenceJios = firebaseDatabase.getReference().child("Jios");

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // find the thing in event, if cannot, find in jio
                // search thru, if by the end selected = null, do nth

                databaseReferenceEvents.addListenerForSingleValueEvent(new ValueEventListener() {
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
                                    validDate = df.parse(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK)
                                        .format(Calendar.getInstance().getTime()))
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
                                    updatedEventsItem = new EventsItem(selected.getNumLikes(), occID, creatorID,
                                            df.parse(dateTextView.getText().toString().trim()),
                                            updatedTimeInfo, updatedHourOfDay, updatedMinute,
                                            updatedLocationInfo, updatedTitle, updatedDescription, updatedCategory);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (validDate) {
                                    Toast.makeText(EditOccasionInfoActivity.this,
                                        "Date selected cannot be before current date", Toast.LENGTH_LONG).show();
                                } else if (!factors) {
                                    Toast.makeText(EditOccasionInfoActivity.this,
                                        "Please check all fields and try again", Toast.LENGTH_LONG).show();
                                } else if (factors && updatedEventsItem != null) {
                                    databaseReferenceEvents.child(occID).setValue(updatedEventsItem);
                                    Toast.makeText(EditOccasionInfoActivity.this, "Successfully Changed",
                                        Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                databaseReferenceJios.addListenerForSingleValueEvent(new ValueEventListener() {
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
                                    validDate = df.parse(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK)
                                        .format(Calendar.getInstance().getTime()))
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
                                    updatedJiosItem = new JiosItem(selected.getNumLikes(), occID, creatorID,
                                            df.parse(dateTextView.getText().toString().trim()),
                                            updatedTimeInfo, updatedHourOfDay, updatedMinute,
                                            updatedLocationInfo, updatedTitle, updatedDescription, updatedCategory);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (validDate) {
                                    Toast.makeText(EditOccasionInfoActivity.this,
                                        "Date selected cannot be before current date", Toast.LENGTH_LONG).show();
                                } else if (!factors) {
                                    Toast.makeText(EditOccasionInfoActivity.this,
                                        "Please check all fields and try again", Toast.LENGTH_LONG).show();
                                } else if (factors && updatedJiosItem != null) {
                                    databaseReferenceJios.child(occID).setValue(updatedJiosItem);
                                    Toast.makeText(EditOccasionInfoActivity.this,
                                        "Successfully Changed", Toast.LENGTH_LONG).show();
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
                databaseReferenceEvents.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            EventsItem selected = snapshot.getValue(EventsItem.class);
                            String selectedOccID = selected.getEventID();
                            if (selectedOccID.equals(occID)) {
                                String key = snapshot.getKey();
                                databaseReferenceEvents.child(key).removeValue();
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                databaseReferenceJios.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            JiosItem selected = snapshot.getValue(JiosItem.class);
                            String selectedOccID = selected.getJioID();
                            if (selectedOccID.equals(occID)) {
                                String key = snapshot.getKey();
                                databaseReferenceJios.child(key).removeValue();
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
        String currentDateString = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK).format(c.getTime());
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

    private void initializeOccSpinner() {
        categorySpinner = findViewById(R.id.occ_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditOccasionInfoActivity.this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setSelection(category);
        categorySpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        updatedCategory = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
