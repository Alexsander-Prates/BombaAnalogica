package com.example.myapplication;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;





public class MainActivity extends AppCompatActivity {


    private EditText valorLitro,valorOleo;
    private AppCompatButton botaoProximo;
    private ProgressBar load;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        valorOleo = findViewById(R.id.editNumberValorOleo);
        valorLitro = findViewById(R.id.editNumberValorLitro);
        botaoProximo = findViewById(R.id.btnProximoGravar);
        load = findViewById(R.id.progressebar);

        botaoProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificadorIniciar();

            }
        });
    }

    private void chamarMainActivity(){
        Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
        if(TextUtils.isEmpty(valorLitro.getText().toString())){
            intent.putExtra("valorOleo",valorOleo.getText().toString());
        } else if (TextUtils.isEmpty(valorOleo.getText().toString())){
            intent.putExtra("valorLitro",valorLitro.getText().toString());
        } else{
            intent.putExtra("valorOleo",valorOleo.getText().toString());
            intent.putExtra("valorLitro",valorLitro.getText().toString());
        }
        startActivity(intent);
        exibierProgres();

    }

    private void verificadorIniciar(){
        if ((TextUtils.isEmpty(valorOleo.getText().toString()))&&
                (TextUtils.isEmpty(valorLitro.getText().toString()))) {
            Toast.makeText(MainActivity.this, "Preencha um dos campos", Toast.LENGTH_SHORT).show();
        }  else {

            chamarMainActivity();
            finish();
        }

    }


    public void exibierProgres(){
        load.setVisibility(true ? View.VISIBLE: View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_cadastros,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.itemCadastrar:
                Intent intent2 = new Intent(getApplicationContext(), MainActivityCadastros.class);
                startActivity(intent2);
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}