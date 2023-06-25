package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityMainCalculationsBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.DecimalFormat;


public class MainActivityCalculations extends AppCompatActivity {

    private ActivityMainCalculationsBinding binding;
    private String tResultados;
    private EditText leituraInic, leituraFim, quantOl;
    private TextView total;
    private AppCompatButton botaoCalcular;
    private Float valorLitro, valorOleo, totalPagar, litros,
            leituraInicial, leituraFinal, quantOleo, taxaAdm;
    float valorGambiarra = 0;
    DecimalFormat df = new DecimalFormat("0.00");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainCalculationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        valorLitro = Float.parseFloat(getIntent().getStringExtra("valorLitro"));
        valorOleo = Float.parseFloat(getIntent().getStringExtra("valorOleo"));
        taxaAdm = Float.parseFloat(getIntent().getStringExtra("taxaAdm"));

        inicialicarLigacoes();


        if (valorLitro == 0) {
            Toast.makeText(MainActivityCalculations.this, "Preencha os campos quantidade de Óleo", Toast.LENGTH_LONG).show();
            leituraInic.setEnabled(false);leituraFim.setEnabled(false);
            leituraInic.setText("0");
            leituraFim.setText("0");
        } else if (valorOleo==0) {
            Toast.makeText(MainActivityCalculations.this, "Preencha os campos Leituras Inicial e Final", Toast.LENGTH_LONG).show();
            quantOl.setEnabled(false);
            quantOl.setText("0");
        } else {
            Toast.makeText(MainActivityCalculations.this, "Preencha os campos das Leituras e Óleo", Toast.LENGTH_LONG).show();
        }

        botaoCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    calculandoLitros();

                    calculandoValores();

                    resultados();

                    levarTelaResults();

                }catch (NullPointerException nu){
                    totalPagar=valorGambiarra;
                    return;
                }

            }
        });

    }


    //colocar esses verificadores dentro do oncreat
    public void calculandoLitros() {

        try{

            if((TextUtils.isEmpty(leituraInic.getText().toString()) && (TextUtils.isEmpty(leituraFim.getText().toString())))){
                Toast.makeText(MainActivityCalculations.this, "Preencha os campos de leitura", Toast.LENGTH_LONG).show();

            }else if((leituraInic.getText().toString()!=null) && (leituraFim.getText().toString()!=null)){
                leituraInicial = Float.parseFloat(leituraInic.getText().toString());
                leituraFinal = Float.parseFloat(leituraFim.getText().toString());

                if (leituraFinal <= leituraInicial) {
                    Toast.makeText(MainActivityCalculations.this, "Leitura Inicial Maior que Final", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    litros = ((leituraFinal - leituraInicial) / 10);
                }

            } else {
                leituraInic.setText("0");
                leituraFim.setText("0");
                Toast.makeText(MainActivityCalculations.this, "Digite a quantidade de Óleos", Toast.LENGTH_LONG).show();

            }


        }catch (NumberFormatException e){
            Toast.makeText(MainActivityCalculations.this, "tenete preencher os dados novamente", Toast.LENGTH_LONG).show();
        }


    }

    public float calculandoValores() {

        try {

            taxaAdm=taxaAdm/100;

            if (valorOleo==0) {
                totalPagar = (litros * valorLitro);
                quantOleo=valorGambiarra;

            } else if (valorLitro==0) {
                quantOleo = Float.parseFloat(quantOl.getText().toString());
                totalPagar = quantOleo * valorOleo;
                litros=valorGambiarra;

            } else {
                quantOleo = Float.parseFloat(quantOl.getText().toString());
                totalPagar = (litros * valorLitro);
                totalPagar += quantOleo * valorOleo;
            }



        }catch (NumberFormatException e){
            Toast.makeText(MainActivityCalculations.this, "Preencha os dados novamente", Toast.LENGTH_LONG).show();
        }


        return totalPagar+=taxaAdm*totalPagar;
    }

    public String resultados() {

        df.format(totalPagar);

        if ((valorLitro != null) && (valorOleo == 0)) {
            tResultados = "   Valor do Litro R$: " + valorLitro;
            tResultados = tResultados + "   \n  " + litros + "Litros";
            tResultados = tResultados + "   \n" + "   Valor Total à pagar R$: " + totalPagar;

        } else if ((valorOleo != null) && (valorLitro == 0)) {
            tResultados = "   Valor do Óleo R$: " + valorOleo;
            tResultados = tResultados + "   \n  " + quantOleo + "Óleo(s)";
            tResultados = tResultados + "   \n" + "   Valor Total à pagar R$: " + totalPagar;

        } else {
            tResultados = "   Valor do Litro R$: " + valorLitro;
            tResultados = tResultados + "    \n  " + litros + " Litros";
            tResultados = tResultados + "   \n" + "   Valor do Óleo R$: " + valorOleo;
            tResultados = tResultados + "    \n   " + quantOleo + " Óleo(s)";
            tResultados = tResultados + "   \n" + "   Valor Total à pagar R$:" + totalPagar;

        }
        total.setText(tResultados);
        return tResultados;



    }



    private void inicialicarLigacoes() {
        leituraInic = binding.editTextLeInic;
        leituraFim = binding.editTextLeFim;
        quantOl = binding.editTextQntOl;
        total = binding.textViewTotal;
        botaoCalcular = binding.buttonCalcular;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_vincular, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.itemVincular:
                try {
                    levarTelaResults();
                }catch (NullPointerException e){



                }Toast.makeText(MainActivityCalculations.this, "Preencha os dados novamente", Toast.LENGTH_LONG).show();


                break;

        }

        return super.onOptionsItemSelected(item);
    }


    private void levarTelaResults() {

        AlertDialog.Builder confirmarIncluir = new AlertDialog.Builder(this);
        confirmarIncluir.setTitle("Atenção");
        confirmarIncluir.setMessage("Deseja vincular valores ao auto?");
        confirmarIncluir.setCancelable(false);
        confirmarIncluir.setNegativeButton("Cancelar", null);

        confirmarIncluir.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(totalPagar!=0){
                    Intent intent2 = new Intent(getApplicationContext(),MainActivityResults.class);
                    intent2.putExtra("valorLitro",valorLitro.toString());
                    intent2.putExtra("valorOleo",valorOleo.toString());
                    intent2.putExtra("valorTotal",totalPagar.toString());
                    intent2.putExtra("mensagem",tResultados);
                    intent2.putExtra("litros",litros.toString());
                    intent2.putExtra("quantO",quantOleo.toString());
                    intent2.putExtra("taxaAdm",taxaAdm.toString());
                    startActivity(intent2);

                } else{
                    Toast.makeText(MainActivityCalculations.this, "Calcule as quantidades", Toast.LENGTH_LONG).show();
                }

            }
        });
        confirmarIncluir.create().show();
    }

}