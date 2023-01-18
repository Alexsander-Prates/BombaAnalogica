package model;

public class Autos {

    private String clube,outros,autoID,auto;

    public  Autos(){

    }

    public Autos(String clube, String outros, String auto) {
        this.clube = clube;
        this.outros = outros;
        this.auto = auto;
    }

    public Autos(String clube, String outros, String autoID, String auto) {
        this.clube = clube;
        this.outros = outros;
        this.autoID = autoID;
        this.auto = auto;
    }


    public String getAuto() {
        return auto;
    }

    public void setAuto(String auto) {
        this.auto = auto;
    }


    public String getClube() {
        return clube;
    }

    public void setClube(String clube) {
        this.clube = clube;
    }

    public String getOutros() {
        return outros;
    }

    public void setOutros(String outros) {
        this.outros = outros;
    }

    public String getAutoID() {
        return autoID;
    }

    public void setAutoID(String autoID) {
        this.autoID = autoID;
    }
}
