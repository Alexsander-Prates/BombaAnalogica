package Util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import model.Valores;

public class PDF extends Valores {



    private File arquivo;
    private Context context;
    private String nomeFile;

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

        PdfDocument.PageInfo detalhePage =
                new PdfDocument.PageInfo.Builder(300, 400, 1).create();

        PdfDocument.Page novaPagina = documentoPDF.startPage(detalhePage);

        Canvas canvas = novaPagina.getCanvas();

        Paint corTexto = new Paint();
        corTexto.setColor(Color.BLACK);

        Paint corTexto2 = new Paint();
        corTexto2.setColor(Color.GRAY);

        String autoDPF = "Nome do Auto";
        String litroPDF = "Valor do Litro R$ ";
        String oleoPDF = "Valor do Óleo R$ ";
        String taxaPDF = "Taxa cobrada R$ ";
        String quantLitroPDF = "Litros abastecidos ";
        String quantOleoPDF = "Oleo abastecidos ";
        String dataPDF = "Data do abastecimento";
        String valorTotalPDF = "Total à Pagar R$ ";
        String descPDF = "Descrição do Auto";

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



        documentoPDF.finishPage(novaPagina);


        File pasta = new File(Environment.getExternalStorageDirectory() + "/Download");
        nomeFile = date.toString();
        salvarPDF(documentoPDF,nomeFile, pasta);

        documentoPDF.close();


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
}
