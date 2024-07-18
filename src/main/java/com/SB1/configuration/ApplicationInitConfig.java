package com.SB1.configuration;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.SB1.entity.User;
import com.SB1.enums.Role;
import com.SB1.repository.UserRepository;

@Configuration
public class ApplicationInitConfig {
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Bean
	ApplicationRunner applicationRunner(UserRepository userRepository)
	{
		return args -> {
			if(userRepository.findByUsername("admin").isEmpty()) 
			{
				var roles = new HashSet<String>();
				roles.add(Role.ADMIN.name());
				
				User user = User.builder()
						.username("admin")
						.password(passwordEncoder.encode("admin"))
						.roles(roles)
						.build();
				
				userRepository.save(user);
			}
			
		};
	}
}
