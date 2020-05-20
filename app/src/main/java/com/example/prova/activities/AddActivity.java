package com.example.prova.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.prova.R;
import com.example.prova.entities.Prenotazione;
import com.example.prova.fragments.DatePickerFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private Button btnPrenota, btnDate, btnCheck;
    private Button btnMaps;
    private int y, mo , d;
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

        btnPrenota=findViewById(R.id.btnPrenota);
        btnPrenota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote(citySpinner, marketSpinner, hourSpinner, minuteSpinner);
            }
        });

        //PROVANDO A COLLEGARE CON INTENT LA ACTIVITY CON MAPS
        btnMaps = findViewById(R.id.btnMaps);
        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddActivity.this, MapsActivity.class);

                String cityValue = citySpinner.getSelectedItem().toString();
                switch(cityValue){
                    case "Camerano":
                        double latit=43.5312679;
                        double longit =13.5515741;

                        intent.putExtra("latit", latit);
                        intent.putExtra("longit", longit);

                        break;

                    case "CupraMontana":
                        double latit2=43.4497;
                        double longit2 =13.1131;

                        intent.putExtra("latit", latit2);
                        intent.putExtra("longit", longit2);
                        break;
                }
                        intent.putExtra("citta",citySpinner.getSelectedItem().toString());
                        startActivity(intent);
            }
        });

        btnDate=findViewById(R.id.button_data);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        btnCheck=findViewById(R.id.button_check);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData(hourSpinner, minuteSpinner, marketSpinner);
            }
        });
    }

    private void checkData(Spinner hourS, Spinner minuteS, Spinner marketS) {
        int x = Integer.parseInt(hourS.getSelectedItem().toString()) - 8;
        int z = Integer.parseInt(minuteS.getSelectedItem().toString()) / 10;

        CollectionReference notebookRef = FirebaseFirestore.getInstance()
                .collection(marketS.getSelectedItem().toString() + d+mo+y);

        Toast.makeText(this,String.valueOf(notebookRef), Toast.LENGTH_LONG);


    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        y=year;
        c.set(Calendar.MONTH, month);
        mo=month;
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        d=dayOfMonth;
        String chosenDate = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        TextView dateText = findViewById(R.id.text_data);
        dateText.setText(chosenDate);
    }

    private void saveNote(Spinner citySpinner, Spinner marketSpinner, Spinner hourSpinner, Spinner minuteSpinner) {
            String citta = citySpinner.getSelectedItem().toString();
            String negozio = marketSpinner.getSelectedItem().toString();
            int ora = Integer.parseInt(hourSpinner.getSelectedItem().toString());
            int minuti = Integer.parseInt(minuteSpinner.getSelectedItem().toString());

            if (citta.trim().isEmpty() || negozio.trim().isEmpty()) {
                Toast.makeText(this, "Please insert all fields", Toast.LENGTH_LONG);
            }

            //salva prenotazione nella cartella dell'utente
            CollectionReference notebookRef = FirebaseFirestore.getInstance()
                    .collection("utenti").document(currentUser.getEmail()).collection("Prenotazioni");
            notebookRef.add(new Prenotazione(citta, negozio, ora, minuti));

        //salva prenotazione nella cartella di tutte le prenotazioni
        notebookRef = FirebaseFirestore.getInstance()
                .collection("Supermercati").document(marketSpinner.getSelectedItem().toString()).collection("Prenotazioni");
        notebookRef.add(new Prenotazione(citta, negozio, ora, minuti));


            Toast.makeText(this, "Note added", Toast.LENGTH_SHORT);
            finish();

    }


}
