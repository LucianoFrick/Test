package com.example.prova.entities;

        import java.text.DateFormat;
        import java.util.Calendar;

public class Prenotazione {
    private String citta;
    private String negozio;
    private int ora;
    private int minuti;
    private String data;
    private long ts;

    public Prenotazione(String citta, String negozio, int ora, int minuti, String data, long ts){
        this.citta=citta;
        this.negozio=negozio;
        this.ora=ora;
        this.minuti=minuti;
        this.data=data;
        this.ts=ts;
    }

    public Prenotazione() {
    }

    public String getCitta() {
        return citta;
    }
    public String getNegozio() { return negozio; }
    public int getOra(){
        return  ora;
    }
    public int getMinuti(){ return  minuti; }
    public String getData() { return data; }
    public long getTs() { return ts; }
}
