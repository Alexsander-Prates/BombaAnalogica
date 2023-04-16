package Util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ConfigBD {

    private static FirebaseAuth auth;
    private static FirebaseFirestore db;
    private static FirebaseFirestore db2;



    public static FirebaseAuth FirebaseAutentic(){
        if(auth==null){
            auth=FirebaseAuth.getInstance();
        }
        return auth;
    }




    public static FirebaseFirestore FirebaseCadastroUser(){
        if(db==null){
            db=FirebaseFirestore.getInstance();
        }
        return db;
    }

    public static FirebaseFirestore FirebaseCadastroValor(){
        if(db2==null){
            db=FirebaseFirestore.getInstance();
        }
        return db2;
    }





}
