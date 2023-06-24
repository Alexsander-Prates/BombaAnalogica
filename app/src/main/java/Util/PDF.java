package Util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.myapplication.MainActivityResults;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Valores;

public class PDF extends Valores {



    private File arquivo;
    Context context;
    private String nomeFile;
    FirebaseAuth autenticacaoAuth = ConfigBD.FirebaseAutentic();
    FirebaseFirestore autenticacaoUserBD = ConfigBD.FirebaseCadastroUser();
    CollectionReference meusUsuarios= autenticacaoUserBD.collection("Usuarios");
    Bitmap photoPDF;



    public PDF() {
    }

    public PDF(Context context){
        this.context = context;
    }

    public PDF(String date, String valorTotal, String valorGasolina, String valorOleo, String valorTaxa, String quantidadeL, String quantidadeO, String mensagem) {
        super(date, valorTotal, valorGasolina, valorOleo, valorTaxa, quantidadeL, quantidadeO, mensagem);
    }

    public void gerarPDF(String desc, String valorTotals, Date date,String auto,String valorLitroS, String valorOleoS, String taxaS, String litros, String quantO, String mensagem) {

        PdfDocument documentoPDF = new PdfDocument();
        PdfDocument.PageInfo detalhePage = new PdfDocument.PageInfo.Builder(300, 400, 1).create();

        PdfDocument.Page novaPagina = documentoPDF.startPage(detalhePage);
        Canvas canvas = novaPagina.getCanvas();

        Paint corTexto = new Paint();
        corTexto.setColor(Color.BLACK);

        Paint corTexto2 = new Paint();
        corTexto2.setColor(Color.GRAY);

        String autoDPF = "Nome do Auto";
        String litroPDF = "Valor do Litro R$ ";
        String oleoPDF = "Valor do Óleo R$ ";
        String taxaPDF = "Taxa % ";
        String quantLitroPDF = "Litros abastecidos ";
        String quantOleoPDF = "Oleo abastecidos ";
        String dataPDF = "Data do abastecimento";
        String valorTotalPDF = "Total à Pagar R$ ";
        String descPDF = "Descrição do Auto";
        String chavePixPDF = "Chave Pix";
        String nomeUserPDF = "Usuário";
        String emailUserPDF = "E-mail";


        canvas.drawText(dataPDF,30,60,corTexto2);
        canvas.drawText(date.toString(), 30, 75, corTexto);

        canvas.drawText(autoDPF,30,100,corTexto2);
        canvas.drawText(auto, 180, 100, corTexto);

        canvas.drawText(litroPDF,30,120,corTexto2);
        canvas.drawText(valorLitroS, 180, 120, corTexto);

        canvas.drawText(oleoPDF,30,140,corTexto2);
        canvas.drawText(valorOleoS, 180, 140, corTexto);

        canvas.drawText(taxaPDF,30,160,corTexto2);
        canvas.drawText(taxaS, 180, 160, corTexto);

        canvas.drawText(quantLitroPDF,30,180,corTexto2);
        canvas.drawText(litros, 180, 180, corTexto);

        canvas.drawText(quantOleoPDF,30,200,corTexto2);
        canvas.drawText(quantO, 180, 200, corTexto);

        canvas.drawText(valorTotalPDF,30,220,corTexto2);
        canvas.drawText(valorTotals, 180, 220, corTexto);

        canvas.drawText(descPDF,30,260,corTexto2);
        canvas.drawText(desc, 30, 275, corTexto);



        String id = autenticacaoAuth.getCurrentUser().getUid();
        meusUsuarios.document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if(documentSnapshot != null){

                    final String nome = documentSnapshot.getString("nome");
                    final String photoUri = documentSnapshot.getString("photoUri");
                    final String pix = documentSnapshot.getString("pix");
                    final String email = autenticacaoAuth.getCurrentUser().getEmail();

                    canvas.drawText(nomeUserPDF,30,310,corTexto2);
                    canvas.drawText(nome, 90, 310, corTexto);

                    canvas.drawText(emailUserPDF,30,330,corTexto2);
                    canvas.drawText(email, 90, 330, corTexto);

                    canvas.drawText(chavePixPDF,30,350,corTexto2);
                    canvas.drawText(pix, 30, 380, corTexto);


                    documentoPDF.finishPage(novaPagina);

                    asPermissions();
                    File pasta = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myPDF");
                    if(!pasta.exists()){
                        pasta.mkdir();
                    }
                    nomeFile = date.toString();
                    salvarPDF(documentoPDF,nomeFile, pasta);

                    documentoPDF.close();




                }




            }
        });





    }


    public void salvarPDF(PdfDocument documentoPDF ,String nomeDoArquivo , File pasta){
        arquivo = new File(pasta, nomeDoArquivo + ".pdf");

        try {
            arquivo.createNewFile();
            OutputStream stream = new FileOutputStream(arquivo);
            documentoPDF.writeTo(stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void asPermissions(){
        Dexter.withContext(this.context).withPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
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
}
