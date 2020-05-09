package com.example.prova;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class AddActivity extends AppCompatActivity {
    private Button btnPrenota;
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
    }
}
