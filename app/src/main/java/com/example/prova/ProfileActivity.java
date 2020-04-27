package com.example.prova;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.prova.fragments.FragmentFeed;
import com.example.prova.fragments.FragmentProfilo;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private ImageView propic;
    public TextView textNome;
    private BottomNavigationView bottomNav;
    private FragmentProfilo fragmentProfilo;
    private FragmentFeed fragmentFeed;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void logOut(MenuItem Item) {
        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("firstrun", true);
        editor.apply();
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle(getString(R.string.benvenuti));

        fragmentFeed= new FragmentFeed();
        fragmentProfilo= new FragmentProfilo();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentFeed).commit();

        bottomNav=findViewById(R.id.bottomnav);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch(item.getItemId()){
                    case R.id.menu_feed:
                        selectedFragment = fragmentFeed;
                        break;
                    case R.id.menu_profile:
                        selectedFragment= fragmentProfilo;
                        break;
                }
                if(selectedFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }
                return true;
            }
        });



    }
}
