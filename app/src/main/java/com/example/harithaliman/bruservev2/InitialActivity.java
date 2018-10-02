package com.example.harithaliman.bruservev2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class InitialActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        Button initialPageRegisterButton = (Button) findViewById(R.id.btnInitialPageRegister);
        Button initialPageLoginButton = (Button) findViewById(R.id.btnInitialPageLogin);


        initialPageRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InitialActivity.this, RegisterActivity.class));
            }
        });

        initialPageLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InitialActivity.this, LoginActivity.class));
            }
        });
    }
}
