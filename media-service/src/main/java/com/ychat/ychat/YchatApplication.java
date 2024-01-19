package com.ychat.ychat;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;

@SpringBootApplication
public class YchatApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(YchatApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
