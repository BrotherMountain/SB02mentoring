# dto를 왜 써야할까

1. 요청(Request)과 응답(Response) 데이터를 분리하여 안전성 확보
엔티티(Entity)를 직접 사용하면, 클라이언트가 **불필요한 정보**를 보거나 수정할 위험이 있음.
User 엔티티에 password 필드가 있을때
요청의 관점 : 만약 username 수정 요청이라면? password 필드가 포함되어 있으면 보안상 문제가 발생할 수 있음.
응답의 관점 : entity만 사용하면 응답시 그대로 노출.
DTO를 사용하면 필요한 데이터만 포함할 수 있어 보안성을 높이고, API 응답 구조를 명확하게 만들 수 있다!

그래서 개인적으론 아래와 같이 많이 사용함.

```java
public record UserCreateRequestDto (
    String name,
    String email,
    String password  // 요청에는 포함
) {}
public class UserResponseDto (
    Long id,       // 다음 조회를 위해 응답에는 포함
    String name,
    String email  // 응답에는 비밀번호 제외
) {}
public class UserUpdateRequestDto (
    Long id,        // 수정할 대상을 찾기 위해 요청에는 포함
    String name    // 수정할 필드 데이터만 포함
) {}

```

2. 엔티티(Entity)의 수정이 DTO에 영향을 주지 않도록 함
   엔티티를 그대로 사용하면, DB 스키마 변경이 API 설계에 직접적인 영향을 줄 수 있음.

기존 클라이언트에서 address 필드를 받지 않는다고 가정하면, 갑작스런 응답 구조 변경이 클라이언트에 영향을 줄 수 있음.
API 응답에서 원하지 않는 필드가 포함되므로 **호환성 문제가 발생**하고, 
- 호환성 문제란? Web 클라이언트는 변경사항을 빠르게 반영 가능하지만, 모바일 클라이언트는 변경사항을 반영하기까지 시간이 걸림. 
- 만약 특정 app 버전에서 address 필드를 사용하지 않는다고 가정하면?, 해당 필드가 포함되어 있으면 불필요한 데이터를 전송하게 되어 **네트워크 자원 낭비**가 발생할 수 있음.
- 반대로 서버에서 name 필드를 사용하다가 사용하지 않게 된다면? 클라이언트에서 name 필드를 사용하고 있을 수 있으므로 특정버전에선 api사용이 불가능하게되는데 고칠방법이 없게됌.


# Entity ↔ DTO 변환

## 1. 수동 변환

```java
public class ReadStatusMapper {
    
    // Entity → DTO 변환
    public static ReadStatusDto toDto(ReadStatus readStatus) {
        return new ReadStatusDto(
            readStatus.getId(),
            readStatus.getUser().getId(),
            readStatus.getChannel().getId(),
            readStatus.getLastReadAt()
        );
    }

    // DTO → Entity 변환
    public static ReadStatus toEntity(ReadStatusDto dto, User user, Channel channel) {
        return new ReadStatus(user, channel, dto.getLastReadAt());
    }
}
```

- 장점: 직관적이고 제어가 용이함.
- 단점: 변환할 필드가 많아지면 코드가 길어짐. 
- 위치를 어디에 둘지에 대한 고민필요.
- 보통 entity는 1개 dto는 여러개일 가능성이 있으므로 entity는 dto의 존재를 모르는게 좋다는 관점에서 dto에 변환 메서드를 두는걸 선호한다.

## 2. MapStruct 활용

### 2.1. 의존성 추가

```gradle
dependencies {
    implementation 'org.mapstruct:mapstruct:1.4.2.Final'
    annotationProcessor 'org.mapstruct:mapstruct-processor:1.4.2.Final'
}
```

