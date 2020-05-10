package com.example.prova;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private Button btnLogin, btnRegister;
    private EditText emailSU, passwordSU;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailSU=findViewById(R.id.emailLogin);
        passwordSU=findViewById(R.id.passwordLogin);
        btnLogin=findViewById(R.id.btnLogin);
        btnRegister=findViewById(R.id.btnRegister);
        firebaseAuth =FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    firebaseAuth.signInWithEmailAndPassword(emailSU.getText().toString(), passwordSU.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE); //
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putBoolean("firstrun",false);
                                        editor.apply();
                                        finish(); //cosi se uno stronzo clicca il pulsante indietro non torna più sulla schermata iniziale
                                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }catch(Exception e) {
                    Toast.makeText(MainActivity.this, getString(R.string.inforequire), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                finish();
                startActivity(intent);
            }
        });
        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);

        if(preferences.getBoolean("firstrun", true)) {
        }
        else{
            finish();//cosi la main si chiude e non ci si torna
            startActivity(new Intent(this, ProfileActivity.class));
        }
    }
}
