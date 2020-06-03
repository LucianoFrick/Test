package com.example.prova.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prova.R;
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
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText textNome, textCognome, textPassword, textEmail;
    private Button btnRegistra, btnAlready;
    private FirebaseAuth nAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nAuth = FirebaseAuth.getInstance();

        textNome = findViewById(R.id.text_nome);
        textCognome=findViewById(R.id.text_cognome);
        textEmail=findViewById(R.id.text_email);
        textPassword=findViewById(R.id.text_psw);

        btnRegistra= findViewById(R.id.button);
        btnRegistra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String nome = textNome.getText().toString().trim();
                final String cognome = textCognome.getText().toString().trim();
                final String email = textEmail.getText().toString().trim();
                final String password = textPassword.getText().toString().trim();

                //vari controlli per far inserire correttamente i dati all'user
                if (email.isEmpty()) {
                    textEmail.setError("Email required");
                    textEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    textEmail.setError("Valid Email required");
                    textEmail.requestFocus();
                    return;
                }

                if (password.isEmpty() || password.length() < 6) {
                    textPassword.setError("At least 6 characters");
                    textPassword.requestFocus();
                    return;
                }

                registerUser(nome, cognome, email, password);
            }
        });

        btnAlready = findViewById(R.id.btnAlready);
        btnAlready.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        });

        getSupportActionBar().setTitle(getString(R.string.app_name));
    }

    private void writeUserToDb(String nome, String cognome, String email, String password, String uid){
        Map<String, Object> user = new HashMap<>();
        user.put("nome", nome);
        user.put("cognome", cognome);
        user.put("email", email);
        user.put("password", password);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = nAuth.getCurrentUser();
        db.collection("utenti").document(currentUser.getEmail()).collection("Info").document(uid).set(user);
    }

    private void registerUser (final String nome, final String cognome, final String email, final String password){

        nAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    final FirebaseUser user = nAuth.getCurrentUser();
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(nome + " " + cognome).build();

                    assert user != null;
                    user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            writeUserToDb(nome, cognome, email, password, user.getUid());
                            SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("firstrun",false);
                            editor.apply();
                            Intent intent = new Intent(RegisterActivity.this, ProfileActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    });
                }else{

                    Objects.requireNonNull(task.getException()).printStackTrace();
                    Toast.makeText(RegisterActivity.this, getString(R.string.errorlogin), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}