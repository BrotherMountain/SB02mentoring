package com.jyami.repository;

import com.jyami.dto.UserDto;
import com.jyami.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryListImplTest {
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryListImpl(); // 테스트 전에 새로운 저장소 초기화
    }

    @Test
    @DisplayName("유저 생성 및 조회 테스트")
    void testCreateAndFindById() {
        UserDto userDto = new UserDto("Alice", "alice@example.com");
        User user = userRepository.create(userDto);

        Optional<User> foundUser = userRepository.findById(user.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("Alice", foundUser.get().getName());
    }

    @Test
    @DisplayName("존재하지 않는 유저 조회 테스트")
    void testFindById_NotFound() {
        Optional<User> user = userRepository.findById(999L);
        assertFalse(user.isPresent(), "존재하지 않는 유저는 Optional.empty() 여야 함");
    }

    @Test
    @DisplayName("이메일로 유저 조회 테스트")
    void testFindByEmail() {
        UserDto userDto = new UserDto("Bob", "bob@example.com");
        User user = userRepository.create(userDto);

        User foundUser = userRepository.findByEmail("bob@example.com");
        assertEquals("Bob", foundUser.getName(), "이름이 일치해야 함");
    }

    @Test
    @DisplayName("이메일로 유저 조회 테스트")
    void testFindByEmail_NotFound() {
        UserDto userDto = new UserDto("Bob", "bob@example.com");
        User user = userRepository.create(userDto);

        assertThrows(IllegalArgumentException.class,
            () -> userRepository.findByEmail("jyami@kakao.com")
        );
    }

    @Test
    @DisplayName("유저 정보 업데이트 테스트")
    void testUpdateUser() {
        UserDto userDto = new UserDto("Alice", "alice@example.com");
        User user = userRepository.create(userDto);

        userRepository.update(user, new UserDto("Alice2", "alice2@example.com"));

        assertEquals("Alice2", user.getName());
        assertEquals("alice2@example.com", user.getEmail());
    }
}

