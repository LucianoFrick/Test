package com.example.prova.entities;

public class Prenotazione {
    private String titolo;
    private int voto;
    private String testo;

    public Prenotazione(String titolo, int voto, String testo){
        this.titolo=titolo;
        this.voto=voto;
        this.testo=testo;
    }

    public String getTitolo() {
        return titolo;
    }

    public int getVoto(){
        return  voto;
    }

    public String getTesto(){
        return testo;
    }


}
