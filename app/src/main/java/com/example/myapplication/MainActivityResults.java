package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Date;

import Util.Adapter;
import Util.ConfigBD;
import Util.PDF;
import model.Autos;
import model.Valores;


public class MainActivityResults extends AppCompatActivity {

    FirebaseAuth autenticacaoAuth = ConfigBD.FirebaseAutentic();

    FirebaseFirestore autenticacaoUserBD = ConfigBD.FirebaseCadastroUser();
    CollectionReference meusAutos = autenticacaoUserBD.collection("Autos - " + autenticacaoAuth.getCurrentUser().getEmail());

    FirebaseFirestore autenticacaoHistoric = ConfigBD.FirebaseCadastroUser();




    private ActivityMainResultsBinding binding;
    ArrayList<Autos> userArrayList;
    Adapter adapter;
    private SwipeableRecyclerView listaDeItens;
    private TextView valorLitro, valorOleo, totalPagar, tTaxa;
    private Float taxa;
    ImageButton info;
    DecimalFormat df = new DecimalFormat("R$ #,##0.00");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainResultsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        inicilializarLigacoes();


        listaDeItens.setHasFixedSize(true);
        listaDeItens.setLayoutManager(new LinearLayoutManager(this));
        userArrayList = new ArrayList<>();
        adapter = new Adapter(MainActivityResults.this, userArrayList);
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
        meusAutos.whereEqualTo("adm",autenticacaoAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            Log.d("Erro Bando de Dados Firebase Firestore", error.getMessage());

                            return;
                        }


                        for (DocumentChange dc : value.getDocumentChanges()) {


                            if (dc.getType() == DocumentChange.Type.ADDED) {

                                userArrayList.add(dc.getDocument().toObject(Autos.class));


                            }

                            adapter.notifyDataSetChanged();

                        }
                    }
                });

    }

    private void swipDatas() {

        listaDeItens.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {
                salvarValoresAuto(position);
                adapter.notifyDataSetChanged();

                //abilitar botao enviar e pintar de verde


            }

            @Override
            public void onSwipedRight(int position) {


            }
        });
    }

    private void inicilializarLigacoes() {
        listaDeItens = binding.recListItens;
        valorLitro = binding.recebeGasolina;
        valorOleo = binding.recebeOleo;
        totalPagar = binding.textViewValorCombustivel;
        tTaxa = binding.recebeTaxa;
        info = binding.informativo;
    }

    public void receberValores() {
        String valorLitroR = getIntent().getStringExtra("valorLitro");
        String valorOleoR = getIntent().getStringExtra("valorOleo");
        String valorTotalPagar = getIntent().getStringExtra("valorTotal");
        String taxaAdm = getIntent().getStringExtra("taxaAdm");

        String totalFormat = df.format(Float.parseFloat(valorTotalPagar));

        valorLitro.setText(valorLitroR);
        valorOleo.setText(valorOleoR);
        totalPagar.setText(totalFormat);
        taxa = Float.parseFloat(taxaAdm)*100;
        tTaxa.setText(String.valueOf(taxa));
    }

    public void informacao() {
        AlertDialog.Builder confirmarIncluir = new AlertDialog.Builder(this);
        confirmarIncluir.setTitle("Atenção");
        confirmarIncluir.setMessage("Para vincular os valores ao auto será necessário selecionar" +
                " o auto puxando da direita para esquerda e " +
                "clicar em vincular valores. Caso queira enviar ao cliente, após vincular o valor" +
                " você pode acessar seus Arquivos - Download.");
        confirmarIncluir.setCancelable(false);
        confirmarIncluir.setPositiveButton("Ok", null);
        confirmarIncluir.create().show();

    }

    public int salvarValoresAuto(int index) {
        String valorLitroS = getIntent().getStringExtra("valorLitro");
        String valorOleoS = getIntent().getStringExtra("valorOleo");
        String valorTotalPagarS = df.format(Float.parseFloat(getIntent().getStringExtra("valorTotal")));

        String litros = getIntent().getStringExtra("litros");
        String quantO = getIntent().getStringExtra("quantO");

        String mensagemDesformatar = getIntent().getStringExtra("mensagem");
        String mensagemTirarEspaco = mensagemDesformatar.replace("\n","");
        String mensagem = mensagemTirarEspaco.replace("   ","");

        AlertDialog.Builder confirmarSalvar = new AlertDialog.Builder(this);
        confirmarSalvar.setTitle("Atenção");
        confirmarSalvar.setMessage("Você desejar salvar os valores -> " + mensagem + " no auto selecionado?" +
                "Clique em Ok para continuar ou Não para cancelar");
        confirmarSalvar.setCancelable(false);
        confirmarSalvar.setNegativeButton("Cancelar", null);
        confirmarSalvar.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String taxaS = (df.format(taxa));
                Date dataAtual = new Date();

                SimpleDateFormat formatoData = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy 'às' HH: mm: ss", new Locale("pt", "BR"));
                String dateFim = formatoData.format(dataAtual);
                String date = dateFim;

                Valores valores = new Valores(date, valorTotalPagarS,valorLitroS, valorOleoS, taxaS,
                        litros, quantO, mensagem);


                Map<String, Object> autoValores = new HashMap<>();
                String nome = userArrayList.get(index).getAuto();
                String idValor = userArrayList.get(index).getId();
                String descAuto = userArrayList.get(index).getDesc();
                autoValores.put("ValorLitro", valores.getValorGasolina());
                autoValores.put("ValorOleo", valores.getValorOleo());
                autoValores.put("Taxa", valores.getValorTaxa());
                autoValores.put("Litros", valores.getQuantidadeL());
                autoValores.put("QuantidadeOleo", valores.getQuantidadeO());
                autoValores.put("mensagem", mensagem);
                autoValores.put("date", date);
                autoValores.put("NomeAuto", nome);
                autoValores.put("idCarro", idValor);
                autoValores.put("valorTotal", valorTotalPagarS);

                criarPDF(descAuto,valorTotalPagarS,dataAtual,nome,valorLitroS,valorOleoS,taxaS,litros,quantO,mensagem);




                DocumentReference documentReferenceValor = autenticacaoUserBD.collection("HistoricoValores").document(nome + " - " + date);
                documentReferenceValor.set(autoValores).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("db", "Salvado com sucesso");
                        Toast.makeText(MainActivityResults.this, "Cadastro de Valores realizado com sucesso", Toast.LENGTH_SHORT).show();

                        //enviarValoresWhats();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("db", "Não salvou");
                        Toast.makeText(MainActivityResults.this, "Cadastro nao realizado", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });confirmarSalvar.create().show();

        return index;

    }

    public void criarPDF(String descAuto,String valorTotalPagarS,Date dataAtual,String nome,String valorLitroS,String valorOleoS,String taxaS,String litros,String quantO,String mensagem){

        AlertDialog.Builder confirmarPDF = new AlertDialog.Builder(this);
        confirmarPDF.setTitle("Atenção");
        confirmarPDF.setMessage("Você deseja criar um PDF com os resultados?" +
                "Clique em Ok para continuar ou Não para cancelar");
        confirmarPDF.setCancelable(false);
        confirmarPDF.setNegativeButton("Cancelar", null);
        confirmarPDF.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PDF pdf = new PDF();
                pdf.gerarPDF(descAuto, valorTotalPagarS,dataAtual, nome,valorLitroS, valorOleoS, taxaS, litros, quantO, mensagem);
            }
        });

        confirmarPDF.create().show();
    }



    /*private void enviarValoresWhats() {
        AlertDialog.Builder inserirPhone = new AlertDialog.Builder(this);
        inserirPhone.setTitle("Atenção");
        inserirPhone.setMessage("Vocẽ deseja enviar o PDF pelo Whatsapp? Favor, insirá uma número");
        inserirPhone.setCancelable(false);
        inserirPhone.setNegativeButton("Cancelar", null);
        EditText phone = new EditText(this);
        LinearLayout.LayoutParams linearLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT
        );
        phone.setLayoutParams(linearLayout);
        inserirPhone.setView(phone);


        inserirPhone.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pdfUri=getIntent().getData();
                String url = "https://api.whatsapp.w4b.com/send?phone ="; //+dialog com camp pegar numero
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.putExtra(Intent.EXTRA_STREAM,pdfUri);
                intent.setType("application/pdf");
                intent.setData(Uri.parse(url));
                startActivity(intent);



            }
        });

        inserirPhone.create().show();
    }*/


}