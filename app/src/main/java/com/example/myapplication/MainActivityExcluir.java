package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import com.example.myapplication.databinding.ActivityMainExcluirBinding;

public class MainActivityExcluir extends AppCompatActivity {

    private ActivityMainExcluirBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainExcluirBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}