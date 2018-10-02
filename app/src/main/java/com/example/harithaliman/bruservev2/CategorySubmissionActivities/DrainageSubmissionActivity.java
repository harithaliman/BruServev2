package com.example.harithaliman.bruservev2.CategorySubmissionActivities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harithaliman.bruservev2.Complaint;
import com.example.harithaliman.bruservev2.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;

public class DrainageSubmissionActivity extends AppCompatActivity {

    EditText drainageComplaintTitle, drainageComplaintDescription, drainageComplaintDateOfEncounter;

    Complaint complaint;


    // Variables for location retrieval
    private static final int REQUEST_LOCATION = 1;
    Button buttonGetCurrentLocation;
    TextView textViewCoordinates;
    LocationManager locationManager;
    double latitude, longitude;

    // Variables for image addition
    ImageButton imageButtonAddPhoto;
    private int gallery_intent = 2;
    private StorageReference storageReference;
    StorageReference imagePath;

    // Variables for camera
    ImageButton imageButtonCamera;
    private ProgressDialog mProgress;
    private static final int CAMERA_REQUEST_CODE = 1;
    private Uri mImageUri = null;
    File cameraFile = null;

    AlertDialog alertDialog;

    // Camera Variables
    String mCurrentPhotoPath;

    // Database References

    // GeoFire References
    DatabaseReference geoFireRef = FirebaseDatabase.getInstance().getReference().child("GeoFireCoordinates");
    //GeoFire geoFire = new GeoFire(geoFireRef);

    Location location;


    class myOnDismissListener implements DialogInterface.OnDismissListener {

        @Override
        public void onDismiss(DialogInterface dialog) {
            // TODO Auto-generated method stub
            alertDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drainage_submission);

        drainageComplaintTitle = (EditText) findViewById(R.id.drainageTitleEditText);
        //potholeComplaintLocation = (EditText)findViewById(R.id.potholeTitleEditText);
        drainageComplaintDescription = (EditText) findViewById(R.id.drainageDescriptionEditText);
        drainageComplaintDateOfEncounter = (EditText) findViewById(R.id.drainageDateOfEncounterEditText);

        // onCreate for retrieving location
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        }, REQUEST_LOCATION);

        textViewCoordinates = (TextView) findViewById(R.id.textViewCoordinates);
        buttonGetCurrentLocation = (Button) findViewById(R.id.buttonGetCurrentLocation);

        // onCreate for image addition
        imageButtonAddPhoto = (ImageButton) findViewById(R.id.imageButtonAddPhoto);

        complaint = new Complaint();

        storageReference = FirebaseStorage.getInstance().getReference();

        // Camera

        imageButtonCamera = (ImageButton) findViewById(R.id.imageButtonCamera);

        mProgress = new ProgressDialog(this);

        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                }
                //startActivityForResult(intent,CAMERA_REQUEST_CODE);


/*
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

                */
            }
        });
    }

    // Get Current Location Button Onclick
    public void getCurrentLocationCoordinates(View view) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(DrainageSubmissionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DrainageSubmissionActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DrainageSubmissionActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_LOCATION);
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (location != null) {
                double doubleLatitude = location.getLatitude();
                double doubleLongitude = location.getLongitude();
                latitude = doubleLatitude;
                longitude = doubleLongitude;
//                latitude = String.valueOf(doubleLatitude);
//                longitude = String.valueOf(doubleLongitude);

                textViewCoordinates.setText("Your current location is " + "\nLatitude= " + latitude + "\nLongitude= " + longitude);


            } else if (location1 != null) {
                double doubleLatitude = location1.getLatitude();
                double doubleLongitude = location1.getLongitude();
                latitude = doubleLatitude;
                longitude = doubleLongitude;
//                latitude = String.valueOf(doubleLatitude);
//                longitude = String.valueOf(doubleLongitude);

                textViewCoordinates.setText("Your current location is " + "\nLatitude= " + latitude + "\nLongitude= " + longitude);

            } else if (location2 != null) {
                double doubleLatitude = location2.getLatitude();
                double doubleLongitude = location2.getLongitude();
                latitude = doubleLatitude;
                longitude = doubleLongitude;
//                latitude = String.valueOf(doubleLatitude);
//                longitude = String.valueOf(doubleLongitude);

                textViewCoordinates.setText("Your current location is " + "\nLatitude= " + latitude + "\nLongitude= " + longitude);
            } else {
                Toast.makeText(this, "Unable to trace your location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please turn on your GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void buttonImageChoose(View view) {
//        Intent photoIntent = new Intent(Intent.ACTION_PICK);
//
//        photoIntent.setType("image/*");
//
//        startActivityForResult(photoIntent, gallery_intent);

        AlertDialog.Builder builder = new AlertDialog.Builder(DrainageSubmissionActivity.this);
        alertDialog = builder.create();
        alertDialog.setOnDismissListener(new DrainageSubmissionActivity.myOnDismissListener());

        // alertDialog.setTitle("Complete ");
        alertDialog.setMessage("Complete action using:");

        alertDialog.setButton(DialogInterface.BUTTON1, "Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(takePicture, 0);

            }
        });
        alertDialog.setButton(DialogInterface.BUTTON2, "Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);
            }

        });
        alertDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @android.support.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                imageButtonCamera.setImageURI(resultUri);
                mImageUri = resultUri;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


        switch (requestCode) {
            // Camera Case
            case 0:
                if (resultCode == RESULT_OK) {

                }
                break;
            // Gallery Case
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    imageButtonAddPhoto.setImageURI(selectedImage);

                    imagePath = storageReference.child("DrainageImages").child(selectedImage.getLastPathSegment());

                    imagePath.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Toast.makeText(PotholeSubmissionActivity.this, "", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
                break;
        }


    }

    public void submitComplaint(View v) {
        String title = drainageComplaintTitle.getText().toString();
        String description = drainageComplaintDescription.getText().toString();
        String dateOfEncounter = drainageComplaintDateOfEncounter.getText().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String category = "Drainage";


        Complaint complaint = new Complaint();
        complaint.setComplaintTitle(title);
        complaint.setComplaintDescription(description);

//        if (latitude != null && longitude != null) {
//            complaint.setLatitude(latitude);
//            complaint.setLongitude(longitude);
//        }

//        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("ActiveComplaints");
//
//        GeoFire geoFire = new GeoFire(locationRef);
//        String key = geoFire.getDatabaseReference().push().getKey();
//        geoFire.setLocation(key, new GeoLocation(location.getLatitude(), location.getLongitude()));

        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("ActiveComplaints");

        GeoFire geoFire = new GeoFire(locationRef);
        String key = geoFire.getDatabaseReference().push().getKey();
        geoFire.setLocation(key, new GeoLocation(latitude, longitude));
        //geoFire.setLocation(title, new GeoLocation(latitude, longitude));

        complaint.setDateOfEncounter(dateOfEncounter);

        complaint.setImageAddress(imagePath.toString());

        complaint.setCurrentStatus("Active");

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference ref = rootRef.child("complaints").child("drainage");

        ref.push().setValue(complaint).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(DrainageSubmissionActivity.this, "Complaint Submitted!", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DrainageSubmissionActivity.this, "Error on submission", Toast.LENGTH_SHORT).show();

            }
        });
    }


}
