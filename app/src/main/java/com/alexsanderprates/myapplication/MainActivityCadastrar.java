package com.alexsanderprates.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;


import android.Manifest;
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
import android.widget.Toast;


import com.alexsanderprates.myapplication.databinding.ActivityMainCadastrarBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
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
import com.squareup.picasso.Picasso;


import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import Util.Adapter;
import Util.ConfigBD;

import model.Autos;


public class MainActivityCadastrar extends AppCompatActivity {

    private ActivityMainCadastrarBinding binding;

    FirebaseFirestore autenticacaoUserBD = ConfigBD.FirebaseCadastroUser();


    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseAuth autenticacaoAuth = ConfigBD.FirebaseAutentic();


    private Uri photoUri;
    private ImageView photoAuto;
    private EditText nome, descricao;
    private RadioButton radioClube, radioOutros;
    private ProgressBar progressBar;
    private AppCompatButton btnCadastrarAuto, btnShowData;
    String pNome, pDesc, pOutros, pClube, pId,pPhoto,pPhotoKey;



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
        askPermission();


        btnCadastrarAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = getIntent().getExtras();
                if(bundle!= null){

                    updateDadosCadastro();

                } else {
                    if(photoUri!=null){
                        inserirPhoto();
                    }else{
                        String gambiarra = "sem foto";
                        salvarAutos(gambiarra,gambiarra);
                    }

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

    private void askPermission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
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
        try {
            if (requestCode == 0) {
                photoUri = data.getData();

                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);

                    // Verifica se o tamanho da foto é menor que 5MB
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    int fileSize = outputStream.toByteArray().length;

                    if (fileSize > 5 * 1024 * 1024) {
                        // Foto excede o tamanho máximo permitido (5MB)
                        Toast.makeText(this, "A foto selecionada excede o tamanho máximo permitido.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    photoAuto.setImageDrawable(new BitmapDrawable(bitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (NullPointerException e) {
            // Tratamento da exceção
        }
    }


    private void inserirPhoto() {

        try{

            byte[] bytes = new byte[0];
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),photoUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,0,byteArrayOutputStream);
                bytes = byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }

            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Up Photo");
            pd.show();


            final String randomKey = UUID.randomUUID().toString();
            String nome = autenticacaoAuth.getCurrentUser().getEmail() + "/";
            String teste = "images ";
            String nomePastaPhoto = teste + nome;
            StorageReference riversRef = storageReference.child(nomePastaPhoto + randomKey);


            riversRef.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.i("Test_URL",uri.toString());
                            String receberPhoto = uri.toString();
                            String photoKey = randomKey;

                            Bundle bundle = getIntent().getExtras();
                            if(bundle!= null){

                            }
                            else {
                                salvarAutos( receberPhoto, photoKey);
                            }

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

        }catch (NullPointerException n){
            Toast.makeText(getApplicationContext(), "Sem foto", Toast.LENGTH_SHORT).show();
        }

    }

    private void salvarAutos(String receberPhoto, String photokEY){
        Autos autos = new Autos();
        autos.setAuto(nome.getText().toString());

        if(receberPhoto!=null){
            autos.setPhoto(receberPhoto);
            autos.setPhotoKey(photokEY);
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
        autoMoveis.put("photoKey",autos.getPhotoKey());
        autoMoveis.put("adm",autenticacaoAuth.getCurrentUser().getUid());


        if(TextUtils.isEmpty(autos.getClube())){
            autoMoveis.put("outros", autos.getOutros());
        } else {
            autoMoveis.put("clube",autos.getClube());
        }

        DocumentReference documentReference = autenticacaoUserBD.collection("Autos - " + autenticacaoAuth.getCurrentUser().getEmail()).document(autos.getId());
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
        btnCadastrarAuto = binding.btnGravarCadastrar;
        radioClube = binding.radioButtonClube;
        radioOutros = binding.radioButtonOutros;
        progressBar = binding.progressebarMain3;
        descricao = binding.editDescricao;
        btnShowData = binding.btnMostrarList;
        photoAuto = binding.fotoMeioLocomocao;

        Bundle bundle = getIntent().getExtras();
        if(bundle!= null){
            pPhoto = bundle.getString("pPhoto");
            pPhotoKey = bundle.getString("pPhotoKey");

            try{
                Picasso.get().load(pPhoto).into(photoAuto);
            } catch (IOError e){

            }

            //nome.setEnabled(false);
        }


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
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Up Photo");
        pd.show();

        Bundle bundle = getIntent().getExtras();
        pId = bundle.getString("pId");
        String upNome = nome.getText().toString();
        String upDesc = descricao.getText().toString();
        String upClube = "Clube";
        String upOutros = "Outros";



        if(bundle.getString("pPhotoKey")!=null){

            BitmapDrawable drawable = (BitmapDrawable) photoAuto.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            File file = new File(getCacheDir(), "temp_image.jpg");

            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 0, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Uri fileUri = Uri.fromFile(file);

            String nome = autenticacaoAuth.getCurrentUser().getEmail() + "/";
            String teste = "images ";
            String nomePastaPhoto = teste + nome;
            StorageReference riversRef = storageReference.child(nomePastaPhoto + pPhotoKey);
            UploadTask uploadTask = riversRef.putFile(fileUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String receberNovaPhoto = uri.toString();
                            CollectionReference meusAutos = autenticacaoUserBD.collection("Autos - " + autenticacaoAuth.getCurrentUser().getEmail());

                            meusAutos.document(pId).update("auto",upNome,
                                    "desc",upDesc,"photo",receberNovaPhoto).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                    pd.dismiss();
                    Snackbar.make(findViewById(android.R.id.content), "Imagem Uploaded",Snackbar.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progressPercent = (100.00 * snapshot.getBytesTransferred()/ snapshot.getTotalByteCount());
                    pd.setMessage("Percentage: " + (int) progressPercent + "%");
                }
            });

        }

    }
}