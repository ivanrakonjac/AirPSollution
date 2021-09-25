package com.ika.airpsollution.db;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseStorageDB {

    public static final String url = "https://airpsollution-default-rtdb.europe-west1.firebasedatabase.app";

    private static FirebaseStorage firebaseStorageInstace = null;
    private static StorageReference storageReference = null;
    private static StorageReference profilePhotosStorageReference = null;

    public static FirebaseStorage getFirebaseStorageInstance () {
        if (firebaseStorageInstace == null) {
            synchronized (FirebaseStorageDB.class) {
                // Double check pattern
                if (firebaseStorageInstace == null) {
                    firebaseStorageInstace = FirebaseStorage.getInstance();
                }
            }
        }

        return firebaseStorageInstace;

    }

    public static StorageReference getChatPhotosReference () {
        if (storageReference == null) {
            synchronized (FirebaseStorageDB.class) {
                // Double check pattern
                if (storageReference == null) {
                    storageReference = FirebaseStorageDB.getFirebaseStorageInstance().getReference().child("chat_photos");
                }
            }
        }

        return storageReference;

    }

    public static StorageReference getProfilePhotosReference () {
        if (profilePhotosStorageReference == null) {
            synchronized (FirebaseStorageDB.class) {
                // Double check pattern
                if (profilePhotosStorageReference == null) {
                    profilePhotosStorageReference = FirebaseStorageDB.getFirebaseStorageInstance().getReference().child("profile_photos");
                }
            }
        }

        return profilePhotosStorageReference;

    }

}
