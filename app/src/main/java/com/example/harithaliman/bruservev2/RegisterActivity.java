package com.example.harithaliman.bruservev2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText username, email, password, confirmPassword, phoneNumber;
    Button btnRegister;
    TextView mLoginPageBack;
    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabase;
    String Name, Email, Password, ConfirmPassword, PhoneNumber;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = (EditText)findViewById(R.id.editTextUsername);
        email = (EditText)findViewById(R.id.editTextEmail);
        password = (EditText)findViewById(R.id.editTextPassword);
        confirmPassword = (EditText)findViewById(R.id.editTextConfirmPassword);
        phoneNumber = (EditText)findViewById(R.id.editTextPhoneNumber);

        btnRegister = (Button)findViewById(R.id.btnRegister);
        //mLoginPageBack = (TextView)findViewById(R.id.buttonLogin);

        // for authentication using FirebaseAuth.
        firebaseAuth = FirebaseAuth.getInstance();
        btnRegister.setOnClickListener(this);
        // mLoginPageBack.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    @Override
    public void onClick(View v) {
        if (v==btnRegister){
            UserRegister();
        }else if (v== mLoginPageBack){
            startActivity(new Intent(RegisterActivity.this,InitialActivity.class));
        }
    }

    private void UserRegister() {
        Name = username.getText().toString().trim();
        Email = email.getText().toString().trim();
        Password = password.getText().toString().trim();
        ConfirmPassword = confirmPassword.getText().toString().trim();
        PhoneNumber = phoneNumber.getText().toString().trim();

        if (TextUtils.isEmpty(Name)){
            Toast.makeText(RegisterActivity.this, "Enter Name", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(Email)){
            Toast.makeText(RegisterActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(Password)){
            Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }else if (Password.length()<6){
            Toast.makeText(RegisterActivity.this,"Password must be greater then 6 digit",Toast.LENGTH_SHORT).show();
            return;
        }else if (!Password.equals(ConfirmPassword)) {
            Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        } else {


        progressDialog.setMessage("Creating User please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    sendEmailVerificationEmail();
                    progressDialog.dismiss();
                    OnAuth(task.getResult().getUser());
                    firebaseAuth.signOut();
                    Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(loginIntent);
                }else{
                    Toast.makeText(RegisterActivity.this,"error on creating user",Toast.LENGTH_SHORT).show();

                    if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "Email is already registered", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        }
    }


    private void sendEmailVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){

            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this,"Check your Email for verification",Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();


                    }
                }
            });
        }
    }

    private void OnAuth(FirebaseUser user) {
        createAnewUser(user.getUid());
    }

    private void createAnewUser(String uid) {
        User user = BuildNewuser();
        mDatabase.child(uid).setValue(user);
    }


    private User BuildNewuser(){
        return new User(
                getDisplayName(),
                getUserEmail(),
                new Date().getTime(),
                getPhoneNumber()
        );
    }

    public String getDisplayName() {
        return Name;
    }

    public String getUserEmail() {
        return Email;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }
}
