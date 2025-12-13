package com.login.Login;

import com.login.Login.entities.Role;
import com.login.Login.entities.RoleType;
import com.login.Login.entities.User;
import com.login.Login.repository.RoleRepository;
import com.login.Login.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class LoginApplication {
	static UserRepository userRepository;
	static User user;
	static RoleRepository roleRepository;
	static Role role;
	public static void main(String[] args) {
		SpringApplication.run(LoginApplication.class, args);
	}

	@Bean
	CommandLineRunner init(RoleRepository roleRepository,
						   UserRepository userRepository,
						   BCryptPasswordEncoder passwordEncoder) {
		return args -> {
			// Initialize roles if not present
			if (roleRepository.count() == 0) {
				// Admin role
				Role adminRole = Role.builder()
						.roleName(RoleType.ADMIN)
						.build();
				roleRepository.save(adminRole);

				// User role
				Role userRole = Role.builder()
						.roleName(RoleType.USER)
						.build();
				roleRepository.save(userRole);
			}

			// Initialize admin user if not present
			if (userRepository.count() == 0) {
				Role adminRole = roleRepository.findByNameIgnoreCase("admin")
						.orElseThrow(() -> new RuntimeException("Admin role not found"));

				User adminUser = User.builder()
						.username("admin@examcrux.in")
						.password(passwordEncoder.encode("examcrux@123"))
						.role(adminRole)
						.build();

				userRepository.save(adminUser);
			}
		};
	}
}



