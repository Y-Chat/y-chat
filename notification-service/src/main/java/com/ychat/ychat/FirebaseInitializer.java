package com.ychat.ychat;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

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

    private void initializeFirebaseApp() throws IOException {
        if (FirebaseApp.getApps() == null || FirebaseApp.getApps().isEmpty()) {
            var firebaseOptionBuilder = FirebaseOptions.builder();
            // firebaseOptionBuilder.setCredentials(new EmulatorCredentials()).setProjectId("y-chat-e5132");

            InputStream serviceAccount = FirebaseInitializer.class.getResourceAsStream("/firebase-service-credentials.json");
            Assert.assertNotNull("Error: firebase-service-credentials.json is missing from resources. Ask Ben about it", serviceAccount);
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            firebaseOptionBuilder.setCredentials(credentials);

            FirebaseApp.initializeApp(firebaseOptionBuilder.build());
        }
    }
}
