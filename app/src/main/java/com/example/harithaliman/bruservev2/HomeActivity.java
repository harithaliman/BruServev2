package com.example.harithaliman.bruservev2;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harithaliman.bruservev2.CategorySubmissionActivities.DrainageSubmissionActivity;
import com.example.harithaliman.bruservev2.CategorySubmissionActivities.PotholeSubmissionActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity{


    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;




    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private String userID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageButton imageButtonPothole = findViewById(R.id.imageButtonPothole);

        ImageButton imageButtonDrainage = findViewById(R.id.imageButtonDrainage);


        ImageButton imageButtonHome = findViewById(R.id.imageButtonHome);

        ImageButton imageButtonComplaints = findViewById(R.id.imageButtonComplaints);

        ImageButton imageButtonMap = findViewById(R.id.imageButtonMap);

        ImageButton imageButtonProfile = findViewById(R.id.imageButtonProfile);

        ImageButton imageButtonSettings = findViewById(R.id.imageButtonSettings);

        TextView textViewWelcome = (TextView)findViewById(R.id.textViewWelcome);

        if(isServicesOK()) {
            //init();
        }

        imageButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, HomeActivity.class));
            }
        });

//        imageButtonComplaints.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(HomeActivity.this, ComplaintsHistory.class));
//            }
//        });

        imageButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, MapsActivity.class));
            }
        });

        imageButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            }
        });

//        imageButtonSettings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
//            }
//        });

        imageButtonPothole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, PotholeSubmissionActivity.class));
            }
        });

        imageButtonDrainage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, DrainageSubmissionActivity.class));
            }
        });



    }




    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomeActivity.this);

        if(available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;

        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // An error occurred but we can resolve it
            Log.d(TAG, "isServicesOK: an error occurred but we can resolve it");

            Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(HomeActivity.this, available, ERROR_DIALOG_REQUEST);
            errorDialog.show();
        } else {
            Toast.makeText(this, "We can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}
