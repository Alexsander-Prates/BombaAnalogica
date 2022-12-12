package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityMainCreateLoginBinding;
import com.example.myapplication.databinding.ActivityMainLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivityCreateLogin extends AppCompatActivity {

    private ActivityMainCreateLoginBinding binding;
    private EditText userNovo, emailNovo, senhaNovo, senhaConfirm;
    private AppCompatButton btnCadastrarNovo;
    private ProgressBar load;
    private String[] mensagens = {"Preencha dotos os campos" , "Cadastro realizado com sucesso"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainCreateLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        senhaNovo = binding.editEmail;
        senhaConfirm = binding.editSenhaCreateLoginConfirm;
        emailNovo = binding.editEmail;
        userNovo = binding.editUser;
        btnCadastrarNovo = binding.btnCreatLogin;

        btnCadastrarNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                senhaNovo.getText().toString();
                if((TextUtils.isEmpty(senhaNovo.getText().toString()))
                        ||((TextUtils.isEmpty(senhaConfirm.getText().toString())))
                ||(TextUtils.isEmpty(emailNovo.getText().toString()))
                        ||(TextUtils.isEmpty(emailNovo.getText().toString()))){
                    Toast.makeText(MainActivityCreateLogin.this, mensagens[0], Toast.LENGTH_SHORT).show();
                } else {
                    CadastrandoUsers();
                }
            }
        });
    }

    private void CadastrandoUsers(){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailNovo.getText().toString(),senhaNovo.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivityCreateLogin.this, mensagens[1], Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}