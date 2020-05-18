package com.example.prova.entities;

public class Days {
    public int date;
    public int [][]orari=new int[12][6];

    public Days(){
        for (int i=0; i<12; i++){
            for(int j=0; j<6;j++){
                orari[i][j]=0;
            }
        }
    }

    public Days (int a, int b){
        for (int i=0; i<12; i++){
            for(int j=0; j<6;j++){
                if(a==i && b==j)
                    orari[i][j]+=1;
            }
        }
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
