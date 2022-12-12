package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.myapplication.databinding.ActivityMainCadastrarBinding;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivityCadastrar extends AppCompatActivity {

    private ActivityMainCadastrarBinding binding;
    private EditText cadastrar;
    private RadioGroup radioGroup;
    private String radioClube;
    private String radioOutros;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainCadastrarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        cadastrar = binding.editCadastrar;
        radioGroup = binding.radioGroupFins;

    }

    private void RadioGroupSelecionado(){
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId){
                    case R.id.radioButtonClube:
                        radioClube = "Clube";
                        break;
                    case R.id.radioButtonOutros:
                        radioOutros = "Outros";
                        break;
                }
            }
        });
    }

    private void SalvarDadosAuto(){
        RadioGroupSelecionado();

        cadastrar.getText().toString();
        Map<String,Object> autos = new HashMap<>();
        autos.put("auto",cadastrar);

        if(radioClube!=null){
            autos.put("fins",radioClube);

        } else {
            autos.put("fins",radioOutros);
        }


    }
}