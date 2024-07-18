package com.SB1.configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.SB1.enums.Role;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
	@Value("${jwt.signerKey}")
	private String SIGNER_KEY;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
//        http
//            .csrf(Customizer.withDefaults())
//            .authorizeHttpRequests(authorize -> authorize
//                .anyRequest().authenticated()
//            )
//            .httpBasic(Customizer.withDefaults())
//            .formLogin(Customizer.withDefaults());
    	
    	httpSecurity.authorizeHttpRequests(t ->                            // "/user" truy cap ko can authenticated
    			 t.requestMatchers(HttpMethod.POST, "/users", "/users/my-info").permitAll()
    			.requestMatchers(HttpMethod.POST, "auth/token", "auth/introspect").permitAll()  //phan quyen tren endpoint
    			//.requestMatchers(HttpMethod.GET, "/users").hasRole(Role.ADMIN.name())     //quyen admin
    			.anyRequest().authenticated());
    	
    	httpSecurity.csrf(t -> t.disable());   //tat csrf
    	
    	httpSecurity.oauth2ResourceServer(t ->
    	t.jwt(t1 -> t1.decoder(jwtDecoder())
    			.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return httpSecurity.build();
    }
    
    @Bean   // Chuyen SCOPE -> ROLE
    JwtAuthenticationConverter jwtAuthenticationConverter() 
    {
    	JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    	jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
    	JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    	jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
    	return jwtAuthenticationConverter;
    }
    

    @Bean
    JwtDecoder jwtDecoder() 
    {
    	SecretKeySpec secretKey = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS512");									// tao Secret key
    	return NimbusJwtDecoder.withSecretKey(secretKey)
    			.macAlgorithm(MacAlgorithm.HS512)
    			.build();
    }
    
    @Bean
    PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder(10);
    }
    
}