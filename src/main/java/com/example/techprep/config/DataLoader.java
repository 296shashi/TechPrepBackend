package com.example.techprep.config;

import com.example.techprep.entity.User;
import com.example.techprep.entity.Admin;
import com.example.techprep.repository.UserRepository;
import com.example.techprep.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "296shashi@gmail.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail(adminEmail);
            // default password — please change in production
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRoles("USER,ADMIN");
            userRepository.save(admin);
            System.out.println("Created default admin user: " + adminEmail);
        }

        // ensure admin row exists for Google token storage
        if (!adminRepository.existsByEmail(adminEmail)) {
            Admin a = new Admin();
            a.setName("Admin");
            a.setEmail(adminEmail);
            adminRepository.save(a);
            System.out.println("Created admin token row: " + adminEmail);
        }
    }
}
