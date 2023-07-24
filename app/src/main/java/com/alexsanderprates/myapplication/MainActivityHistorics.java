package com.alexsanderprates.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.alexsanderprates.myapplication.databinding.ActivityMainHistoricsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import Util.AdapterH;
import Util.ConfigBD;
import model.Autos;
import model.Valores;

public class MainActivityHistorics extends AppCompatActivity {

    FirebaseFirestore autenticacaoUserBD = ConfigBD.FirebaseCadastroUser();
    CollectionReference historicsAutos = autenticacaoUserBD.collection("HistoricoValores");

    private SwipeableRecyclerView listaDeValores;
    ArrayList<Valores> valoresArrayList;
    ArrayList<Autos> userArrayList;
    AdapterH adapterH;
    private TextView nameAuto,valorAuto;


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
        swipData();
        calcularValorTotal();


    }

    private void iniciarLigacoes(){
        listaDeValores = binding.recyclerListHistoric;
        nameAuto = binding.nomeHistoric;
        valorAuto = binding.valorHistoric;


    }

    private void iniciarListener() {
        String id = getIntent().getStringExtra("id");
        String nomeAuto = getIntent().getStringExtra("nomeAuto");
        nameAuto.setText(nomeAuto);

        valoresArrayList.clear();
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

    private void calcularValorTotal() {
        String id = getIntent().getStringExtra("id");
        final double[] total = {0};

        historicsAutos.whereEqualTo("idCarro", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.contains("valorTotal")) {
                            String valorTotalString = document.getString("valorTotal");
                            String valorNumericoString = valorTotalString.substring(3); // Remove "R$ " do início da string
                            valorNumericoString = valorNumericoString.replace(",", "."); // Substitui a vírgula por ponto
                            try {
                                double valorTotal = Double.parseDouble(valorNumericoString);
                                total[0] += valorTotal;
                            } catch (NumberFormatException n) {
                                // Lidar com a exceção, se necessário
                            }
                        }
                    }
                    DecimalFormat df = new DecimalFormat("0.00");
                    String totalFormatado = df.format(total[0]); // Formata o valor total como uma string com 2 casas decimais
                    valorAuto.setText(totalFormatado);
                } else {
                    // Lidar com falha na obtenção dos documentos, se necessário
                }
            }
        });
    }


    private void swipData(){

        listaDeValores.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {
                deleteDadosHistoric(position);
                adapterH.notifyDataSetChanged();
            }

            @Override
            public void onSwipedRight(int position) {
                adapterH.notifyDataSetChanged();
            }
        });
    }

    private void deleteDadosHistoric(int index) {
        String nomeAuto = getIntent().getStringExtra("nomeAuto");
        String date = valoresArrayList.get(index).getDate();
        AlertDialog.Builder confirmarExcluir = new AlertDialog.Builder(this);
        confirmarExcluir.setTitle("Atenção");
        confirmarExcluir.setMessage("Deseja excluir o historico?" + date);
        confirmarExcluir.setCancelable(false);
        confirmarExcluir.setNegativeButton("Cancelar", null);
        confirmarExcluir.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                historicsAutos.document(nomeAuto + " - " + date).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        valoresArrayList.remove(index);
                        adapterH.notifyDataSetChanged();
                        Log.d("DB_DELETE","Historico excluido com sucesso");
                    }
                });
            }
        });
        confirmarExcluir.create().show();
    }


}