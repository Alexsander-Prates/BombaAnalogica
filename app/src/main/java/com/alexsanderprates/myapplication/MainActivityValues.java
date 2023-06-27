package com.alexsanderprates.myapplication;



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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alexsanderprates.myapplication.R;
import com.alexsanderprates.myapplication.databinding.ActivityMainValuesBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;

import Util.ConfigBD;


public class MainActivityValues extends AppCompatActivity {


    ActivityMainValuesBinding binding;
    private EditText valorLitro,valorOleo;
    private AppCompatButton botaoProximo;
    private ProgressBar load;
    private CheckBox taxa;
    private ImageButton btnInfo;
    private String taxaAdm;

    private String [] mensagensErros = {"Faça Login"};

    FirebaseAuth autenticacaoLogin = ConfigBD.FirebaseAutentic();

    //firebaseauth esta retornado nullpointer - verificar na class


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainValuesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        iniciliazarLigacoes();

        incluirTaxa();





        botaoProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificadorIniciar();

            }
        });

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                informacao();

            }
        });


    }

    private void chamarTelaCalculos(){
        Intent intent = new Intent(getApplicationContext(),MainActivityCalculations.class);
        if(TextUtils.isEmpty(valorLitro.getText().toString())){
            intent.putExtra("valorOleo",valorOleo.getText().toString());
            valorLitro.setText("0");
            intent.putExtra("valorLitro",valorLitro.getText().toString());
        } else if (TextUtils.isEmpty(valorOleo.getText().toString())){
            intent.putExtra("valorLitro",valorLitro.getText().toString());
            valorOleo.setText("0");
            intent.putExtra("valorOleo",valorOleo.getText().toString());
        } else{
            intent.putExtra("valorOleo",valorOleo.getText().toString());
            intent.putExtra("valorLitro",valorLitro.getText().toString());
        }

        intent.putExtra("taxaAdm", taxaAdm);
        startActivity(intent);
        exibierProgres();
        finish();

    }

    private void verificadorIniciar(){
        if ((TextUtils.isEmpty(valorOleo.getText().toString()))&&
                (TextUtils.isEmpty(valorLitro.getText().toString()))) {
            Toast.makeText(MainActivityValues.this, "Preencha um dos campos", Toast.LENGTH_SHORT).show();
        }  else {

            chamarTelaCalculos();


        }

    }


    public void exibierProgres(){
        load.setVisibility(true ? View.VISIBLE: View.GONE);
    }



    private void iniciliazarLigacoes(){
        valorOleo = binding.editNumberValorOleo;
        valorLitro = binding.editNumberValorLitro;
        botaoProximo = binding.btnProximoGravar;
        load = binding.progresseBBar;
        taxa = binding.chekTaxaAdm;
        btnInfo= binding.informativoTaxa;

    }

    private String incluirTaxa(){
        String taxaNot="0";
        taxaAdm=taxaNot;
        String taxaSelected="15";

        taxa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){

                    taxaAdm=taxaSelected;

                    Toast.makeText(MainActivityValues.this, "Os valores serão calculados com a Taxa Administrativa", Toast.LENGTH_SHORT).show();

                } else {

                    taxaAdm=taxaNot;

                    Toast.makeText(MainActivityValues.this, "Os valores serão calculados sem a Taxa Administrativa", Toast.LENGTH_SHORT).show();

                }

            }
        });

        return taxaAdm;
    }

    public void informacao() {
        if ((TextUtils.isEmpty(valorOleo.getText().toString()))&&
                (TextUtils.isEmpty(valorLitro.getText().toString()))) {
            Toast.makeText(MainActivityValues.this, "Preencha um dos campos", Toast.LENGTH_SHORT).show();
        }  else {

            AlertDialog.Builder confirmarTaxa = new AlertDialog.Builder(this);
            confirmarTaxa.setTitle("Atenção");
            confirmarTaxa.setMessage("Você pode alterar o valor de 15% da Taxa Administrativa");
            confirmarTaxa.setCancelable(true);
            confirmarTaxa.setNegativeButton("Cancelar",null);
            EditText editarTaxa = new EditText(this);
            LinearLayout.LayoutParams linearLayout = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT
            );
            editarTaxa.setLayoutParams(linearLayout);
            confirmarTaxa.setView(editarTaxa);


            confirmarTaxa.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String taxaAdmInformativo = editarTaxa.getText().toString();
                    incluirTaxaAlterada(taxaAdmInformativo);
                }
            });
            confirmarTaxa.create().show();

        }

    }

    private void incluirTaxaAlterada(String taxaAdmInformativo) {

        Intent intent = new Intent(getApplicationContext(),MainActivityCalculations.class);
        if(TextUtils.isEmpty(valorLitro.getText().toString())){
            intent.putExtra("valorOleo",valorOleo.getText().toString());
            valorLitro.setText("0");
            intent.putExtra("valorLitro",valorLitro.getText().toString());
        } else if (TextUtils.isEmpty(valorOleo.getText().toString())){
            intent.putExtra("valorLitro",valorLitro.getText().toString());
            valorOleo.setText("0");
            intent.putExtra("valorOleo",valorOleo.getText().toString());
        } else{
            intent.putExtra("valorOleo",valorOleo.getText().toString());
            intent.putExtra("valorLitro",valorLitro.getText().toString());
        }


        intent.putExtra("taxaAdm",taxaAdmInformativo);
        exibierProgres();
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_cadastros, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(autenticacaoLogin.getCurrentUser() ==null){
            Toast.makeText(MainActivityValues.this, mensagensErros[0], Toast.LENGTH_SHORT).show();
        } else{
            switch (item.getItemId()){

                case R.id.itemCadastrar:
                    Intent intent2 = new Intent(getApplicationContext(), MainActivityCadastrar.class);
                    startActivity(intent2);
                    break;
                case R.id.itemExcluir:
                    Intent intent3= new Intent(getApplicationContext(), MainActivityExcluir.class);
                    startActivity(intent3);
                    break;
            }


        }

        return super.onOptionsItemSelected(item);


    }


}