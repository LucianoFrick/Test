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

public class FireAdapter extends FirestoreRecyclerAdapter<Prenotazione, FireAdapter.FireHolder> {

    public FireAdapter(@NonNull FirestoreRecyclerOptions<Prenotazione> options) {
        super(options);
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onBindViewHolder(@NonNull FireHolder fireHolder, int i, @NonNull Prenotazione prenotazione) {
        fireHolder.textCitta.setText(prenotazione.getCitta());
        fireHolder.textNegozio.setText(prenotazione.getNegozio());
        fireHolder.textOra.setText(String.format("%d:%d", prenotazione.getOra(), prenotazione.getMinuti()));
        fireHolder.textDurata.setText(prenotazione.getDurata());
    }

    @NonNull
    @Override
    public FireHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_row, parent,false);
        return new FireHolder(v);
    }

    public void deleteItem(int position){ //metodo easy che elimina la prenotazione, viene richiamata con lo swipe
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public void startListening() {
    }

    public void stopListening() {
    }

    class FireHolder extends RecyclerView.ViewHolder{

        TextView textCitta, textNegozio, textOra, textDurata;

        public FireHolder(@NonNull View itemView) {
            super(itemView);
            textCitta =  itemView.findViewById(R.id.text_citta);
            textNegozio =  itemView.findViewById(R.id.text_negozio);
            textOra =  itemView.findViewById(R.id.text_ora);
            textDurata =  itemView.findViewById(R.id.text_durata);

        }
    }
}
