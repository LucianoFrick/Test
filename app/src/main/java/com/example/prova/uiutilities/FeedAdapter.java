package com.example.prova.uiutilities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova.R;
import com.example.prova.entities.Prenotazione;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.CViewHolder>{

    class CViewHolder extends RecyclerView.ViewHolder{
        TextView textCitta, textNegozio, textOra, textDurata;

        CViewHolder(@NonNull View itemView){
            super(itemView);
            textCitta =  itemView.findViewById(R.id.text_citta);
            textNegozio =  itemView.findViewById(R.id.text_negozio);
            textOra =  itemView.findViewById(R.id.text_ora);
            textDurata =  itemView.findViewById(R.id.text_durata);

        }
    }

    private ArrayList<Prenotazione> struttura;

    public FeedAdapter(ArrayList<Prenotazione> struttura){
        this.struttura=struttura;
    }

    @Override
    public CViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_row, parent, false);
        return new CViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CViewHolder holder, int position){
        holder.textCitta.setText(struttura.get(position).getCitta());
        holder.textNegozio.setText(struttura.get(position).getNegozio());
        holder.textOra.setText(struttura.get(position).getOra()+":"+struttura.get(position).getMinuti());
        holder.textDurata.setText(struttura.get(position).getDurata());
    }

    @Override
    public int getItemCount(){
        return struttura.size();
    }
}
