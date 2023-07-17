package Util;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import model.Valores;

public class PDF extends Valores {



    private File arquivo;
    Context context;
    private String nomeFile;
    FirebaseAuth autenticacaoAuth = ConfigBD.FirebaseAutentic();
    FirebaseFirestore autenticacaoUserBD = ConfigBD.FirebaseCadastroUser();
    CollectionReference meusUsuarios= autenticacaoUserBD.collection("Usuarios");
    private ContentResolver contentResolver;




    public PDF(Context context){
        this.context = context;
        this.contentResolver = context.getContentResolver();
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

        SimpleDateFormat formatoData = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy 'às' HH: mm: ss", new Locale("pt", "BR"));
        String dateFim = formatoData.format(date);
        String dateF = dateFim;

        canvas.drawText(dataPDF,30,60,corTexto2);
        canvas.drawText(dateF.toString(), 30, 75, corTexto);

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

                    requestStoragePermissions();
                    File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    if(!downloadsDirectory.exists()){
                        downloadsDirectory.mkdir();
                    }
                    nomeFile = dateF;
                    salvarPDF(documentoPDF,nomeFile, downloadsDirectory);

                    documentoPDF.close();

                }
            }
        });

    }


    public void salvarPDF(PdfDocument documentoPDF, String nomeDoArquivo, File pasta) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, nomeDoArquivo + ".pdf");
            values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

            try {
                OutputStream outputStream = contentResolver.openOutputStream(uri);
                documentoPDF.writeTo(outputStream);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
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
    }






    private void requestStoragePermissions() {
        Dexter.withContext(this.context)
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
    }

}
