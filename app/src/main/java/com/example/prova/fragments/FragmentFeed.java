package com.example.prova.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova.ProfileActivity;
import com.example.prova.R;
import com.example.prova.entities.Recensione;
import com.example.prova.uiutilities.FeedAdapter;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.round;

public class FragmentFeed extends Fragment {

    private RecyclerView recyclerView;
    private FeedAdapter feedAdapter;
    private ArrayList<Recensione> recensioni;
    private ImageButton btnAdd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recensioni=new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_feed, container, false);

        recyclerView=view.findViewById(R.id.rv_feed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        feedAdapter = new FeedAdapter(recensioni);
        recyclerView.setAdapter(feedAdapter);

        btnAdd=view.findViewById(R.id.button_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {}catch(Exception e){     Log.i("a","a");
                }
                    String titolo = randomString(15);
                    int voto = 5;
                    String testo = randomString(90);

                Recensione recensione = new Recensione(titolo, voto, testo);

                recensioni.add(recensione);

                    feedAdapter.notifyDataSetChanged();//ogni volta che fai modifica nella struttura affinche il recyclerview sia aggiornato bisogna richiamareil notify, cosi sa cheè stato cambiato e aggiorna visualizzazione



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
}
