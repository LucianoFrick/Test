package com.example.prova.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova.R;
import com.example.prova.activities.AddActivity;
import com.example.prova.entities.Prenotazione;
import com.example.prova.uiutilities.FireAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class FragmentPrenotazioni extends Fragment {

    FirebaseAuth nAuth = FirebaseAuth.getInstance();
    final FirebaseUser currentUser = nAuth.getCurrentUser();
    private static final int NEW_PRENOTATION = 301;
    private RecyclerView recyclerView;
    private ArrayList<Prenotazione> recensioni;
    private ImageButton btnAdd;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection(currentUser.getEmail()+"_Notebook");

    private FireAdapter adapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recensioni=new ArrayList<>();
        setUpRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_prenotazioni, container, false);
        recyclerView=view.findViewById(R.id.rv_prenotazioni);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        //gestore interazioni
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, // lo zero significa che non fa nulla con il drag and drop, ma solo con swipe left e right
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);


        btnAdd=view.findViewById(R.id.button_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), AddActivity.class), NEW_PRENOTATION);
                }

        });
        return view;
    }
    private void setUpRecyclerView() {
        Query query = notebookRef.orderBy("ora",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Prenotazione> options =
                new FirestoreRecyclerOptions.Builder<Prenotazione>()
                        .setQuery(query, Prenotazione.class).build();
        adapter=new FireAdapter(options);


    }



    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
