package com.example.myapplication;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityMainValuesBinding;


public class MainActivityValues extends AppCompatActivity {

    private ActivityMainValuesBinding binding;
    private EditText valorLitro,valorOleo;
    private AppCompatButton botaoProximo;
    private ProgressBar load;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainValuesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        valorOleo = binding.editNumberValorOleo;
        valorLitro = binding.editNumberValorLitro;
        botaoProximo = binding.btnProximoGravar;
        load = binding.progressebar;

        botaoProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificadorIniciar();

            }
        });

    }

    private void chamarMainActivity(){
        Intent intent = new Intent(getApplicationContext(), MainActivityCalculations.class);
        if(TextUtils.isEmpty(valorLitro.getText().toString())){
            intent.putExtra("valorOleo",valorOleo.getText().toString());
        } else if (TextUtils.isEmpty(valorOleo.getText().toString())){
            intent.putExtra("valorLitro",valorLitro.getText().toString());
        } else{
            intent.putExtra("valorOleo",valorOleo.getText().toString());
            intent.putExtra("valorLitro",valorLitro.getText().toString());
        }
        startActivity(intent);
        exibierProgres();

    }

    private void verificadorIniciar(){
        if ((TextUtils.isEmpty(valorOleo.getText().toString()))&&
                (TextUtils.isEmpty(valorLitro.getText().toString()))) {
            Toast.makeText(MainActivityValues.this, "Preencha um dos campos", Toast.LENGTH_SHORT).show();
        }  else {

            chamarMainActivity();

        }

    }


    public void exibierProgres(){
        load.setVisibility(true ? View.VISIBLE: View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_login,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.itemLogar:
                Intent intent2 = new Intent(getApplicationContext(), MainActivityLogin.class);
                startActivity(intent2);
                break;

        }

        return super.onOptionsItemSelected(item);
    }


}