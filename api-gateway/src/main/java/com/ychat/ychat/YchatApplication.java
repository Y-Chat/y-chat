package com.ychat.ychat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class YchatApplication {

	public static void main(String[] args) {
		SpringApplication.run(YchatApplication.class, args);
	}

	@Value("${ychat.gateway.redirect.target.auth}")
	private String authServiceURL;

	@Value("${ychat.gateway.redirect.target.social}")
	private String socialServiceURL;

	@Value("${ychat.gateway.redirect.target.messaging}")
	private String messagingServiceURL;

	@Value("${ychat.gateway.redirect.target.notification}")
	private String notificationServiceURL;

	@Value("${ychat.gateway.redirect.target.payment}")
	private String paymentServiceURL;

	@Value("${ychat.gateway.redirect.target.media}")
	private String mediaServiceURL;

	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p
						.path("/auth/*")
						.uri(authServiceURL))
				.route(p -> p
						.path("/social/*")
						.uri(socialServiceURL))
				.route(p -> p
						.path("/messaging/*")
						.uri(messagingServiceURL))
				.route(p -> p
						.path("/notification/*")
						.uri(notificationServiceURL))
				.route(p -> p
						.path("/payment/*")
						.uri(paymentServiceURL))
				.route(p -> p
						.path("/media/*")
						.uri(mediaServiceURL))
				.build();
	}
}
