package com.example.helloworld.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helloworld.DataBase.AppDatabase;
import com.example.helloworld.DataBase.AppExecutors;
import com.example.helloworld.Model.Note;
import com.example.helloworld.R;
import com.google.android.gms.maps.model.LatLng;

public class AddNoteActivity extends AppCompatActivity {

    private static final int MY_CAMERA_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST = 1888;
    public static LatLng latLng = new LatLng(0, 0);
    private Button btLocation, btSave;
    private EditText etTitle, etTime, etDescription;
    private AppDatabase mDb;


    @Override
    protected void onResume() {
        super.onResume();
        if (latLng.latitude != 0 && latLng.longitude != 0) {
            btLocation.setText("" + latLng.latitude + ", " + latLng.longitude);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        getSupportActionBar().setTitle("Add Note");
        mDb = AppDatabase.getInstance(getApplicationContext());
        findViewById(R.id.ivCamera).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("sasd", "SDasd");
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });
        btLocation = findViewById(R.id.btLocation);
        btSave = findViewById(R.id.btSave);
        etTitle = findViewById(R.id.etTitle);
        etTime = findViewById(R.id.etTime);
        etDescription = findViewById(R.id.etDescription);
        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddNoteActivity.this, MapsActivity.class));
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = etTitle.getText().toString();
                final String time = etTime.getText().toString();
                final String description = etDescription.getText().toString();
                if (!title.equals("") && !time.equals("") && !description.equals("")) {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            Note note = new Note(title, latLng.latitude, latLng.longitude, time, description);
                            mDb.noteDao().insertNote(note);
                            latLng = new LatLng(0, 0);
                            finish();
                        }
                    });
                } else {
                    Toast.makeText(AddNoteActivity.this, "All Fields required!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
        }
    }

}
