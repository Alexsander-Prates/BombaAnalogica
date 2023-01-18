package Util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ConfigBD {

    private static FirebaseAuth auth;
    private static FirebaseFirestore db;



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

}
