package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityMainCadastrarBinding;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import Util.ConfigBD;
import model.Autos;

public class MainActivityCadastrar extends AppCompatActivity {

    private ActivityMainCadastrarBinding binding;
    private FirebaseFirestore autenticacaoUserBD;
    private Autos autos;
    private EditText cadastrar;
    private RadioGroup radioGroup;
    private RadioButton radioClube;
    private RadioButton radioOutros;
    private ProgressBar progressBar;


    private AppCompatButton btnCadastrarAuto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainCadastrarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        inicializarLigacoes();

        btnCadastrarAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SalvarAutos();
                exibierProgres();



            }
        });


    }



    private void SalvarAutos(){
        Autos autos = new Autos();
        autos.setAuto(cadastrar.getText().toString());
        if(radioClube.isChecked()){
            autos.setClube("clube");
            autos.getAuto().getClass().toString();

        } else {
            autos.setOutros("outros");
            autos.getAuto().getClass().toString();
        }

        autenticacaoUserBD= ConfigBD.FirebaseCadastroUser();

        Map<String,Object> autoMoveis = new HashMap<>();
        autoMoveis.put("auto",autos.getAuto());

        if(TextUtils.isEmpty(autos.getClube())){
            autoMoveis.put("outros", autos.getOutros());
        } else {
            autoMoveis.put("clube",autos.getClube());
        }

        DocumentReference documentReference = autenticacaoUserBD.collection("AutosCadastro").document();
        documentReference.set(autoMoveis).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("db","Salvado com sucesso");
                        Toast.makeText(MainActivityCadastrar.this, "Cadastro de Auto Sucesso", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("db_error","Erro ao salvar"+e.toString());
                    }
                });
    }

    private void inicializarLigacoes(){

        cadastrar = binding.editCadastrar;
        radioGroup = binding.radioGroupFins;
        btnCadastrarAuto = binding.btnGravarCadastrar;
        radioClube = binding.radioButtonClube;
        radioOutros = binding.radioButtonOutros;
        progressBar = binding.progressebarMain3;
    }

    public void exibierProgres(){
        progressBar.setVisibility(true ? View.VISIBLE: View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                progressBar.setVisibility(true ? View.INVISIBLE: View.GONE);
            }
        },3000);
    }


}