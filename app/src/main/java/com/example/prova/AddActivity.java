package com.example.prova;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prova.entities.Prenotazione;
import com.example.prova.fragments.FragmentProfilo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddActivity extends AppCompatActivity {
    private Button btnPrenota;
    FirebaseAuth nAuth = FirebaseAuth.getInstance();
    final FirebaseUser currentUser = nAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

       final Spinner citySpinner=findViewById(R.id.spinner_city);
       final Spinner marketSpinner = findViewById(R.id.spinner_market);
       final Spinner hourSpinner = findViewById(R.id.spinner_hour);
       final Spinner minuteSpinner = findViewById(R.id.spinner_minutes);
       final Spinner durationSpinner = findViewById(R.id.spinner_duration);

       ArrayAdapter<CharSequence> adapter = ArrayAdapter
               .createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
       final ArrayAdapter<CharSequence> adapter2 = ArrayAdapter
               .createFromResource(this, R.array.marketCamerano, android.R.layout.simple_spinner_item);
        final ArrayAdapter<CharSequence> adapter3 = ArrayAdapter
                .createFromResource(this, R.array.marketCupra, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter
                .createFromResource(this, R.array.hours, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter
                .createFromResource(this, R.array.minutes, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter6 = ArrayAdapter
                .createFromResource(this, R.array.durata, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       citySpinner.setAdapter(adapter);
       hourSpinner.setAdapter(adapter4);
       minuteSpinner.setAdapter(adapter5);
       durationSpinner.setAdapter(adapter6);

       btnPrenota=findViewById(R.id.btnPrenota);
       citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               String cityValue = citySpinner.getSelectedItem().toString();
               switch(cityValue){
                   case "Camerano":
                       marketSpinner.setAdapter(adapter2);
                       break;
                   case "CupraMontana":
                       marketSpinner.setAdapter(adapter3);
                       break;
               }
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });
        btnPrenota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote(citySpinner, marketSpinner, hourSpinner, minuteSpinner, durationSpinner);
            }
        });
    }

    private void saveNote(Spinner citySpinner, Spinner marketSpinner, Spinner hourSpinner, Spinner minuteSpinner, Spinner durationSpinner) {
            String citta = citySpinner.getSelectedItem().toString();
            String negozio = marketSpinner.getSelectedItem().toString();
            int ora = Integer.parseInt(hourSpinner.getSelectedItem().toString());
            int minuti = Integer.parseInt(minuteSpinner.getSelectedItem().toString());
            String durata = durationSpinner.getSelectedItem().toString();

            if (citta.trim().isEmpty() || negozio.trim().isEmpty()) {
                Toast.makeText(this, "Please insert all fields", Toast.LENGTH_LONG);
            }

            CollectionReference notebookRef = FirebaseFirestore.getInstance()
                    .collection(currentUser.getEmail()+"_Notebook");
            notebookRef.add(new Prenotazione(citta, negozio, ora, minuti, durata));
            Toast.makeText(this, "Note added", Toast.LENGTH_SHORT);
            finish();

    }


}
