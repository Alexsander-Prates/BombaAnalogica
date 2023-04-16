package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityMainCadastrarBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import Util.ConfigBD;
import model.Autos;

public class MainActivityCadastrar extends AppCompatActivity {

    private ActivityMainCadastrarBinding binding;

    FirebaseFirestore autenticacaoUserBD = ConfigBD.FirebaseCadastroUser();
    CollectionReference meusAutos = autenticacaoUserBD.collection("AutosCadastro");
    FirebaseStorage storage;
    StorageReference storageReference;

    private Uri photoUri;
    private ImageView photoAuto;
    private EditText nome, descricao;
    private RadioButton radioClube, radioOutros;
    private ProgressBar progressBar;
    private AppCompatButton btnCadastrarAuto, btnShowData;
    String pNome, pDesc, pOutros, pClube, pId;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainCadastrarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        inicializarLigacoes();
        irShowData();
        receberDadosCadastroAuto();


        btnCadastrarAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = getIntent().getExtras();
                if(bundle!= null){

                    updateDadosCadastro();

                } else {

                    inserirPhoto();
                }

                if(TextUtils.isEmpty(descricao.getText().toString())||
                        (TextUtils.isEmpty(nome.getText().toString())||
                                (TextUtils.isEmpty(radioClube.getText().toString())||
                (TextUtils.isEmpty(radioOutros.getText().toString()))))){
                    Toast.makeText(MainActivityCadastrar.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();

                } else {

                    exibirProgres();
                }

            }
        });

        photoAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto();
            }
        });
    }

    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0 );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 ){
            photoUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),photoUri);
                photoAuto.setImageDrawable(new BitmapDrawable(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }

    private void inserirPhoto() {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Up Photo");
        pd.show();


        final String randomKey = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("images/" + randomKey);

        riversRef.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.i("Test_URL",uri.toString());
                        final String receberPhoto = uri.toString();
                        salvarAutos( receberPhoto);
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

    private void salvarAutos(String receberPhoto){
        Autos autos = new Autos();
        autos.setAuto(nome.getText().toString());

        if(receberPhoto!=null){
            autos.setPhoto(receberPhoto);
        }
        if(descricao!=null){
            autos.setDesc(descricao.getText().toString());
        }
        if(radioClube.isChecked()){
            autos.setClube("Clube");
            autos.getAuto().getClass().toString();

        } else {
            autos.setOutros("Outros");
            autos.getAuto().getClass().toString();
        }

        autenticacaoUserBD= ConfigBD.FirebaseCadastroUser();

        autos.setId(UUID.randomUUID().toString());

        Map<String,Object> autoMoveis = new HashMap<>();
        autoMoveis.put("auto",autos.getAuto());
        autoMoveis.put("id",autos.getId());
        autoMoveis.put("desc",autos.getDesc());
        autoMoveis.put("photo",autos.getPhoto());

        if(TextUtils.isEmpty(autos.getClube())){
            autoMoveis.put("outros", autos.getOutros());
        } else {
            autoMoveis.put("clube",autos.getClube());
        }

        DocumentReference documentReference = autenticacaoUserBD.collection("AutosCadastro").document(autos.getAuto());
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


        nome = binding.editNome;
        Bundle bundle = getIntent().getExtras();
        if(bundle!= null){
            nome.setEnabled(false);
        }
        btnCadastrarAuto = binding.btnGravarCadastrar;
        radioClube = binding.radioButtonClube;
        radioOutros = binding.radioButtonOutros;
        progressBar = binding.progressebarMain3;
        descricao = binding.editDescricao;
        btnShowData = binding.btnMostrarList;
        photoAuto = binding.fotoMeioLocomocao;

    }

    public void exibirProgres(){
        progressBar.setVisibility(true ? View.VISIBLE: View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                progressBar.setVisibility(true ? View.INVISIBLE: View.GONE);
            }
        },3000);
    }

    public void irShowData(){
        btnShowData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivityExcluir.class);
                startActivity(intent);
            }
        });
    }

    private void receberDadosCadastroAuto(){
        Bundle bundle = getIntent().getExtras();
        if(bundle!= null){
            pNome = bundle.getString("pNome");
            String pNomeAlt = pNome;
            pDesc= bundle.getString("pDesc");
            pId = bundle.getString("id");

            if(TextUtils.isEmpty(bundle.getString("pClube"))){
                pOutros = bundle.getString("pOutros");
                radioOutros.setChecked(true);
            } else {
                pClube = bundle.getString("pClube");
                radioClube.setChecked(true);
            }
            nome.setText(pNomeAlt);
            descricao.setText(pDesc);

        }
    }

    private void updateDadosCadastro(){


        String upNome = nome.getText().toString();
        String upDesc = descricao.getText().toString();
        String upClube = "Clube";
        String upOutros = "Outros";

        meusAutos.document(pNome).update("auto",upNome,
                "desc",upDesc).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }


}