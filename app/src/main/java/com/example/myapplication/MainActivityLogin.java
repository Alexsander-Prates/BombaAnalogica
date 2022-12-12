package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.databinding.ActivityMainLoginBinding;


public class MainActivityLogin extends AppCompatActivity {

    private EditText email, senha;
    private TextView createUser;
    private Button login;
    private ActivityMainLoginBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        email = binding.editEmail;
        senha = binding.editSenha;
        login = binding.btnLogar;
        createUser = binding.btnNovoCadastro;


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(email.getText().toString())){

                }
            }
        });

        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivityCreateLogin.class);
                startActivity(intent);

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_cadastros, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

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

        return super.onOptionsItemSelected(item);


    }


}