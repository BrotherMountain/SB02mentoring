# 0326


1. @Value란?

application.properties 또는 application.yaml에 정의된 설정값을 가져오기 위한 Spring 애노테이션
일반적으로 외부 설정을 코드에 하드코딩하지 않고 주입하는 데 사용

2. @Value 사용법

- application.properties
```properties
discodeit.repository.type=file
discodeit.repository.file.user=user.dat
discodeit.repository.file.channel=channel.dat
discodeit.repository.file.message=message.dat
```

- application.yaml
```yaml
discodeit:
  repository:
    type: file
    file:
      user: user.dat
      channel: channel.dat
      message: message.dat
```

- @Value 사용
```java
@Value("${discodeit.repository.type:file}")
private String repositoryType;
```
설정이 없을 경우 "defaultValue"가 자동으로 사용

다른 타입도 자동으로 변환
```java
@Value("${batch.size:100}")
private int batchSize; // 문자열 "100"을 int로 자동 변환
```
```java
@Value("${feature.enabled:true}")
private boolean enabled; // 문자열 "true"를 boolean으로 자동 변환
```

@Value를 메서드나 생성자에도 사용 가능
```java
@Bean
public MyService myService(@Value("${service.name}") String serviceName) {
    return new MyService(serviceName);
}
```


# Spring 
JavaApplication vs DiscodeitApplication: 서비스 초기화 방식 비교

1. IoC Container (제어의 역전 컨테이너)

객체 생성과 생명주기를 직접 관리하지 않고, Spring이 대신 관리하는 구조

- DiscodeitApplication에서는 Spring Boot가 실행되면서 내부적으로 IoC 컨테이너(ApplicationContext) 를 생성함
- 모든 @Component, @Service, @Repository, @Bean 객체들을 IoC 컨테이너에 등록하고, 필요할 때 자동으로 주입

```java
ApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
```

반면 JavaApplication에서는 IoC 컨테이너 없이 개발자가 객체를 직접 new로 생성함

2. Dependency Injection (DI)

필요한 객체(의존성)를 외부에서 주입받는 구조
Spring은 DI를 통해 Service, Repository 등을 자동 주입함

```java
@Service
public class UserService {
    public UserService(UserRepository repository) { ... }
}
```

java Application에서는 DI를 직접 구현해야 함

```java
UserRepository userRepository = new InMemoryUserRepository();
UserService userService = new UserService(userRepository);
```

3. Bean

Spring IoC 컨테이너가 관리하는 객체

- @Component, @Service, @Bean 등으로 정의된 객체는 모두 Bean으로 등록됨
-  컨테이너는 Bean을 싱글턴으로 관리하며, 필요 시 자동으로 주입

```java
@Component
public class MainMenuController {
    
}
```

반면 JavaApplication에서는 Bean이라는 개념 없이 직접 생성된 객체만 존재

4. @Bean 직접 등록하는 방법

Spring이 객체를 직접 관리할 수 있도록, 개발자가 직접 정의해서 IoC 컨테이너에 등록하는 방식

- 일반적으로 @Configuration 클래스 내부에서 사용
- 라이브러리, 직접 생성한 객체 등 @Component로 스캔할 수 없는 객체를 등록할 때 자주 사용

```java
@Configuration
public class AppConfig {

    @Bean
    public UserService userService() {
        return new UserService(new UserRepository());
    }
}
```
- @Bean은 메서드 레벨에서 사용 
- 이 메서드의 리턴값이 Bean으로 등록됨 
- userService() 메서드가 호출되지 않아도 Spring이 알아서 객체를 생성해 등록함 (싱글턴으로)

```java
@Configuration
public class AppConfig {

    @Bean
    public UserRepository userRepository() {
        return new UserRepository(); // 실제 구현체 반환
    }

    @Bean
    public UserService userService(UserRepository userRepository) {
        return new UserService(userRepository); // 위에서 만든 Bean이 자동 주입됨
    }
}
```
@Bean 메서드 간의 의존성은 파라미터로 선언하면 Spring이 자동 주입해줌. (DI)


----

# 같은 타입일때 하나의 의존성을 주입하는 방법

```java
public interface UserRepository { ... }

@Component
public class JCFUserRepository implements UserRepository { ... }

@Component
public class FileUserRepository implements UserRepository { ... }
```

이 경우, Spring은 어떤 구현체를 주입해야 할지 모름
그래서 애플리케이션 실행 시 NoUniqueBeanDefinitionException이 발생

1. @Primary 사용

```java
@Component
@Primary // 이 구현체를 기본으로 사용하겠다
public class JCFUserRepository implements UserRepository { ... }
```
```java
@Autowired
private UserRepository userRepository; // → JCFUserRepository 주입됨
```
여러 Bean 중 하나를 '기본값'으로 사용하고 싶을 때 사용

2. @Qualifier 사용

```java
@Component("fileUserRepository")
public class FileUserRepository implements UserRepository { ... }

@Component("jcfUserRepository")
public class JCFUserRepository implements UserRepository { ... }

@Autowired
@Qualifier("fileUserRepository")
private UserRepository userRepository;
```

여러 Bean 중 특정 Bean을 선택하고 싶을 때 사용

3. Bean 수동 등록시 직접 선택

```java

@Configuration
public class RepositoryConfig {

    @Bean
    public UserRepository userRepository() {
        return new FileUserRepository(); // 수동으로 어떤 구현체를 쓸지 지정
    }
}
```
빈 등록 자체를 명시적으로 하고 싶은 경우에 사용

4. 설정 기반으로 빈을 등록하는 방법

```java
@Configuration
public class RepositoryConfig {

    @Value("${discodeit.repository.type:file}")
    private String repositoryType;

    @Bean
    public UserRepository userRepository() {
        if ("file".equals(repositoryType)) {
            return new FileUserRepository();
        } else {
            return new JCFUserRepository();
        }
    }
}
```

```java
@Configuration
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileRepositoryConfig {
    @Bean
    public UserRepository userRepository() {
        return new FileUserRepository();
    }
}

@Configuration
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFRepositoryConfig {
    @Bean
    public UserRepository userRepository() {
        return new JCFUserRepository();
    }
}
```












