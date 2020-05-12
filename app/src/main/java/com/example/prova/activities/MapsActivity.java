package com.example.prova.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.prova.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latit, longit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        try{
            //Inserisco lat e lng del paese selezionato in due variabili
            Bundle datiPassati = getIntent().getExtras();
            latit = datiPassati.getDouble("latit", 0);
            longit = datiPassati.getDouble("longit", 0);

        }catch (NullPointerException e){
            Toast.makeText(this, "SCEGLI LA CITTA", Toast.LENGTH_SHORT).show();
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng paese = new LatLng(latit, longit); //creo variabile di tipo LatLng e ci metto le informazioni ricevute dall'addActivity

        // Add a marker in paese and move the camera
        CameraPosition posizionePaese = new CameraPosition.Builder().target(
                paese).zoom(15).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(posizionePaese));
        MarkerOptions marker = new MarkerOptions().position(paese);
        googleMap.addMarker(marker);

        // Add a marker in Cupra and move the camera

/*
        mMap.addMarker(new MarkerOptions().position(cupra).title("Marker in Cupra"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cupra));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(cupra)); */

    }

}
