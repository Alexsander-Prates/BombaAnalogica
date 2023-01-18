package Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;

import model.Autos;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

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

        holder.auto.setText(autos.getAuto());
        holder.clube.setText(autos.getClube());



    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView auto, clube, user;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            auto=itemView.findViewById(R.id.textAuto);
            clube=itemView.findViewById(R.id.textCategoria);
            user=itemView.findViewById(R.id.textUser);
        }
    }


}
