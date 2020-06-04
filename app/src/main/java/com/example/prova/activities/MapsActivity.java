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

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latit, longit;
    private ArrayList<LatLng> markers;
    String citta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        try{
            //Inserisco lat e lng del paese selezionato in due variabili
            Bundle datiPassati = getIntent().getExtras();
            latit = datiPassati.getDouble("latit", 0);
            longit = datiPassati.getDouble("longit", 0);
            citta=datiPassati.getString("citta", null);

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

        // move the camera at paese
        CameraPosition posizionePaese = new CameraPosition.Builder().target(
                paese).zoom(13).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(posizionePaese));

        //in base alla città scelta, si caricano sull'arrayList i supermercati della città
        markers = new ArrayList<>();
        switch(citta){
            case "CupraMontana":
                LatLng SiCamilletti = new LatLng(43.457222, 13.107696);
                LatLng AlimentariMauro = new LatLng(43.448703, 13.120636);
                LatLng CoalCu = new LatLng(43.445378, 13.113300);
                markers.add(SiCamilletti);
                markers.add(AlimentariMauro);
                markers.add(CoalCu);
                break;
            case "Camerano"://SCELTA SAGGIA
                LatLng SiLoretana = new LatLng(43.522577, 13.557408);
                LatLng SiFazioli = new LatLng(43.530817, 13.548291);
                LatLng Carrefour = new LatLng(43.521072, 13.525623);
                LatLng CoalCa = new LatLng(43.530959, 13.555702);
                markers.add(SiFazioli);
                markers.add(SiLoretana);
                markers.add(Carrefour);
                markers.add(CoalCa);
                break;
        }

        //per tutti i LatLng nell'arrayList "markers", si aggiunge un marker per ogni supermercato
        for(LatLng i : markers) {
            MarkerOptions marker = new MarkerOptions().position(i);
            googleMap.addMarker(marker);
        }
    }

}
