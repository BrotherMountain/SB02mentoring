package com.jyami.repository;

import com.jyami.dto.UserDto;
import com.jyami.entity.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepositoryHashImpl implements UserRepository {

    private final HashMap<Long, User> users = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public User create(UserDto userDto) {
        User user = new User(idGenerator.getAndIncrement(), userDto.name(), userDto.email());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(long userId) {
        User user = users.get(userId);
        return Optional.ofNullable(user);
    }

    @Override
    public User findByEmail(String email) {
        return users.values().stream().filter(user -> user.getEmail().equals(email)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
    }


    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void update(User user, UserDto userDto) {
        // 데이터베이스가 아니라서 update 메소드가 필요 없을 수도 있음
        user.setName(userDto.name());
        user.setEmail(userDto.email());
    }

    @Override
    public void delete(long userId) {
        throw new IllegalArgumentException("TODO implementation");
    }

}
