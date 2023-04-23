package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityMainResultsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import Util.Adapter;
import Util.ConfigBD;
import model.Autos;
import model.Valores;


public class MainActivityResults extends AppCompatActivity {

    FirebaseFirestore autenticacaoUserBD = ConfigBD.FirebaseCadastroUser();
    CollectionReference meusAutos = autenticacaoUserBD.collection("AutosCadastro");


    private ActivityMainResultsBinding binding;
    ArrayList<Autos> userArrayList;
    Adapter adapter;
    private SwipeableRecyclerView listaDeItens;
    private TextView valorLitro, valorOleo,totalPagar, tTaxa;
    double taxa;
    ImageButton info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainResultsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        inicilializarLigacoes();


        listaDeItens.setHasFixedSize(true);
        listaDeItens.setLayoutManager(new LinearLayoutManager(this));
        userArrayList = new ArrayList<>();
        adapter = new Adapter(MainActivityResults.this,userArrayList);
        listaDeItens.setAdapter(adapter);

        iniciarListener();
        receberValores();
        swipDatas();

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                informacao();

            }
        });





    }

    private void iniciarListener() {
        userArrayList.clear();
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
    private void swipDatas(){

        listaDeItens.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {
                salvarValoresAuto(position);
                adapter.notifyDataSetChanged();
                salvarValoresAuto(position);
                //abilitar botao enviar e pintar de verde


            }

            @Override
            public void onSwipedRight(int position) {
                salvarValoresAuto(position);
                adapter.notifyDataSetChanged();
                salvarValoresAuto(position);
                //abilitar botao enviar e pintar de verde
            }
        });
    }

    private void inicilializarLigacoes() {
        listaDeItens = binding.recListItens;
        valorLitro = binding.recebeGasolina;
        valorOleo = binding.recebeOleo;
        totalPagar = binding.textViewValorCombustivel;
        tTaxa = binding.recebeTaxa;
        info= binding.informativo;
    }

    public void receberValores(){
        String valorLitroR = getIntent().getStringExtra("valorLitro");
        String valorOleoR = getIntent().getStringExtra("valorOleo");
        String valorTotalPagar = getIntent().getStringExtra("valorTotal");

        valorLitro.setText(valorLitroR);
        valorOleo.setText(valorOleoR);
        totalPagar.setText(valorTotalPagar);
        taxa = (Float.parseFloat(totalPagar.getText().toString())*0.15);
        tTaxa.setText(String.valueOf(taxa));
    }

    public void informacao(){
        AlertDialog.Builder confirmarIncluir = new AlertDialog.Builder(this);
        confirmarIncluir.setTitle("Atenção");
        confirmarIncluir.setMessage("Para vincular os valores ao auto será necessário selecionar" +
                "selecionar o auto puxando da direita para esquerda e" +
                "clicar em vincular valores. Caso queira enviar ao cliente, após vincular o valor" +
                "você deverá clicar no botão enviar.");
        confirmarIncluir.setCancelable(false);
        confirmarIncluir.setPositiveButton("Ok",null);
        confirmarIncluir.create().show();

    }

    public int salvarValoresAuto(int index){
        String valorLitroS = getIntent().getStringExtra("valorLitro");
        String valorOleoS = getIntent().getStringExtra("valorOleo");
        //String valorTotalPagarS = getIntent().getStringExtra("valorTotal");
        String litros = getIntent().getStringExtra("litros");
        String quantO = getIntent().getStringExtra("quantO");
        String tResultados = getIntent().getStringExtra("tResultados");

        AlertDialog.Builder confirmarIncluir = new AlertDialog.Builder(this);
        confirmarIncluir.setTitle("Atenção");
        confirmarIncluir.setMessage("Você desejar salvar os valores -> " + tResultados + " no auto selecionado?" +
                "Clique em Ok para continuar ou Não para cancelar" );
        confirmarIncluir.setCancelable(false);
        confirmarIncluir.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String taxaS = String.valueOf(taxa);
                Valores valores = new Valores(valorLitroS,valorOleoS,taxaS,litros,quantO,tResultados);
                Date dataAtual = new Date();


                Map<String,Object> autoValores = new HashMap<>();
                String nome = userArrayList.get(index).getAuto();
                String idValor = userArrayList.get(index).getId();
                autoValores.put("ValorLitro",valores.getValorGasolina());
                autoValores.put("ValorOleo",valores.getValorOleo());
                autoValores.put("Taxa",valores.getValorTaxa());
                autoValores.put("Litros",valores.getQuantidadeL());
                autoValores.put("QuantidadeOleo",valores.getQuantidadeO());
                autoValores.put("Mensagem",valores.getMensagem());
                autoValores.put("Data", dataAtual.toString());
                autoValores.put("NomeAlto", nome);
                autoValores.put("idCarro", idValor);

                DocumentReference documentReferenceValor = autenticacaoUserBD.collection("HistoricoValores").document(dataAtual.toString());
                documentReferenceValor.set(autoValores).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("db","Salvado com sucesso");
                        Toast.makeText(MainActivityResults.this, "Cadastro de Valores realizado com sucesso", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("db","Não salvou");
                        Toast.makeText(MainActivityResults.this, "Cadastro nao realizado", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        confirmarIncluir.create().show();

        return index;

    }
}