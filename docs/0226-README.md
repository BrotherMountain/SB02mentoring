## 인터페이스

인터페이스(interface)는 클래스가 따라야 할 행동(메서드의 집합)을 정의하는 일종의 계약(contract)입니다.
구현체 없이 메서드의 시그니처(정의)만 제공하며, 이를 구현하는 클래스는 반드시 해당 메서드를 구현해야 합니다.

예제에서 UserRepository는 인터페이스이며, 이를 구현하는 UserRepositoryHashImpl과 UserRepositoryListImpl이 실제 로직을 담당합니다.

### 왜 사용해야하는가

1) 유연성

인터페이스를 사용하면 구현체를 변경하더라도 코드 변경을 최소화할 수 있습니다.

예제에서는 해시맵 기반 저장소(UserRepositoryHashImpl)와 리스트 기반 저장소(UserRepositoryListImpl) 두 가지 구현체를 만들었습니다.
인터페이스가 없다면, 특정 구현체에 의존해야 하므로 교체가 어렵습니다.

```java
UserRepository userRepository = new UserRepositoryHashImpl(); // HashMap 기반 저장소 사용
// UserRepository userRepository = new UserRepositoryListImpl(); // List 기반 저장소로 변경 (한 줄만 수정하면 됨)
```
- HashMap 기반 구현 : ID를 키(key)로 활용 → 검색 속도가 O(1). (ID 기반 검색시에만 - DB의 index와 비슷한 역할을 하도록 함.)
- List를 사용하여 데이터를 순차적으로 저장. → 검색 시 O(N)의 성능을 가지므로, 데이터가 많아지면 속도가 느려짐.

2) 확장성
새로운 저장 방식이 필요하면, 기존 코드를 변경하지 않고 새로운 클래스를 추가하면 됨.
```java
public class UserRepositoryTreeMapImpl implements UserRepository {
    // TreeMap을 사용한 저장소 구현 (ID 정렬 지원)
}
```

```java
public class UserRepositoryDBImpl implements UserRepository {
    // Database를 사용한 저장소 구현 
}
```

```java
public class UserRepositoryRedisImpl implements UserRepository {
    // Redis를 사용한 저장소 구현 
}
```

---

## Optional 과 예외처리

- findById(long userId): Optional<User>을 반환
- findByEmail(String email): User를 직접 반환하고, 값이 없으면 예외 발생


#### 1. Optional을 사용하는 경우
```java
@Override
public Optional<User> findById(long userId) {
    return Optional.ofNullable(userStorage.get(userId));
}
```
- 메서드 호출하는 쪽에서 빈 값에 대한 처리를 강제할 수 있음. (null 체크를 강제하지 않음)
- 예외 발생을 강제하지 않고, 다양한 처리 방법을 제공
- 결과가 존재하지 않을 경우 : 메서드를 호출하는 바깥 코드에서 예외를 처리하도록 강제하도록 한다.

```java
Optional<User> userOptional = userRepository.findById(1L);
User user = userOptional.orElseThrow(() -> new UserNotFoundException("User not found"));
System.out.println("User found: " + user.getName());
```

#### 2. Optional을 사용하지 않는 경우
```java
@Override
public User findByEmail(String email) {
    return userStorage.values().stream()
        .filter(user -> user.getEmail().equals(email))
        .findFirst()
        .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
}
```
- 이 메서드를 호출하면 반드시 User가 존재해야 한다는 보장이 있음.
- API 설계 측면에서, “이메일로 검색할 때 무조건 유저가 있어야 한다”는 의미를 전달 가능.
- 메서드를 호출하는 곳에서 null 체크를 하지 않아도 됨.

```java
User user = userRepository.findByEmail("alice@example.com");
System.out.println("User found: " + user.getName());
```

- 메서드 내부에서 즉시 예외를 던지므로, 호출하는 쪽에서는 예외 처리를 별도로 하지 않아도 됨.
- 반드시 예외가 발생하므로, Optional과 달리 빈 값에 대한 후처리 코드가 필요 없음.


#### 언제 사용해야할까?
언제 Optional을 사용해야 할까?
- 해당 데이터가 없을 가능성이 있는 경우.
- 예외 대신 기본값을 사용하거나 다른 처리를 하고 싶은 경우.
- 데이터가 없을 때 반드시 예외를 던질 필요가 없는 경우.

#### 언제 Optional을 사용하지 말아야 할까?
- 반드시 값이 존재해야 하는 경우.
- 데이터가 없을 때 예외를 던지는 것이 논리적으로 더 적절한 경우.
- 데이터가 없으면 로직이 더 이상 진행될 수 없는 경우.

----

### junit5 테스트코드

#### Main 함수 기반 테스트

```java
public class Main {
    public static void main(String[] args) {
        UserRepository userRepository = new UserRepositoryHashImpl(); // Hash 기반 저장소
        userRepository.create(new UserDto("Alice", "alice@example.com"));

        Optional<User> user = userRepository.findById(1L);
        if (user.isPresent()) {
            System.out.println("User found: " + user.get().getName());
        } else {
            System.out.println("User not found.");
        }
    }
}
```

- 프로그램을 실행하여 직접 결과를 확인해야 함.
- System.out.println()으로 결과를 출력하고 사람이 직접 판단.
- 자동화가 어렵고, 반복적으로 실행하기 불편함.
- 특정 조건을 테스트하기 어렵고 예외 발생 시 전체 프로그램이 종료될 수도 있음.

-----

## JUnit5 테스트 코드

Main 함수를 이용한 테스트는 개발 중 빠르게 동작을 확인할 때는 유용하지만, 유지보수나 반복 테스트에 불편함이 있다.

- assertEquals(), assertTrue(), assertFalse() 등을 사용하여 자동으로 결과를 검증.
- @BeforeEach를 사용하여 테스트마다 새로운 환경을 제공하여 독립적인 테스트 가능.
- @Test, @DisplayName을 사용하여 테스트를 명확하게 정의.
- 예외가 발생하면 테스트 프레임워크가 자동으로 실패를 감지하여 사람이 직접 확인할 필요 없음.

결론 : 다양한 조건에 따른 테스트를 매번 main java 파일을 고치지 않고도 확인이 가능하다.

#### 실무자 관점

- 실무에서는 끊임없이 로직이 변경된다.
  - 새로운 기능을 추가하거나 기존 로직을 수정할 때, 예상치 못한 부작용(Bug)이 발생할 가능성이 큼.
  - 특히, 여러 기능이 복잡하게 얽혀 있는 서비스에서는 하나의 변경이 다른 기능에 영향을 미칠 수 있음.

- 테스트는 변경 전후의 동작을 보장하는 유일한 방법이다.
  - 기존 코드에 대해 자동화된 테스트가 있다면, 기능을 추가하거나 수정한 후에도 기존 기능이 정상 동작함을 보장할 수 있음.
  - 테스트가 없다면, 매번 수동으로 전체 기능을 확인해야 하며, 이는 비효율적이고 사람이 실수할 가능성이 높음.

- 테스트가 없다면?
  - 기능 추가 후 다른 코드가 깨졌는지 직접 확인해야 하는 부담이 커짐.
  - 변경 후 예상치 못한 버그가 발생해, 배포 후에 문제가 발견될 가능성이 높음.
  - 버그 수정 과정에서 또 다른 문제를 만들 수 있어, 유지보수 비용이 기하급수적으로 증가함.

- 테스트가 있다면?
  - 코드 변경 후 테스트를 실행하는 것만으로 기존 기능이 정상 동작하는지 확인 가능.
  - 예상하지 못한 변경이 발생했을 때, 어떤 부분이 영향을 받았는지 빠르게 확인할 수 있음.
  - 지속적인 리팩토링이 가능해지고, 안정적으로 새로운 기능을 추가할 수 있음.
 
