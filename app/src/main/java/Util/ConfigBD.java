package Util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class ConfigBD {

    private static FirebaseAuth auth;
    private static FirebaseFirestore db;
    private static FirebaseStorage db2;



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

    public static FirebaseStorage FirebaseCadastroStorage(){
        if(db2==null){
            db2=FirebaseStorage.getInstance();
        }
        return db2;
    }





}
