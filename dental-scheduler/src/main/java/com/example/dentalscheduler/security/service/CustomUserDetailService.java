package com.example.dentalscheduler.security.service;

import com.example.dentalscheduler.model.User;
import com.example.dentalscheduler.repository.UserRepository;
import com.example.dentalscheduler.security.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
@Profile(value = "!integrationTest")
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> byEmail = userRepository.findByUsername(username);
        if (byEmail.isEmpty()) {
            log.info("User {} not found", username);
            throw new UsernameNotFoundException("User not found");
        }

        User user = byEmail.get();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
        return new CustomUserDetails(user.getUsername(), user.getPassword(), Collections.singleton(authority), user.getId(), user);
    }
}
