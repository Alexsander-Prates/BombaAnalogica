package Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;

import model.Valores;

public class AdapterH extends RecyclerView.Adapter<AdapterH.MyViewHolderH> {

    Context context;

    ArrayList<Valores> valorArrayList;

    public AdapterH(Context context, ArrayList<Valores> valorArrayList){
        this.context=context;
        this.valorArrayList=valorArrayList;

    }

    @NonNull
    @Override
    public MyViewHolderH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_list_recy_historic,parent,false);
        return new MyViewHolderH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderH holder, int position) {


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolderH extends  RecyclerView.ViewHolder{

        // elementos

        public MyViewHolderH(@NonNull View itemView) {
            super(itemView);
        }
    }
}