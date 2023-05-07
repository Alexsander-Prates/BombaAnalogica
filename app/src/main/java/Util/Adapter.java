package Util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;


import androidx.recyclerview.widget.RecyclerView;


import com.example.myapplication.MainActivityCalculations;
import com.example.myapplication.MainActivityHistorics;
import com.example.myapplication.R;

import com.squareup.picasso.Picasso;


import java.io.IOError;
import java.util.ArrayList;

import model.Autos;


public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    ImageButton btn;
    Context context;
    ArrayList<Autos> userArrayList;




    public Adapter(Context context, ArrayList<Autos> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_list_recycle,parent,false);

        return new MyViewHolder(v);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {



        Autos autos = userArrayList.get(position);
        String id = userArrayList.get(position).getId();
        String nome =userArrayList.get(position).getAuto();

        holder.auto.setText(autos.getAuto());
        holder.desc.setText(autos.getDesc());

        try{
            Picasso.get().load(autos.getPhoto()).into(holder.photo);
        } catch (IOError e){
            Log.i("Teste_photo", autos.getPhoto());
        }



        if(TextUtils.isEmpty(autos.getClube())){
            holder.outro.setText("Outros");
        } else{
            holder.clube.setText(autos.getClube());
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(),MainActivityHistorics.class);
                intent.putExtra("id",id);
                intent.putExtra("nomeAuto",nome);

                context.startActivity(intent);
            }
        });


    }



    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView auto, clube, desc, outro;
        ImageView photo;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            btn=itemView.findViewById(R.id.btnHistoric);
            auto=itemView.findViewById(R.id.textAuto);
            clube=itemView.findViewById(R.id.textCategoria);
            outro=itemView.findViewById(R.id.textCategoria);
            desc=itemView.findViewById(R.id.textDesc);
            photo=itemView.findViewById(R.id.receberImagem);

        }

    }


}
