package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityMainLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import Util.ConfigBD;
import model.User;


public class MainActivityLogin extends AppCompatActivity {

    private CheckBox verSenha;
    private boolean verificadorLogin = false;
    private FirebaseAuth autenticacao;
    private EditText emailLogin, senhaLogin;
    private TextView createUser;
    private Button login;
    private ProgressBar progressBar;
    private ActivityMainLoginBinding binding;
    private String [] mensagensErros = {"Preencha todos os campos","Login efetuado","E-mail ou Senha inválidos","Fça Login"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        inicilializarLigacoes();
        MostrarSenhaDigitada();




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if((TextUtils.isEmpty(emailLogin.getText().toString())) ||
                (TextUtils.isEmpty(senhaLogin.getText().toString()))){
                    Toast.makeText(MainActivityLogin.this, mensagensErros[0], Toast.LENGTH_SHORT).show();
                } else{
                    AutenticarUser();

                }



            }
        });






        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivityLogin.this, "Em manutenção", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(getApplicationContext(), MainActivityCreateLogin.class);
                //startActivity(intent);

            }
        });


    }


    private void AutenticarUser() {

        User user = new User();
        user.setEmail(emailLogin.getText().toString());
        user.setSenha(senhaLogin.getText().toString());

        autenticacao = ConfigBD.FirebaseAutentic();

        autenticacao.signInWithEmailAndPassword(user.getEmail(), user.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivityLogin.this, mensagensErros[1], Toast.LENGTH_SHORT).show();
                    exibierProgres();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            verificadorLogin = true;
                            progressBar.setVisibility(true ? View.INVISIBLE: View.GONE);
                        }
                    },3000);
                } else{
                    Toast.makeText(MainActivityLogin.this, mensagensErros[2], Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser userAtual = FirebaseAuth.getInstance().getCurrentUser();

        if(userAtual!=null){
            verificadorLogin = true;
            login.setText("Deslogar");
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().signOut();
                    verificadorLogin = false;
                    login.setText("Login");
                }
            });
        }

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
                Intent intent2 = new Intent(getApplicationContext(), MainActivityValues.class);
                startActivity(intent2);
                break;

        }

        return super.onOptionsItemSelected(item);
    }



    private void inicilializarLigacoes(){
        emailLogin = binding.editEmail;
        senhaLogin = binding.editSenha;
        login = binding.btnLogar;
        createUser = binding.btnNovoCadastro;
        progressBar = binding.progressebar;
        verSenha = binding.chekSenhaLogin;


    }

    public void exibierProgres(){
        progressBar.setVisibility(true ? View.VISIBLE: View.GONE);
    }

    private void MostrarSenhaDigitada(){
        verSenha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    senhaLogin.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    senhaLogin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }


}