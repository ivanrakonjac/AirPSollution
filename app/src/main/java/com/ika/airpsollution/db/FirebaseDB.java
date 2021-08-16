package com.ika.airpsollution.db;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDB {

    public static final String url = "https://airpsollution-default-rtdb.europe-west1.firebasedatabase.app";

    private static FirebaseDatabase firebaseDbInstace = null;
    private static DatabaseReference messagesDbReference = null;

    public static FirebaseDatabase getFirebaseDBInstance () {
        if (firebaseDbInstace == null) {
            synchronized (FirebaseDB.class) {
                // Double check pattern
                if (firebaseDbInstace == null) {
                    firebaseDbInstace = FirebaseDatabase.getInstance(url);
                }
            }
        }

        return firebaseDbInstace;

    }

    public static DatabaseReference getMessagesDbReference () {
        if (messagesDbReference == null) {
            synchronized (FirebaseDB.class) {
                // Double check pattern
                if (messagesDbReference == null) {
                    messagesDbReference = FirebaseDB.getFirebaseDBInstance().getReference("messages");;
                }
            }
        }

        return messagesDbReference;

    }

}
