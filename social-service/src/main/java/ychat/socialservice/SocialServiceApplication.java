package ychat.socialservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// TODO look at fetch types for JPA
// TODO think about adding timestamps to DB
// TODO change additional parameters to query
// Go through all controllers to have error handling
// TODO fit everything to column
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class SocialServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialServiceApplication.class, args);
	}

}
