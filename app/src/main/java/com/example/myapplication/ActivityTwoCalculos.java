package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class ActivityTwoCalculos extends AppCompatActivity {


    private String tResultados;
    private TextView taxa;
    private EditText leituraInic,leituraFim,quantOl;
    private TextView total;
    private AppCompatButton botaoCalcular;
    private Float valorLitro, valorOleo, totalPagar, litros,
            leituraInicial, leituraFinal, quantOleo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        leituraInic = findViewById(R.id.editTextLeInic);
        leituraFim = findViewById(R.id.editTextLeFim);
        quantOl = findViewById(R.id.editTextQntOl);
        total = findViewById(R.id.textViewTotal);
        botaoCalcular = findViewById(R.id.buttonCalcular);
        botaoCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recebervalores();

                if((valorLitro!=null)&&((TextUtils.isEmpty(leituraInic.getText().toString())) ||
                        (TextUtils.isEmpty(leituraFim.getText().toString())))) {

                    Toast.makeText(ActivityTwoCalculos.this, "Preencha os campos", Toast.LENGTH_LONG).show();


                }else {

                    calculandoLitros();

                    calculandoValores();

                    resultados();

                }

            }
        });

    }

    public void recebervalores(){
        Intent intent = getIntent();
        if(TextUtils.isEmpty(intent.getStringExtra("valorOleo"))){
            valorLitro = Float.parseFloat(intent.getStringExtra("valorLitro"));

        } else if(TextUtils.isEmpty(intent.getStringExtra("valorLitro"))){
            valorOleo = Float.parseFloat(intent.getStringExtra("valorOleo"));
        } else {
            valorLitro = Float.parseFloat(intent.getStringExtra("valorLitro"));
            valorOleo = Float.parseFloat(intent.getStringExtra("valorOleo"));
        }

    }
    public float calculandoLitros(){
        recebervalores();

        if(valorLitro==null){

            return 0;
        } else {

            leituraInicial=Float.parseFloat(leituraInic.getText().toString());
            leituraFinal=Float.parseFloat(leituraFim.getText().toString());
            if(leituraInicial>=leituraFinal){
                Toast.makeText(ActivityTwoCalculos.this, "Leitura Inicial Maior que Final", Toast.LENGTH_LONG).show();
                return 0;
            } else {
                litros = ((leituraFinal-leituraInicial)/10);

            }

        }return litros;
    }
    public float calculandoValores(){
        recebervalores();

        if(TextUtils.isEmpty(quantOl.getText().toString())&&valorOleo==null){
            totalPagar = (litros*valorLitro);

        } else if(valorLitro==null) {
            quantOleo=Float.parseFloat(quantOl.getText().toString());
            totalPagar=quantOleo*valorOleo;

        } else{
            quantOleo=Float.parseFloat(quantOl.getText().toString());
            totalPagar = (litros*valorLitro);
            totalPagar+=quantOleo*valorOleo;

        }

        return totalPagar;

        }

        public String resultados(){
            calculandoValores();
            if ((valorLitro!=null)&&(valorOleo==null)){
                tResultados = "Valor do Litro R$: " + valorLitro;
                tResultados = tResultados + "\n" + litros + " L";
                tResultados = tResultados + "\n" + "Valor Total à pagar R$: " + totalPagar;
            } else if ((valorOleo!=null)&&(valorLitro==null)){
                tResultados = "Valor do Óleo R$: " + valorOleo;
                tResultados = tResultados + "\n" + quantOleo + " Óleo(s)";
                tResultados = tResultados + "\n" + "Valor Total à pagar R$: " + totalPagar;
            } else {
                tResultados = "Valor do Litro R$: " + valorLitro;
                tResultados = tResultados + "\n" + litros + " L";
                tResultados = tResultados + "\n" + " Valor do Óleo R$: " + valorOleo;
                tResultados = tResultados + "\n" + quantOleo + " Óleo(s)";
                tResultados = tResultados + "\n" + "Valor Total à pagar R$: " + totalPagar;
            }

            total.setText(tResultados.toString());
             return tResultados;
        }

}