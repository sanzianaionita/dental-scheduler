package com.example.dentalscheduler.config;

import com.example.dentalscheduler.enums.Role;
import com.example.dentalscheduler.model.User;
import com.example.dentalscheduler.repository.UserRepository;
import com.example.dentalscheduler.security.config.ConfigClass;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class PreProcessor implements ApplicationRunner {
    private final UserRepository userRepository;
    private final ConfigClass configClass;

    @Override
    public void run(ApplicationArguments args) {
        insertAdminUser();
    }

    private void insertAdminUser() {
        User appUser = new User();
        appUser.setRole(Role.ADMIN);
        appUser.setUsername("admin");
        appUser.setPassword(configClass.passwordEncoder().encode("admin"));
        appUser.setActive(true);
        Optional<User> byUsername = userRepository.findByUsername(appUser.getUsername());

        if (byUsername.isEmpty()) {
            userRepository.save(appUser);
        }

    }
}
