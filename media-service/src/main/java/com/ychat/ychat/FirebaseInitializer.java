package com.ychat.ychat;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.StorageException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import com.ychat.ychat.services.SocialServiceConnector;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FirebaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseInitializer.class);

    private final SocialServiceConnector socialServiceConnector;

    public FirebaseInitializer(@Autowired SocialServiceConnector socialServiceConnector) {
        this.socialServiceConnector = socialServiceConnector;
    }

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

    Pattern chatIdPattern = Pattern.compile("chats/(.*?)/");

    public String generateSignedUrl(String objectName, UUID requesterId) {
        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            BlobInfo blobInfo = BlobInfo.newBuilder(bucket.asBucketInfo(), objectName).build();
            Blob blob = bucket.getStorage().get(blobInfo.getBlobId());
            if (blob == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Object not found");
            }

            if (objectName.startsWith("chats/")) {
                Matcher matcher = chatIdPattern.matcher(objectName);
                if(!matcher.find()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "objectName format is invalid");
                String chatId = matcher.group(1);
                if(!socialServiceConnector.canUserAccessChat(requesterId, UUID.fromString(chatId))) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
                }
            }
            return blob.signUrl(2, TimeUnit.HOURS).toString();
        } catch (StorageException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving object");
        }
    }
}
