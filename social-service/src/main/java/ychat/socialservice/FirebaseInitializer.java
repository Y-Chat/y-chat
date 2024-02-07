package ychat.socialservice;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.internal.EmulatorCredentials;
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
            if(serviceAccount == null) {
                throw new RuntimeException("Error: firebase-service-credentials.json is missing from resources. Ask Ben Str. about it");
            }
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            firebaseOptionBuilder.setCredentials(credentials);

            FirebaseApp.initializeApp(firebaseOptionBuilder.build());
        }
    }

    /*
    For local testing:
    private void initializeFirebaseApp() throws IOException {
        if (FirebaseApp.getApps() == null || FirebaseApp.getApps().isEmpty()) {
            var firebaseOptionBuilder = FirebaseOptions.builder();
            firebaseOptionBuilder.setCredentials(new EmulatorCredentials())
                    .setProjectId("y-chat-e5132");
            FirebaseApp.initializeApp(firebaseOptionBuilder.build());
        }
    }
     */
}
