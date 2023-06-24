package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityMainCadastrarPropBinding;

public class MainActivityCadastrarProp extends AppCompatActivity {

    private ActivityMainCadastrarPropBinding binding;
    private AppCompatButton btnCreatProp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainCadastrarPropBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        inicializarLicacoes();

        btnCreatProp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivityCadastrarProp.this, "Em criação", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inicializarLicacoes(){

        btnCreatProp = binding.btnCreatPropLogin;


    }

}