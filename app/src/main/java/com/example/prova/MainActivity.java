package com.example.prova;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    public static final int LOGIN_REQUEST = 101;
    private FirebaseAuth nAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        if(preferences.getBoolean("firstrun", true)) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST);
        }
        else{
            nAuth= FirebaseAuth.getInstance();
            FirebaseUser currentUser = nAuth.getCurrentUser();
            getSupportActionBar().setTitle(currentUser.getDisplayName());
        }
    }


}
