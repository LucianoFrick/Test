package com.example.prova;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseAuth nAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = nAuth.getCurrentUser();
        getSupportActionBar().setTitle(currentUser.getDisplayName());
    }
}
