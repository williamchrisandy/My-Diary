package com.theclowns.mydiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class DetailActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback
{
    protected static final String KEY_DIARY_ID = "diaryId";
    protected static final String KEY_LOCATION_ID = "locationId";
    protected static final int REQUEST_CODE_PERMISSION_NO_BACKGROUND = 1;
    protected static final int REQUEST_CODE_PERMISSION_WITH_BACKGROUND = 2;

    TextView textViewDiaryDate;
    EditText editTextTitle, editTextDescription;
    RecyclerView recyclerViewLocation;
    LocationAdapter locationAdapter;
    boolean add;
    String locationIdTarget;
    DiaryHeader diaryHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        add = false;

        textViewDiaryDate = findViewById(R.id.text_view_diary_date);
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);

        Intent intent = getIntent();
        int diaryId = intent.getIntExtra(KEY_DIARY_ID, -1);

        DiaryHeaderDatabase diaryHeaderDatabase= new DiaryHeaderDatabase(this);
        diaryHeader = diaryId == -1? diaryHeaderDatabase.getLastDiaryHeader() : diaryHeaderDatabase.getDiaryHeader(diaryId);
        editTextTitle.setText(diaryHeader.getDiaryTitle());
        textViewDiaryDate.setText(diaryHeader.getDiaryDate().equals("No Date") ? "yyyy-mm-dd" : diaryHeader.getDiaryDate());
        editTextDescription.setText(diaryHeader.getDiaryDescription());

        recyclerViewLocation = findViewById(R.id.recycler_view_location);

        locationAdapter = new LocationAdapter(this);
        locationAdapter.setOnItemClickListener(new LocationAdapter.ClickListener()
        {
            @Override
            public void onItemClick(String locationId)
            {
                locationIdTarget = locationId;
                dynamicPermission();
            }

            @Override
            public void onItemDeletedClick(String locationId)
            {
                DiaryDetailDatabase diaryDetailDatabase = new DiaryDetailDatabase(DetailActivity.this);
                diaryDetailDatabase.deleteDiaryDetail(diaryHeader.getDiaryId(), locationId);

                if(!diaryDetailDatabase.isLocationInAnyDiary(locationId))
                {
                    LocationDatabase locationDatabase = new LocationDatabase(DetailActivity.this);
                    locationDatabase.deleteLocation(locationId);
                }
            }
        });

        recyclerViewLocation.setAdapter(locationAdapter);
        recyclerViewLocation.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loadLocationData();
    }

    public void openCalendar(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_date, null);
        builder.setView(dialogView);

        builder.setTitle("Pick Date");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                DatePicker picker = dialogView.findViewById(R.id.dpDOB);

                String year = Integer.toString(picker.getYear());
                String month = Integer.toString(picker.getMonth() + 1);
                String day = Integer.toString(picker.getDayOfMonth());
                String createdDate = year + "-" + (month.length() < 2 ? "0" + month : month) + "-" + (day.length() < 2 ? "0" + day : day);
                textViewDiaryDate.setText(createdDate);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void addLocation(View view)
    {
        add = true;
        dynamicPermission();
    }

    private void loadLocationData()
    {
        DiaryDetailDatabase diaryHeaderDatabase = new DiaryDetailDatabase(this);
        Vector<DiaryDetail> diaryDetailVector = diaryHeaderDatabase.getDiaryDetail(diaryHeader.getDiaryId());

        LocationDatabase locationDatabase = new LocationDatabase(this);

        Vector<Location> locationVector = new Vector<>();
        for(DiaryDetail diaryDetail : diaryDetailVector)
        {
            String locationId = diaryDetail.getLocationId();
            Location location = locationDatabase.getLocation(locationId);
            locationVector.add(location);
        }
        locationAdapter.setLocationVector(locationVector);
    }

    public void updateDiary(View view)
    {
        String diaryTitle = editTextTitle.getText().toString();
        String createdDate = textViewDiaryDate.getText().toString();
        String diaryDescription = editTextDescription.getText().toString();

        if(createdDate.equals("yyyy-mm-dd")) createdDate = "No Date";
        else
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            Date now = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            Date date = new Date(Integer.parseInt(createdDate.substring(0,4)), Integer.parseInt(createdDate.substring(5,7)) - 1, Integer.parseInt(createdDate.substring(8,10)));
            if(date.after(now))
            {
                Toast.makeText(this, "Date cannot be in the future!", Toast.LENGTH_LONG).show();
                return;
            }
        }

        DiaryHeaderDatabase diaryHeaderDatabase = new DiaryHeaderDatabase(this);
        diaryHeaderDatabase.updateDiaryHeader(new DiaryHeader(diaryHeader.getDiaryId(), diaryTitle, createdDate, diaryDescription));

        finish();
    }

    private void dynamicPermission()
    {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
        {
            if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                String[] strings = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(this, strings, REQUEST_CODE_PERMISSION_NO_BACKGROUND);
            }
            else intentToLocationActivity();
        }
        else
        {
            if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED)
            {
                String[] strings =
                        {
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        };
                ActivityCompat.requestPermissions(this, strings, REQUEST_CODE_PERMISSION_WITH_BACKGROUND);
            }
            else intentToLocationActivity();
        }
    }

    private void intentToLocationActivity()
    {
        Intent intent = new Intent(this, LocationActivity.class);
        if(add)
        {
            intent.putExtra(KEY_DIARY_ID, diaryHeader.getDiaryId());
            add = false;
        }
        else intent.putExtra(KEY_LOCATION_ID, locationIdTarget);
        startActivity(intent);
    }

    private boolean validPermission(int[] grantResults)
    {
        boolean valid = true;
        for(int result : grantResults)
        {
            if(result == PackageManager.PERMISSION_DENIED)
            {
                valid = false;
                break;
            }
        }
        return valid;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults)
    {
        if(requestCode == REQUEST_CODE_PERMISSION_NO_BACKGROUND || requestCode == REQUEST_CODE_PERMISSION_WITH_BACKGROUND)
        {
            if(validPermission(grantResults)) intentToLocationActivity();
            else Toast.makeText(this, "Grant the location permission and click again at the button to access this feature.", Toast.LENGTH_LONG).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Your change in diary title, date, and description will be discarded. Continue?");
        builder.setTitle("Discard Change");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                DetailActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}