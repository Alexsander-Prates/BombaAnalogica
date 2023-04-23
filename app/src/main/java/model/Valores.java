package model;

public class Valores {

    private final String valorGasolina, valorOleo, valorTaxa, quantidadeL, quantidadeO , mensagem;


    public Valores(String valorGasolina, String valorOleo, String valorTaxa, String quantidadeL, String quantidadeO, String mensagem) {
        this.valorGasolina = valorGasolina;
        this.valorOleo = valorOleo;
        this.valorTaxa = valorTaxa;
        this.quantidadeL = quantidadeL;
        this.quantidadeO = quantidadeO;

        this.mensagem = mensagem;
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
}
