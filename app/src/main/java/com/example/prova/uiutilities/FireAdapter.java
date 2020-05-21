package com.example.prova.uiutilities;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova.R;
import com.example.prova.entities.Prenotazione;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireAdapter extends FirestoreRecyclerAdapter<Prenotazione, FireAdapter.FireHolder> {
    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    public FireAdapter(@NonNull FirestoreRecyclerOptions<Prenotazione> options) {
        super(options);
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onBindViewHolder(@NonNull FireHolder fireHolder, int i, @NonNull Prenotazione prenotazione) {
        fireHolder.textCitta.setText(prenotazione.getCitta());
        fireHolder.textNegozio.setText(prenotazione.getNegozio());
        fireHolder.textData.setText(prenotazione.getData());
        if(prenotazione.getMinuti()!=0) //if serve a far scrivere :00 sennò con il parse verrebe solo :0
        fireHolder.textOra.setText(String.format("%d:%d", prenotazione.getOra(), prenotazione.getMinuti()));
        else         fireHolder.textOra.setText(String.format("%d:00", prenotazione.getOra()));

    }

    @NonNull
    @Override
    public FireHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_row, parent,false);
        return new FireHolder(v);
    }

    public void deleteItem(int position){ //metodo easy che elimina la prenotazione, viene richiamata con lo swipe
       getSnapshots().getSnapshot(position).getReference().delete();//cancella dal coso utente

        CollectionReference  notebookRef = FirebaseFirestore.getInstance()//cancella da raccolta prenotazioni
                .collection("Supermercati").document(getItem(position).getNegozio()).collection("Prenotazioni");
        notebookRef.document(currentUser.getEmail()+getItem(position).getData()).delete();

    }

    public Prenotazione getItem(int position) {//prendo la prenotazione dalla recyclerview
        return getSnapshots().get(position);
    }

    class FireHolder extends RecyclerView.ViewHolder{

        TextView textCitta, textNegozio, textOra, textData;

        public FireHolder(@NonNull View itemView) {
            super(itemView);
            textCitta =  itemView.findViewById(R.id.text_citta);
            textNegozio =  itemView.findViewById(R.id.text_negozio);
            textOra =  itemView.findViewById(R.id.text_ora);
            textData =itemView.findViewById(R.id.text_data);
        }
    }
}
