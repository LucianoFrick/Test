package com.example.prova;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText textNome, textCognome, textPassword, textEmail;
    private Button btnRegistra;
    private FirebaseAuth nAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nAuth = FirebaseAuth.getInstance();

        textNome = findViewById(R.id.text_nome);
        textCognome=findViewById(R.id.text_cognome);
        textEmail=findViewById(R.id.text_email);
        textPassword=findViewById(R.id.text_psw);
        btnRegistra= findViewById(R.id.button);
        btnRegistra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    final String nome = textNome.getText().toString();
                    final String cognome = textCognome.getText().toString();
                    String email = textEmail.getText().toString();
                    String password = textPassword.getText().toString();

                    nAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                final FirebaseUser user = nAuth.getCurrentUser();
                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(nome + " " + cognome).build();
                                user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        writeUserToDb(nome, cognome, user.getUid());
                                        Intent intent = new Intent();
                                        intent.putExtra("nome", textNome.getText().toString());
                                        intent.putExtra("cognome", textCognome.getText().toString());
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                });
                            }
                            else{
                                task.getException().printStackTrace();
                                Toast.makeText(LoginActivity.this, getString(R.string.errorlogin), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                catch(NullPointerException e){
                    Toast.makeText(LoginActivity.this, getString(R.string.inforequire), Toast.LENGTH_SHORT).show();
                }
            }
        });

        getSupportActionBar().setTitle(getString(R.string.app_name));
    }

    private void writeUserToDb(String nome, String cognome, String uid){
        Map<String, Object> user = new HashMap<>();
        user.put("nome", nome);
        user.put("cognome", cognome);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("utenti").document(uid).set(user);

    }
}