package model;


public class Valores {



    private  String date, valorGasolina, valorOleo, valorTaxa, quantidadeL, quantidadeO , mensagem, valorTotal;

    public Valores(){

    }


    public Valores(String date, String valorTotal, String valorGasolina, String valorOleo, String valorTaxa, String quantidadeL, String quantidadeO, String mensagem) {
        this.valorTotal = valorTotal;
        this.valorGasolina = valorGasolina;
        this.valorOleo = valorOleo;
        this.valorTaxa = valorTaxa;
        this.quantidadeL = quantidadeL;
        this.quantidadeO = quantidadeO;
        this.mensagem = mensagem;
        this.date = date;

    }

    public String getValorGasolina() {
        return valorGasolina;
    }

    public String getValorOleo() {
        return valorOleo;
    }

    public String getValorTaxa() {
        return valorTaxa;
    }

    public String getQuantidadeL() {
        return quantidadeL;
    }

    public String getQuantidadeO() {
        return quantidadeO;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getValorTotal() {
        return valorTotal;
    }

    public String getDate() {

        return date;
    }
    public void setValorGasolina(String valorGasolina) {
        this.valorGasolina = valorGasolina;
    }

    public void setValorOleo(String valorOleo) {
        this.valorOleo = valorOleo;
    }

    public void setValorTaxa(String valorTaxa) {
        this.valorTaxa = valorTaxa;
    }

    public void setQuantidadeL(String quantidadeL) {
        this.quantidadeL = quantidadeL;
    }

    public void setQuantidadeO(String quantidadeO) {
        this.quantidadeO = quantidadeO;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
