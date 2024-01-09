package com.test.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestApplication implements CommandLineRunner {

    @Value("${MONGODB_HOST:NONE}")
    private String host;

    @Value("${MONGODB_PORT:NONE}")
    private String port;

    @Value("${MONGODB_AUTH_DB:NONE}")
    private String authDb;

    @Value("${MONGODB_DB:NONE}")
    private String db;

    @Value("${MONGODB_USERNAME:NONE}")
    private String username;

    @Value("${MONGODB_PASSWORD:NONE}")
    private String password;

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // debugging inside a running container
        System.out.println("Host: " + host);
        System.out.println("Port: " + port);
        System.out.println("Auth DB: " + authDb);
        System.out.println("DB: " + db);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
    }
}
