package com.example.harithaliman.bruservev2;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ComplaintsInformation extends AppCompatActivity {

    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    private ImageView complaintImage;

    TextView complaintTitleField;
    TextView complaintDescriptionField;
    TextView complaintStatusField;
    TextView categoryField;

    private String complaintId;

    private String mComplaintTitle;
    private String mComplaintDescription;
    private String mComplaintStatus;
    private String mCategoryField;

    private String mComplaintsImageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints_information);

        rootRef.child("complaintsPothole");

        complaintImage = findViewById(R.id.imageViewComplaint);

        complaintTitleField = (TextView)findViewById(R.id.textViewComplaintTitle);

        complaintDescriptionField = (TextView)findViewById(R.id.textViewComplaintDescription);

        complaintStatusField = (TextView)findViewById(R.id.textViewComplaintStatus);

        categoryField = (TextView)findViewById(R.id.textViewCategory);

        complaintImage = findViewById(R.id.imageViewComplaint);

        complaintId = rootRef.child("complaintsPothole").getKey();

        onStart();

    }

    @Override
    protected void onStart() {
        super.onStart();

        rootRef.child("complaintsPothole").child(complaintId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("complaintTitle") != null) {
                        mComplaintTitle = map.get("complaintTitle").toString();
                        complaintTitleField.setText(mComplaintTitle);
                    }

                    if(map.get("complaintDescription") != null) {
                        mComplaintDescription = map.get("complaintDescription").toString();
                        complaintDescriptionField.setText(mComplaintDescription);
                    }

                    if(map.get("imageAddress") != null) {
                        mComplaintsImageUrl = map.get("imageAddress").toString();
                        Glide.with(getApplication()).load(mComplaintsImageUrl).into(complaintImage);
                    }



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
