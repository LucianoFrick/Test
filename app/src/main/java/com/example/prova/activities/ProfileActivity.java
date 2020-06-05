package com.example.prova.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.prova.R;
import com.example.prova.fragments.FragmentPrenotazioni;
import com.example.prova.fragments.FragmentProfilo;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private FragmentProfilo fragmentProfilo;
    private FragmentPrenotazioni fragmentPrenotazioni;

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
        getSupportActionBar().setTitle(getString(R.string.app_name));

        fragmentPrenotazioni = new FragmentPrenotazioni();
        fragmentProfilo= new FragmentProfilo();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentPrenotazioni).commit();

        bottomNav=findViewById(R.id.bottomnav);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch(item.getItemId()){
                    case R.id.menu_prenotazioni:
                        selectedFragment = fragmentPrenotazioni;
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
