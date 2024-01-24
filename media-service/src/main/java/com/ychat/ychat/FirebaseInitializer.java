package com.ychat.ychat;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.StorageException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
public class FirebaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseInitializer.class);

    @PostConstruct
    public void onStart() {
        logger.info("Initializing Firebase App...");
        try {
            this.initializeFirebaseApp();
        } catch (IOException e) {
            logger.error("Initializing Firebase App", e);
        }
    }

    public void initializeFirebaseApp() throws IOException {
        InputStream serviceAccount = FirebaseInitializer.class.getResourceAsStream("/firebase-service-credentials.json");
        Assert.assertNotNull("Error: firebase-service-credentials.json is missing from resources. Ask Ben about it", serviceAccount);
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setStorageBucket("y-chat-e5132.appspot.com")
                .build();
        FirebaseApp.initializeApp(options);
    }

    public String generateSignedUrl(String objectName) {
        try {
            logger.info("1"); // TODO remove
            Bucket bucket = StorageClient.getInstance().bucket();
            BlobInfo blobInfo = BlobInfo.newBuilder(bucket.asBucketInfo(), objectName).build();
            Blob blob = bucket.getStorage().get(blobInfo.getBlobId());
            logger.info("2 {}", blob); // TODO remove
            if (blob == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Object not found");
            }

            if (objectName.startsWith("chats/")) {
                // TODO check from social service if user is part of chat otherwise return 403
            }
            return blob.signUrl(2, TimeUnit.HOURS).toString();
        } catch (StorageException e) {
            logger.error(e.toString());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving object");
        }
    }
}
