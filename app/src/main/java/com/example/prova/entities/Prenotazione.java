package com.example.prova.entities;

public class Prenotazione {
    private String citta;
    private String negozio;
    private int ora;
    private int minuti;
    private String durata;

    public Prenotazione(String citta, String negozio, int ora, int minuti, String durata){
        this.citta=citta;
        this.negozio=negozio;
        this.ora=ora;
        this.minuti=minuti;
        this.durata=durata;
    }

    public String getCitta() {
        return citta;
    }
    public String getNegozio() { return negozio; }
    public int getOra(){
        return  ora;
    }
    public int getMinuti(){ return  minuti; }
    public String getDurata(){
        return durata;
    }

}
