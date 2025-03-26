package com.jyami.service;

import com.jyami.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    // @Qualifier("userRepositoryListImpl")
    public UserService(
        @Qualifier("userRepositoryListImpl") UserRepository userRepository,
        @Value("${project.name:hello}") String name
    ) {
        this.userRepository = userRepository;
        System.out.println("project name: " + name);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }
}
