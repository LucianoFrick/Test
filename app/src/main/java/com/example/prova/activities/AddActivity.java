package com.example.prova.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.prova.R;
import com.example.prova.entities.Prenotazione;
import com.example.prova.fragments.DatePickerFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private Button btnPrenota, btnDate, btnCheck;
    private Button btnMaps;
    private String chosenDate;
    private TextView countText;
    final Calendar c = Calendar.getInstance();
    private long ts;
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
       final TextView dateText = findViewById(R.id.text_data);
       countText=findViewById(R.id.text_count);

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
        btnPrenota.setEnabled(false);//pulsante non cliccabile finché non ho fatto il check
        btnPrenota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote(citySpinner, marketSpinner, hourSpinner, minuteSpinner, dateText);
            }
        });

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
                DialogFragment datePicker = new DatePickerFragment(); //crea fragment dialog con date picker
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        btnCheck=findViewById(R.id.button_check);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check(hourSpinner, minuteSpinner,marketSpinner);
                  }
        });
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        chosenDate = DateFormat.getDateInstance().format(c.getTime());
        TextView dateText = findViewById(R.id.text_data);
        dateText.setText(chosenDate);
    }

    private void saveNote(Spinner citySpinner, final Spinner marketSpinner, Spinner hourSpinner, Spinner minuteSpinner, final TextView data) {
            final String citta = citySpinner.getSelectedItem().toString();
            final String negozio = marketSpinner.getSelectedItem().toString();
            final int ora = Integer.parseInt(hourSpinner.getSelectedItem().toString());
            final int minuti = Integer.parseInt(minuteSpinner.getSelectedItem().toString());

            //creo due percorsi, uno per utenti uno per supermercati
            final CollectionReference notebookRef = FirebaseFirestore.getInstance()
                    .collection("utenti").document(currentUser.getEmail()).collection("Prenotazioni");
            final CollectionReference notebookRef2 = FirebaseFirestore.getInstance()
                    .collection("Supermercati").document(marketSpinner.getSelectedItem().toString()).collection("Prenotazioni");

            if(data.getText().equals("Scegli la data")){ //se il text data è quello di default crea toast e si stoppa
                Toast.makeText(AddActivity.this, "Inserisci data", Toast.LENGTH_LONG).show();
                return;
            }

        notebookRef2.document(currentUser.getEmail()+data.getText())//metodo che controlla se esiste già una mia prenotazione per qeul supermercato in quel giorno, procede solo in caso di false
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Toast.makeText(AddActivity.this, "Cancella la vecchia prima", Toast.LENGTH_LONG).show();

                    } else {
                        notebookRef.add(new Prenotazione(citta, negozio, ora, minuti, chosenDate, ts));

                        //salva prenotazione nella cartella di tutte le prenotazioni
                        notebookRef2.document(currentUser.getEmail()+data.getText()).set(new Prenotazione(citta, negozio, ora, minuti, chosenDate,ts));

                        Log.e("a", c.toString());
                        finish();
                    }
                } else {

                }
            }
        });
    }

    private void check(Spinner hourSpinner, Spinner minuteSpinner, Spinner marketSpinner){ //controlla prenotazioni
        final int ora = Integer.parseInt(hourSpinner.getSelectedItem().toString());
        final int minuti = Integer.parseInt(minuteSpinner.getSelectedItem().toString());
        final int[] count = {0};

        //creo una variabile calendario con le scelte fatte
        c.set(Calendar.HOUR, ora);
        c.set(Calendar.MINUTE, minuti);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        ts=c.getTimeInMillis()/1000;//creo variabile time stamp in base al calendario creato, divido per mille perche non conto i millisecondi


        FirebaseFirestore.getInstance().collection("Supermercati").document(marketSpinner.getSelectedItem().toString()).
                collection("Prenotazioni").whereEqualTo("ts", ts).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() { // vado nel percorso di firebase del upermercato che ho scelto e guardo se ci sono documenti con il mio stesso timestamp
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot snapshot : list){
                    count[0] += 1;
                    countText.setText(String.valueOf(count[0])); //se trovo aumento di 1, non funzona come contatore solo si o no (va migliorato)
                }
            }
        });

        btnPrenota.setEnabled(true);
        Log.e("ts", String.valueOf(ts));

    }

    public int count(int c){
        c++;
        return  c;
    }

}
