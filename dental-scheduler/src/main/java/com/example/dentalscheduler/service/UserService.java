package com.example.dentalscheduler.service;

import com.example.dentalscheduler.mapper.UserMapper;
import com.example.dentalscheduler.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Profile(value = "!integrationTest")
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
}
