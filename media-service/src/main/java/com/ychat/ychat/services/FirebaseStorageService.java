package com.ychat.ychat.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class FirebaseStorageService {

    @PostConstruct
    public void initializeFirebaseApp() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("firebase/serviceAccountKey.json");

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("y-chat-e5132.appspot.com")
                .build();
        FirebaseApp.initializeApp(options);


    }

    public String generateSignedUrl(String objectName) {
        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            BlobInfo blobInfo = BlobInfo.newBuilder(bucket.asBucketInfo(), objectName).build();
            Blob blob = bucket.getStorage().create(blobInfo);
            return blob.signUrl(10, TimeUnit.MINUTES).toString();
        } catch (StorageException e) {
            // Handle storage exception
            e.printStackTrace();
            return null;
        }
    }
}
