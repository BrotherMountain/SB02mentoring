package com.jyami.repository;

import com.jyami.dto.UserDto;
import com.jyami.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User create(UserDto userDto);
    Optional<User> findById(long userId);
    User findByEmail(String email);
    List<User> findAll();
    void update(User user, UserDto userDto);
    void delete(long userId);
}
