package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;


import com.example.myapplication.databinding.ActivityMainExcluirBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import Util.Adapter;
import Util.ConfigBD;
import model.Autos;

public class MainActivityExcluir extends AppCompatActivity {

    private FirebaseFirestore autenticacaoUserBD = ConfigBD.FirebaseCadastroUser();
    private CollectionReference meusAutos = autenticacaoUserBD.collection("AutosCadastro");


    private RecyclerView listaDeItens;
    private ArrayList<Autos> userArrayList;
    Adapter adapter;



    private ActivityMainExcluirBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainExcluirBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        inicilializarLigacoes();


        listaDeItens.setHasFixedSize(true);
        listaDeItens.setLayoutManager(new LinearLayoutManager(this));

        userArrayList = new ArrayList<Autos>();
        adapter = new Adapter(MainActivityExcluir.this,userArrayList);

        listaDeItens.setAdapter(adapter);
        iniciarListener();


    }

    private void iniciarListener() {
        meusAutos.orderBy("auto", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error != null){

                    Log.d("Erro Bando de Dados Firebase Firestore",error.getMessage());

                    return;
                }

                for(DocumentChange dc: value.getDocumentChanges()){

                    if(dc.getType() == DocumentChange.Type.ADDED){

                        userArrayList.add(dc.getDocument().toObject(Autos.class));
                    }

                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


    private void inicilializarLigacoes(){
        listaDeItens = binding.recyclerListItens;

    }

    /*private void iniciarListaAutos(){
        meusAutos.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentChange doc: value.getDocumentChanges()){
                    switch (doc.getType()){
                        case ADDED:
                            Log.d("dyww","ADDED" + doc.getDocument().getData().get("auto"));
                            break;
                        case MODIFIED:
                            Log.d("dyww","MODIFIED" + doc.getDocument().getData().get("auto"));
                            break;
                        case REMOVED:
                            Log.d("dyww","REMOVED" + doc.getDocument().getData().get("auto"));
                            break;
                    }
                }


            }
        });
    }*/
}