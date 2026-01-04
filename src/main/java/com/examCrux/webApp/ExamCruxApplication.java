package com.examCrux.webApp;

import com.examCrux.webApp.entities.Role;
import com.examCrux.webApp.entities.RoleType;
import com.examCrux.webApp.entities.User;
import com.examCrux.webApp.repository.RoleRepository;
import com.examCrux.webApp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ExamCruxApplication {
	static UserRepository userRepository;
	static User user;
	static RoleRepository roleRepository;
	static Role role;
	public static void main(String[] args) {
		SpringApplication.run(ExamCruxApplication.class, args);
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
						.roleName(RoleType.ADMIN.name())
						.build();
				roleRepository.save(adminRole);

				// User role
				Role userRole = Role.builder()
						.roleName(RoleType.USER.name())
						.build();
				roleRepository.save(userRole);
			}

			// Initialize admin user if not present
			if (userRepository.count() == 0) {
				Role adminRole = roleRepository.findByRoleNameIgnoreCase("admin")
						.orElseThrow(() -> new RuntimeException("Admin role not found"));

				User adminUser = User.builder()
						.username("sam.17mittal@gmail.com")
						.role(adminRole)
						.profileImage(null)
						.active(true)
						.build();

				userRepository.save(adminUser);
			}
		};
	}
}



