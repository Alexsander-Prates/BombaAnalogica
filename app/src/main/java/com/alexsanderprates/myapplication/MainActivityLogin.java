package com.alexsanderprates.myapplication;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager.widget.ViewPager;

import com.alexsanderprates.myapplication.databinding.ActivityMainLoginBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

import Util.ConfigBD;
import Util.ImagePagerAdapter;
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
    private String [] mensagensErros = {"Preencha todos os campos","Login efetuado","E-mail ou Senha inválidos","Faça Login"};
    private AppCompatButton calcular;



    private ViewPager viewPager;
    private TabLayout tabLayout;
    private CheckBox checkBoxVerificador;
    private SharedPreferences preferences;
    private View btnClose;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showTutorialDialog();








        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        inicilializarLigacoes();
        MostrarSenhaDigitada();
        requestStoragePermissions();


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
                //Toast.makeText(MainActivityLogin.this, "Em manutenção", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivityCreateLogin.class);
                startActivity(intent);
            }
        });
        calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivityValues.class);
                startActivity(intent);
            }
        });
    }

    private void requestStoragePermissions() {
        //Para acessar midias e arquivos do android 10 para baixo, pode ser essas aqui.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE

                    )
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {
                                // As permissões foram concedidas, você pode acessar o armazenamento externo aqui
                            } else {
                                // Alguma(s) permissão(ões) foi/foram negada(s)
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        } else {

        }

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

                    Intent intent = new Intent(getApplicationContext(), MainActivityValues.class);
                    startActivity(intent);

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
            try{
                String voltarTelaLogin = getIntent().getStringExtra("voltar");
                if(voltarTelaLogin==null){
                    Intent intent = new Intent(this,MainActivityValues.class);
                    startActivity(intent);
                } else {

                }


            }catch (NullPointerException n){

            }


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
        calcular = binding.btnCalcular;


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

    private void showTutorialDialog() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("chaveGeral", Context.MODE_PRIVATE);
        boolean chaveChecked = preferences.getBoolean("chaveChecked", false);

        if (!chaveChecked) {
            List<Integer> images = new ArrayList<>();
            images.add(R.drawable.imagem1tutorial);
            images.add(R.drawable.imagem2tutorial);
            images.add(R.drawable.imagem3tutorial);
            images.add(R.drawable.imagem4tutorial);
            images.add(R.drawable.imagem5tutorial);

            // Cria a dialog personalizada
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_image_pager);


            ImageView closeButton = dialog.findViewById(R.id.closeButton);
            closeButton.setOnClickListener(v -> dialog.dismiss());

            // Configura o ViewPager e o TabLayout dentro da dialog
            viewPager = dialog.findViewById(R.id.viewPager);
            tabLayout = dialog.findViewById(R.id.tabLayout);

            // Configure o adaptador e as imagens no ViewPager
            ImagePagerAdapter adapter = new ImagePagerAdapter(this, images);
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);

            dialog.show();

            checkBoxVerificador = dialog.findViewById(R.id.checkBoxNoShow);
            checkBoxVerificador.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("chaveChecked", isChecked);
                    editor.apply();
                    Toast.makeText(MainActivityLogin.this,"Não mostrar", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            final int[] currentPosition = {0};
            ImageView btnNext = dialog.findViewById(R.id.btnNext);
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    currentPosition[0]++;
                    if (currentPosition[0] >= images.size()) {
                        currentPosition[0] = 0;
                    }
                    viewPager.setCurrentItem(currentPosition[0], true);
                }
            });
            ImageView btnYouTube = dialog.findViewById(R.id.btnYouTube);
            btnYouTube.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String abrirYouTube = "l6LX4MKBHRA";

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + abrirYouTube));
                    intent.putExtra("VIDEO_ID",abrirYouTube);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });


        }
    }


}