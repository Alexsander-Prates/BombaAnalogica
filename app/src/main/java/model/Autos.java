package model;

import com.google.firestore.admin.v1.Index;

public class Autos extends User {

    private String clube,outros,id,auto,desc, photo, photoKey,date;
    Index index;



    public  Autos(){

    }

    public Autos(String date, String photoKey, String clube, String outros, String id, String auto, String desc, String photo, Index index) {
        this.clube = clube;
        this.outros = outros;
        this.id = id;
        this.auto = auto;
        this.desc = desc;
        this.photo = photo;
        this.index = index;
        this.photoKey = photoKey;
        this.date = date;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Index getIndex() {
        return index;
    }

    public void setIndex(Index index) {
        this.index = index;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPhotoKey() {
        return photoKey;
    }

    public void setPhotoKey(String photoKey) {
        this.photoKey = photoKey;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
