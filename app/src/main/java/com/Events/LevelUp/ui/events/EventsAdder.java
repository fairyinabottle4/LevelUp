package com.Events.LevelUp.ui.events;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.MainActivity;
import com.example.LevelUp.ui.events.EventsFragment;
import com.example.tryone.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;


public class EventsAdder extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {
    private static final String[] categories = {
        "Arts", "Sports", "Talks", "Volunteering", "Food", "Others"};

    private FirebaseDatabase database;
    private DatabaseReference databaseRef;

    private DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
    private int selection;
    private TextView dateSelected;
    private TextView timeSelected;
    private EditText eventTitle;
    private Button dateSelector;
    private Button timeSelector;
    private Button saveJio;
    private EditText eventDescription;
    private EditText eventLocation;
    private Uri currentUri;
    private Spinner eventSpinner;

    private int hourOfDay;
    private int minute;
    private boolean validDate;
    private boolean validTime = false;
    private boolean dateIsSame;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_adder);
        currentUri = getIntent().getData();

        eventTitle = findViewById(R.id.event_title);

        dateSelector = findViewById(R.id.event_date);
        dateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        dateSelected = findViewById(R.id.date_selected);

        timeSelector = findViewById(R.id.event_time);
        timeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        timeSelected = findViewById(R.id.time_selected);

        eventDescription = findViewById(R.id.event_description);
        eventLocation = findViewById(R.id.location);


        initializeSpinner();

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference().child("Events");
        saveJio = findViewById(R.id.save_jio);
        saveJio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventsItem eventsItem = null;
                String key = databaseRef.push().getKey();
                String eventCreatorUid = MainActivity.getCurrUser().getId();
                try {
                    eventsItem = new EventsItem(0, key, eventCreatorUid,
                            df.parse((String) dateSelected.getText()), (String) timeSelected.getText(),
                            hourOfDay, minute, eventLocation.getText().toString(),
                            eventTitle.getText().toString(), eventDescription.getText().toString(),
                            selection);
                    // Toast.makeText(EventsAdder.this, key, Toast.LENGTH_SHORT).show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
                String str = sdf.format(Calendar.getInstance().getTime());
                int intCurrentTime = Integer.parseInt(str);
                try {
                    validDate = df.parse(DateFormat.getDateInstance(DateFormat.MEDIUM,
                        Locale.UK).format(Calendar.getInstance().getTime()))
                            .compareTo(df.parse(dateSelected.getText().toString())) > 0;
                    dateIsSame = df.parse(DateFormat.getDateInstance(DateFormat.MEDIUM,
                        Locale.UK).format(Calendar.getInstance().getTime()))
                            .compareTo(df.parse(dateSelected.getText().toString())) == 0;
                    if (!timeSelected.getText().toString().equals("No Time Selected") && dateIsSame) {
                        validTime = intCurrentTime > Integer.parseInt(timeSelected.getText().toString());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                boolean factors = !eventLocation.getText().toString().equals("")
                        && !eventTitle.getText().toString().equals("")
                        && !eventDescription.getText().toString().equals("")
                        && !timeSelected.getText().toString().equals("No Time Selected")
                        && !dateSelected.getText().toString().equals("No Date Selected")
                        //validDate must be an incorrect date
                        && !validDate
                        //validTime must be an incorrect time
                        && !validTime;
                if (!factors) {
                    Toast.makeText(EventsAdder.this, "Please check all fields and try again",
                        Toast.LENGTH_LONG).show();
                } else if (factors) {
                    databaseRef.child(key).setValue(eventsItem);
                    Toast.makeText(EventsAdder.this, "Event saved successfully",
                        Toast.LENGTH_LONG).show();
                    EventsFragment.setRefresh(true);
                    onBackPressed();
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK).format(c.getTime());
        dateSelected.setText(currentDateString);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String initial = hourOfDay < 10 ? "0" : "";
        String after = minute < 10 ? "0" : "";
        String currentTimeString = initial + hourOfDay + after + minute;
        timeSelected.setText(currentTimeString);
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }

    private void initializeSpinner() {
        eventSpinner = findViewById(R.id.event_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EventsAdder.this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventSpinner.setAdapter(adapter);
        eventSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selection = 0;
                        break;
                    case 1:
                        selection = 1;
                        break;
                    case 2:
                        selection = 2;
                        break;
                    case 3:
                        selection = 3;
                        break;
                    case 4:
                        selection = 4;
                        break;
                    case 5:
                        selection = 5;
                        break;
                    default:
                        selection = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
