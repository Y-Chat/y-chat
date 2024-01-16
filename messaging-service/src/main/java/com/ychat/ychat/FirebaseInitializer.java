package com.ychat.ychat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.internal.EmulatorCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

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
            firebaseOptionBuilder.setCredentials(new EmulatorCredentials()).setProjectId("y-chat-e5132");
            FirebaseApp.initializeApp(firebaseOptionBuilder.build());
        }
    }
}
