package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.myapplication.databinding.ActivityMainHistoricsBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;

import java.util.ArrayList;

import Util.AdapterH;
import Util.ConfigBD;
import model.Valores;

public class MainActivityHistorics extends AppCompatActivity {

    FirebaseFirestore autenticacaoUserBD = ConfigBD.FirebaseCadastroUser();
    CollectionReference historicsAutos = autenticacaoUserBD.collection("HistoricoValores");

    private SwipeableRecyclerView listaDeValores;
    ArrayList<Valores> valoresArrayList;
    AdapterH adapterH;
    private TextView nameAuto;

    private ActivityMainHistoricsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainHistoricsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciarLigacoes();
        listaDeValores.setHasFixedSize(true);
        listaDeValores.setLayoutManager(new LinearLayoutManager(this));
        valoresArrayList = new ArrayList<>();
        adapterH = new AdapterH(MainActivityHistorics.this,valoresArrayList);
        listaDeValores.setAdapter(adapterH);


        iniciarListener();


    }

    private void iniciarLigacoes(){
        listaDeValores = binding.recyclerListHistoric;
        nameAuto = binding.nomeHistoric;


    }

    private void iniciarListener() {
        valoresArrayList.clear();
        String id = getIntent().getStringExtra("id");
        String nomeAuto = getIntent().getStringExtra("nomeAuto");
        nameAuto.setText(nomeAuto);

        historicsAutos.whereEqualTo("idCarro",id)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(error != null){

                            Log.d("Erro Bando de Dados Firebase Firestore",error.getMessage());

                            return;
                        }


                        for(DocumentChange dc: value.getDocumentChanges()){


                            if(dc.getType() == DocumentChange.Type.ADDED){

                                valoresArrayList.add(dc.getDocument().toObject(Valores.class));



                            }

                            adapterH.notifyDataSetChanged();

                        }
                    }
                });

    }

}