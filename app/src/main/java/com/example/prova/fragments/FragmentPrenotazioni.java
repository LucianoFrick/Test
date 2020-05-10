package com.example.prova.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova.AddActivity;
import com.example.prova.R;
import com.example.prova.entities.Prenotazione;
import com.example.prova.uiutilities.FeedAdapter;

import java.util.ArrayList;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

public class FragmentPrenotazioni extends Fragment {

    private static final int NEW_PRENOTATION = 301;
    private RecyclerView recyclerView;
    private FeedAdapter feedAdapter;
    private ArrayList<Prenotazione> recensioni;
    private ImageButton btnAdd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recensioni=new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_prenotazioni, container, false);

        recyclerView=view.findViewById(R.id.rv_prenotazioni);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        feedAdapter = new FeedAdapter(recensioni);
        recyclerView.setAdapter(feedAdapter);

        btnAdd=view.findViewById(R.id.button_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), AddActivity.class), NEW_PRENOTATION);
                }

        });
        return view;
    }

    private String randomString(int count){
        final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKL MNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        while(count-- != 0){
            //int character = (int) (Math.random()+ALPHA_NUMERIC_STRING.length());
            Random random = new Random();
            int character = random.nextInt(36);
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode==NEW_PRENOTATION) && (resultCode==RESULT_OK)){
            String citta = data.getStringExtra("citta");
            String negozio = data.getStringExtra("negozio");
            int ora = Integer.parseInt(data.getStringExtra("ora"));
            int minuti = Integer.parseInt(data.getStringExtra("minuti"));
            String durata = data.getStringExtra("durata");

            Prenotazione prenotazione = new Prenotazione(citta, negozio, ora, minuti,durata);
            recensioni.add(prenotazione);
            feedAdapter.notifyDataSetChanged();//ogni volta che fai modifica nella struttura affinche il recyclerview sia aggiornato bisogna richiamareil notify, cosi sa cheè stato cambiato e aggiorna visualizzazion

        }
    }
}
