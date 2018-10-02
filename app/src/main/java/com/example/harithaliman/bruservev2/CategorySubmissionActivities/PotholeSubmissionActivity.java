package com.example.harithaliman.bruservev2.CategorySubmissionActivities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
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
import com.firebase.geofire.core.GeoHash;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.theartofdev.edmodo.cropper.CropImage;

public class PotholeSubmissionActivity extends AppCompatActivity {

    EditText potholeComplaintTitle, potholeComplaintLocation, potholeComplaintDescription, potholeComplaintDateOfEncounter, potholeSiteLocation;

    Complaint complaint;

    // Variables for location retrieval
    private static final int REQUEST_LOCATION = 1;
    Button buttonGetCurrentLocation;
    TextView textViewCoordinates;
    LocationManager locationManager;
    double latitude, longitude;

    String complaintId;

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



    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


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
        setContentView(R.layout.activity_pothole_submission);

        potholeComplaintTitle = (EditText) findViewById(R.id.potholeTitleEditText);
        //potholeComplaintLocation = (EditText)findViewById(R.id.potholeTitleEditText);
        potholeComplaintDescription = (EditText) findViewById(R.id.potholeDescriptionEditText);
        potholeComplaintDateOfEncounter = (EditText) findViewById(R.id.potholeDateOfEncounterEditText);
        potholeSiteLocation = (EditText) findViewById(R.id.potholeSiteLocationEditText);


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
        if (ActivityCompat.checkSelfPermission(PotholeSubmissionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PotholeSubmissionActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PotholeSubmissionActivity.this, new String[]{
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

        AlertDialog.Builder builder = new AlertDialog.Builder(PotholeSubmissionActivity.this);
        alertDialog = builder.create();
        alertDialog.setOnDismissListener(new myOnDismissListener());

        // alertDialog.setTitle("Complete ");
        alertDialog.setMessage("Complete action using:");

        alertDialog.setButton(DialogInterface.BUTTON1, "Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                uriConvert();
//                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                if (takePicture.resolveActivity(getPackageManager()) != null) {
//                    File photoFile = null;
//                    try{
//                        photoFile = createImageFile();
//                    } catch (IOException e) {
//
//                    }
//
//                    if (photoFile != null) {
//                        Uri photoURI = FileProvider.getUriForFile(this, "com.example.harithaliman.bruservev2", photoFile);
//                        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                        startActivityForResult(takePicture, 0);
//                    }
//                }



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

    private void uriConvert() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePicture.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try{
                photoFile = createImageFile();
            } catch (IOException e) {

            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.harithaliman.bruservev2", photoFile);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePicture, 0);
            }
        }
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
//                    Bundle extras = data.getExtras();
//                    Bitmap bitmap = (Bitmap)data.getExtras().get("data");
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                    byte[] dataBAOS = baos.toByteArray();
//
//                    imageButtonAddPhoto.setImageBitmap(bitmap);
//
//                    StorageReference storageRef = FirebaseStorage.getInstance().getReference("PotholeImages");
//
//                    StorageReference imagesRef = storageRef.child("filename" + new Date().getTime());
//
//
//
//                    UploadTask uploadTask = imagesRef.putBytes(dataBAOS);
//                    uploadTask.addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(PotholeSubmissionActivity.this, "Camera Failed", Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                        }
//                    });
                    Uri selectedImage = data.getData();
                    imageButtonAddPhoto.setImageURI(selectedImage);

                    imagePath = storageReference.child("PotholeImages").child(selectedImage.getLastPathSegment());

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
            // Gallery Case
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    imageButtonAddPhoto.setImageURI(selectedImage);

                    imagePath = storageReference.child("PotholeImages").child(selectedImage.getLastPathSegment());

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

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        //GeoFire geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("complaintsPothole_location"));

        complaintId = rootRef.child("complaintsPothole").push().getKey();

        rootRef.child("complaintsPothole").child(complaintId).setValue(complaint);

        GeoFire geoFire = new GeoFire(rootRef.child("complaintsPothole_location"));

        geoFire.setLocation(complaintId, new GeoLocation(latitude, longitude));

        GeoHash geoHash = new GeoHash(new GeoLocation(latitude, longitude));

        Map<String, Object> complaintsAddition = new HashMap<>();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String category = "Pothole";

        String title = potholeComplaintTitle.getText().toString();
        String description = potholeComplaintDescription.getText().toString();
        String dateOfEncounter = potholeComplaintDateOfEncounter.getText().toString();
        String siteLocation = potholeSiteLocation.getText().toString();

        Complaint complaint = new Complaint();
        complaint.setUserId(userId);
        complaint.setComplaintTitle(title);
        complaint.setComplaintDescription(description);
        complaint.setSiteLocation(siteLocation);
        complaint.setCategory(category);
        complaint.setDateOfEncounter(dateOfEncounter);
        complaint.setComplaintValue(1);


        complaint.setImageAddress(imagePath.toString());


        complaint.setCurrentStatus("Active");

        complaintsAddition.put("complaintsPothole/" + complaintId, complaint);
        complaintsAddition.put("complaintsPothole_location/" + complaintId + "/g", geoHash.getGeoHashString());
        complaintsAddition.put("complaintsPothole_location/" + complaintId + "/l", Arrays.asList(latitude,longitude));
        rootRef.updateChildren(complaintsAddition).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(PotholeSubmissionActivity.this, "Complaint Submitted!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PotholeSubmissionActivity.this, "Error on submission", Toast.LENGTH_SHORT).show();

            }
        });

    }
}


