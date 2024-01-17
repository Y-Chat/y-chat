package com.ychat.ychat.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
            Blob blob = bucket.getStorage().get(blobInfo.getBlobId());

            if (blob == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Object not found");
            }

            // TODO if not authenticated for whatever reason (not part of chat etc.) throw 403
            // TODO add caching for frequently accessed objects?

            return blob.signUrl(2, TimeUnit.HOURS).toString();
        } catch (StorageException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving object");
        }
    }
}
