package com.alexsanderprates.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;


import com.alexsanderprates.myapplication.databinding.ActivityMainExcluirBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;

import java.util.ArrayList;

import Util.Adapter;
import Util.ConfigBD;
import model.Autos;

public class MainActivityExcluir extends AppCompatActivity {

    FirebaseAuth autenticacaoAuth = ConfigBD.FirebaseAutentic();

    FirebaseFirestore autenticacaoUserBD = ConfigBD.FirebaseCadastroUser();
    CollectionReference meusAutos = autenticacaoUserBD.collection("Autos - " + autenticacaoAuth.getCurrentUser().getEmail());
    CollectionReference meusHistorics = autenticacaoUserBD.collection("HistoricoValores");

    FirebaseStorage storage = ConfigBD.FirebaseCadastroStorage();
    StorageReference storageReference = storage.getReference();




    private SwipeableRecyclerView listaDeItens;
    ArrayList<Autos> userArrayList;
    Adapter adapter;
    private AppCompatButton btShowCadastros;



    private ActivityMainExcluirBinding binding;
    private ImageButton informativo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainExcluirBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        inicilializarLigacoes();


        listaDeItens.setHasFixedSize(true);
        listaDeItens.setLayoutManager(new LinearLayoutManager(this));
        userArrayList = new ArrayList<>();
        adapter = new Adapter(MainActivityExcluir.this,userArrayList);
        listaDeItens.setAdapter(adapter);

        iniciarListener();

        swipDatas();

        irShowCadastros();



        informativo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info();
            }
        });



    }


    private void iniciarListener() {
        userArrayList.clear();

        meusAutos.whereEqualTo("adm",autenticacaoAuth.getCurrentUser().getUid())
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

    private void swipDatas(){

        listaDeItens.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {
                deletDados(position);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onSwipedRight(int position) {
                alterarDados(position);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void alterarDados(int index) {

        AlertDialog.Builder confirmarExcluir = new AlertDialog.Builder(this);
        confirmarExcluir.setTitle("Atenção" + userArrayList.get(index).getAuto());
        confirmarExcluir.setMessage("Deseja alterar" +userArrayList.get(index).getAuto() + "?");
        confirmarExcluir.setCancelable(false);
        confirmarExcluir.setNegativeButton("Cancelar",null);
        confirmarExcluir.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String nome = userArrayList.get(index).getAuto();
                String desc = userArrayList.get(index).getDesc();
                String clube = userArrayList.get(index).getClube();
                String outros = userArrayList.get(index).getOutros();
                String photo = userArrayList.get(index).getPhoto();
                String photoKey = userArrayList.get(index).getPhotoKey();
                String id = userArrayList.get(index).getId();


                Intent intent = new Intent(getApplicationContext(), MainActivityCadastrar.class);
                intent.putExtra("pNome", nome);
                intent.putExtra("pDesc", desc);
                intent.putExtra("pPhoto",photo);
                intent.putExtra("pPhotoKey",photoKey);
                intent.putExtra("pId",id);


                if(TextUtils.isEmpty(userArrayList.get(index).getClube())){
                    intent.putExtra("pOutros", outros);
                } else {
                    intent.putExtra("pClube", clube);
                }

                startActivity(intent);

            }
        });
        confirmarExcluir.create().show();

    }


    private void inicilializarLigacoes(){
        listaDeItens = binding.recyclerListItens;
        btShowCadastros = binding.btnExcluir;
        informativo = binding.informativo;

    }


    private void deletDados(int index) {
        String auto = userArrayList.get(index).getAuto();
        String idCarro = userArrayList.get(index).getId();
        AlertDialog.Builder confirmarExcluir = new AlertDialog.Builder(this);
        confirmarExcluir.setTitle("Atenção");
        confirmarExcluir.setMessage("Deseja excluir?" + auto);
        confirmarExcluir.setCancelable(false);
        confirmarExcluir.setNegativeButton("Cancelar",null);
        confirmarExcluir.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                meusAutos.document(idCarro).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                excluirPhotoAndHistoric(index,idCarro);
                                userArrayList.remove(index);
                                adapter.notifyDataSetChanged();

                                iniciarListener();
                                Log.d("DB_DELETE","Excluido com sucesso");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("DB_DELETE","Erro ao excluir");
                            }
                        });
            }
        });
        confirmarExcluir.create().show();


    }


    private void irShowCadastros(){
        btShowCadastros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivityCadastrar.class);
                startActivity(intent);
            }
        });
    }



    private void excluirPhotoAndHistoric(int index,String idCarro){
        String atalho = userArrayList.get(index).getPhotoKey();

        String nome = autenticacaoAuth.getCurrentUser().getEmail() + "/";
        String teste = "images ";
        String nomePastaPhoto = teste + nome;

        StorageReference storageRef = storageReference.child(nomePastaPhoto + atalho);
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("StoreDelet","Foi deletado");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("StoreDelet","Não Foi deletado");
            }
        });
        meusHistorics.whereEqualTo("idCarro",idCarro).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document:task.getResult()){
                        meusHistorics.document(document.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("Historic","Foi deletado historio");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Historic","Não Foi deletado historio");
                            }
                        });
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void info(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Atenção");
        alertDialog.setMessage("Para excluir, arraste o cartão do veículo desejado para a esquerda." +
                " Para editar, arraste-o para a direita.");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Ok",null);
        alertDialog.show();
    }
}