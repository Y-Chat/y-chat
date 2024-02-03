package ychat.socialservice;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    // https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter

    @Bean
    public SecurityFilterChain configureJWT(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable) // Is handled by api gateway
                .authorizeHttpRequests((auth) -> auth.anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .oauth2ResourceServer(config -> config.jwt((jwt) -> jwt.decoder(getDecoder())));
        return http.build();
    }

    public JwtDecoder getDecoder() {
        return new JwtDecoder() {
            @Override
            public Jwt decode(String token) throws JwtException {
                try {
                    var decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
                    var headers = new HashMap<String, Object>();
                    headers.put("alg", "HS256");
                    headers.put("typ", "JWT");
                    return new Jwt(decodedToken.getUid(), null, null, headers, decodedToken.getClaims());
                } catch (FirebaseAuthException e) {
                    throw new JwtException(e.getMessage());
                }
            }
        };
    }

    public static UUID getRequesterUUID() {
        return UUID.nameUUIDFromBytes(SecurityContextHolder.getContext().getAuthentication().getName().getBytes());
    }

    /**
     *
     * @deprecated Use {@link #getRequesterUUID()} instead. Only for debugging if correct auth is being passed
     */
    public static UUID getRequesterFirebaseAuthId() {
        return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
