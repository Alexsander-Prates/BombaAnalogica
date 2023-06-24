package com.example.myapplication;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.myapplication.databinding.ActivityMainCreateLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import Util.ConfigBD;
import model.User;


public class MainActivityCreateLogin extends AppCompatActivity {

    private CheckBox verSenha;
    private FirebaseAuth autenticacao;
    private FirebaseFirestore autenticacaoUserBD;
    private ActivityMainCreateLoginBinding binding;

    private EditText userNovo, emailNovo, senhaNovo, senhaConfirm , pix;
    private AppCompatButton btnCadastrarNovo;
    private ProgressBar load;

    private String[] mensagens = {"Preencha dotos os campos", "Cadastro realizado com sucesso", "Usuário salvo","Error User"};
    private String[] mensagensExerc = {"Mínino 6 caracteres", "E-mail já cadastrado, insira outro e-mail",
            "E-mail não existe", "Erro no cadastro, tente novamente", "Preencha os campos senha novamente"};

    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri photoUri;
    private ImageView photoCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainCreateLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        inicializarLicacoes();
        MostrarSenhasDigitadas();
        askPermission();


        btnCadastrarNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(pix.getText().toString())){
                    pix.setText("Não Cadastrado");
                }
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

        photoCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });
    }




    private void askPermission() {
        Dexter.withContext(this).withPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();

    }

    private void cadastrandoUsers() {
        User user = new User();
        user.setEmail(emailNovo.getText().toString());
        user.setSenha(senhaNovo.getText().toString());

        autenticacao = ConfigBD.FirebaseAutentic();

        autenticacao.createUserWithEmailAndPassword(user.getEmail(), user.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(MainActivityCreateLogin.this, mensagens[1], Toast.LENGTH_SHORT).show();
                    salvingUsuarios();
                } else {
                    String[] erroExecao = {};
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

        byte[] bytes = new byte[0];
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),photoUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
            bytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Up Photo");
        pd.show();


        final String randomKey = UUID.randomUUID().toString();
        String nome = emailNovo.getText().toString() + "/";
        String teste = "logo ";
        String nomePastaPhoto = teste + nome;
        StorageReference riversRef = storageReference.child(nomePastaPhoto + randomKey);


        riversRef.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.i("Test_URL",uri.toString());
                        final String receberPhoto = uri.toString();
                        String photoKey = randomKey;


                        User user = new User();

                        user.setNome(userNovo.getText().toString());
                        user.setPix(pix.getText().toString());

                        if(receberPhoto!=null){
                            user.setPhoto(receberPhoto);
                            user.setPhotoKey(photoKey);
                        }


                        autenticacaoUserBD=ConfigBD.FirebaseCadastroUser();

                        user.setUserID(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        Map<String,Object> useres = new HashMap<>();
                        useres.put("nome", user.getNome());
                        useres.put("id", user.getUserID());
                        useres.put("pix", user.getPix());
                        useres.put("photo", user.getPhoto());
                        useres.put("photoKey", user.getPhotoKey());
                        useres.put("photoUri",photoUri);


                        DocumentReference documentReference = autenticacaoUserBD.collection("Usuarios").document(user.getUserID());
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
                });
                pd.dismiss();
                Snackbar.make(findViewById(android.R.id.content), "Imagem Uploaded",Snackbar.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Falha no Upload", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred()/ snapshot.getTotalByteCount());
                pd.setMessage("Percentage: " + (int) progressPercent + "%");
            }
        });


    }

    private void inicializarLicacoes(){

        senhaNovo = binding.editSenhaCreateLogin;
        senhaConfirm = binding.editSenhaCreateLoginConfirm;
        emailNovo = binding.editEmail;
        userNovo = binding.editUser;
        btnCadastrarNovo = binding.btnCreatLogin;
        verSenha = binding.chekSenhaCreatLogin;
        pix = binding.editPix;
        photoCadastro = binding.imageView4;

    }

    private void MostrarSenhasDigitadas(){
        verSenha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    senhaNovo.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    senhaConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    senhaNovo.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    senhaConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    private void selectPhoto() {
        try{

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 0 );

        }catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode==0){
                photoUri = data.getData();

                Bitmap bitmap = null;

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),photoUri);
                photoCadastro.setImageDrawable(new BitmapDrawable(bitmap));
            }


        }catch (NullPointerException e){
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void inserirPhoto() {


    }
}




