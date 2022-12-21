package com.example.myapplication;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.myapplication.databinding.ActivityMainCreateLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;


public class MainActivityCreateLogin extends AppCompatActivity {

    private String userID;
    private ActivityMainCreateLoginBinding binding;
    private EditText userNovo, emailNovo, senhaNovo, senhaConfirm;
    private AppCompatButton btnCadastrarNovo;
    private ProgressBar load;
    private String[] mensagens = {"Preencha dotos os campos", "Cadastro realizado com sucesso", "Usuário salvo","Error User"};
    private String[] mensagensExerc = {"Mínino 6 caracteres", "E-mail já cadastrado, insira outro e-mail",
            "E-mail não existe", "Erro no cadastro, tente novamente", "Preencha os campos senha novamente"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainCreateLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        senhaNovo = binding.editSenhaCreateLogin;
        senhaConfirm = binding.editSenhaCreateLoginConfirm;
        emailNovo = binding.editEmail;
        userNovo = binding.editUser;
        btnCadastrarNovo = binding.btnCreatLogin;

        btnCadastrarNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((TextUtils.isEmpty(userNovo.getText().toString()))
                        || ((TextUtils.isEmpty(emailNovo.getText().toString())))
                        || (TextUtils.isEmpty(senhaNovo.getText().toString()))
                        || (TextUtils.isEmpty(senhaConfirm.getText().toString()))) {
                    Toast.makeText(MainActivityCreateLogin.this, mensagens[0], Toast.LENGTH_SHORT).show();

                } else if (!senhaNovo.getText().toString().equals(senhaConfirm.getText().toString())){

                    senhaNovo.setText("");
                    senhaConfirm.setText("");
                    Toast.makeText(MainActivityCreateLogin.this, mensagensExerc[4], Toast.LENGTH_SHORT).show();

                } else {
                    cadastrandoUsers();
                }
            }
        });
    }

    private void cadastrandoUsers() {
        String email = emailNovo.getText().toString();
        String senha = senhaNovo.getText().toString();


        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(MainActivityCreateLogin.this, mensagens[1], Toast.LENGTH_SHORT).show();
                    salvingUsuarios();
                } else {
                    try {
                        throw task.getException();

                    } catch (FirebaseAuthWeakPasswordException e) {
                        Toast.makeText(MainActivityCreateLogin.this, mensagensExerc[0], Toast.LENGTH_SHORT).show();

                    } catch (FirebaseAuthUserCollisionException e) {
                        Toast.makeText(MainActivityCreateLogin.this, mensagensExerc[1], Toast.LENGTH_SHORT).show();

                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(MainActivityCreateLogin.this, mensagensExerc[2], Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        Toast.makeText(MainActivityCreateLogin.this, mensagensExerc[3], Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });
    }

    private void salvingUsuarios(){

        String nome = userNovo.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String,Object> useres = new HashMap<>();
        useres.put("nome", nome);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("Usuarios").document(userID);
        documentReference.set(useres).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("db",mensagens[2]);
            }
        })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("db_error",mensagens[3]+e.toString());
                    }
                });
    }

}




