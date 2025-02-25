package com.jyami;

import com.jyami.dto.UserDto;
import com.jyami.entity.User;
import com.jyami.repository.UserRepository;
import com.jyami.repository.UserRepositoryHashImpl;

public class JavaApplication {

    public static void main(String[] args) {
        UserRepository userRepository = new UserRepositoryHashImpl();

        // 유저 생성
        UserDto userDto = new UserDto("jyami", "jyami@kakao.com");
        User user = create(userRepository, userDto);

        // 유저 조회
        User user1 = findUser(userRepository, user.getId());
        System.out.println(user1.toString());

        // 유저 정보 업데이트
        UserDto updateUserDto = new UserDto("jyami2", "jyami2@kakao.com");
        updateUser(userRepository, user, updateUserDto);

        // 업데이트된 유저 정보 조회
        User user2 = findUser(userRepository, user.getId());
        System.out.println(user2.toString());

        // 없는 유저일 때 예외 발생 확인 / 예외 출력
        try {
            findUser(userRepository, 100);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    // User service
    public static User create(UserRepository userRepository, UserDto userDto) {
        return userRepository.create(userDto);
    }

    public static User findUser(UserRepository userRepository, long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("user not found"));
    }

    public static void updateUser(UserRepository userRepository, User user, UserDto updateUserDto) {
        userRepository.update(user, updateUserDto);
    }
}
