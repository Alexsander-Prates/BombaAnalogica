package Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
        FirebaseFirestore autenticacaoUserBD = ConfigBD.FirebaseCadastroUser();
        CollectionReference historicsAutos = autenticacaoUserBD.collection("HistoricoValores");
        Valores valores = valorArrayList.get(position);


        holder.valorTotal.setText(valores.getValorTotal());
        holder.mensagem.setText(valores.getMensagem());
        holder.date.setText(valores.getDate());

        Valores valores = valorArrayList.get(position);


        holder.valorTotal.setText(valores.getValorTotal());
        holder.mensagem.setText(valores.getMensagem());
        holder.date.setText(valores.getDate());


    }

    @Override
    public int getItemCount() {
        return valorArrayList.size();

    }

    public class MyViewHolderH extends  RecyclerView.ViewHolder{

        TextView date, valorTotal, mensagem;

        public MyViewHolderH(@NonNull View itemView) {
            super(itemView);

            date=itemView.findViewById(R.id.textDate);
            valorTotal=itemView.findViewById(R.id.textValorTotal);
            mensagem=itemView.findViewById(R.id.textMensagem);

        }
    }
}