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



public class MainActivityCalculations extends AppCompatActivity {

    private ActivityMainCalculationsBinding binding;
    private String tResultados;
    private EditText leituraInic, leituraFim, quantOl;
    private TextView total;
    private AppCompatButton botaoCalcular;
    private Float valorLitro, valorOleo, totalPagar, litros,
            leituraInicial, leituraFinal, quantOleo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainCalculationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        valorLitro = Float.parseFloat(getIntent().getStringExtra("valorLitro"));
        valorOleo = Float.parseFloat(getIntent().getStringExtra("valorOleo"));

        inicialicarLigacoes();

        if (valorLitro == 0) {
            Toast.makeText(MainActivityCalculations.this, "Preencha os campos quantidade de Óleo", Toast.LENGTH_LONG).show();
            leituraInic.setEnabled(false);leituraFim.setEnabled(false);
            leituraInic.setText("0");
            leituraInic.getText().toString();
            leituraFim.setText("0");
            leituraFim.getText().toString();
        } else if (valorOleo==0) {
            Toast.makeText(MainActivityCalculations.this, "Preencha os campos Leituras Inicial e Final", Toast.LENGTH_LONG).show();
            quantOl.setEnabled(false);
            quantOl.setText("0");
            quantOl.getText().toString();
        } else {
            Toast.makeText(MainActivityCalculations.this, "Preencha os campos das Leituras e Óleo", Toast.LENGTH_LONG).show();
        }

        botaoCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                calculandoLitros();

                calculandoValores();

                resultados();

                levarTelaResults();


            }
        });

    }


    //colocar esses verificadores dentro do oncreat
    public void calculandoLitros() {
        if(leituraInic!=null || leituraFim!=null){
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

    }

    public float calculandoValores() {
        float valorGambiarra = 0;

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

        return totalPagar;

    }

    public String resultados() {
        calculandoValores();
        if ((valorLitro != null) && (valorOleo == 0)) {
            tResultados = "   Valor do Litro R$: " + valorLitro;
            tResultados = tResultados + "   \n  " + litros + "   L";
            tResultados = tResultados + "   \n" + "   Valor Total à pagar R$: " + totalPagar;

        } else if ((valorOleo != null) && (valorLitro == 0)) {
            tResultados = "   Valor do Óleo R$: " + valorOleo;
            tResultados = tResultados + "   \n  " + quantOleo + "   Óleo(s)";
            tResultados = tResultados + "   \n" + "   Valor Total à pagar R$: " + totalPagar;

        } else {
            tResultados = "   Valor do Litro R$: " + valorLitro;
            tResultados = tResultados + "   \n  " + litros + "   L";
            tResultados = tResultados + "   \n" + "   Valor do Óleo R$: " + valorOleo;
            tResultados = tResultados + "   \n  " + quantOleo + "   Óleo(s)";
            tResultados = tResultados + "   \n" + "   Valor Total à pagar R$: " + totalPagar;

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

                levarTelaResults();

                break;

        }

        return super.onOptionsItemSelected(item);
    }


    private void levarTelaResults() {

        AlertDialog.Builder confirmarIncluir = new AlertDialog.Builder(this);
        confirmarIncluir.setTitle("Atenção");
        confirmarIncluir.setMessage("Deseja incular valores?");
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
                    intent2.putExtra("tResultados",tResultados);
                    intent2.putExtra("litros",litros.toString());
                    intent2.putExtra("litros",litros.toString());
                    intent2.putExtra("quantO",quantOleo.toString());
                    startActivity(intent2);


                } else{
                    Toast.makeText(MainActivityCalculations.this, "Calcule as quantidades", Toast.LENGTH_LONG).show();
                }
            }
        });
        confirmarIncluir.create().show();
    }

}