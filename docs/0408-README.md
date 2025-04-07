# CustomException 관리

커스텀 예외를 상황마다 무작정 생성하면 아래와 같은 문제 발생:
- 클래스 수 증가 → 유지보수 부담
- 비슷한 예외들이 중복 정의됨
- 예외 처리가 비일관적이 됨

## 해결 방법 : Exception + ErrorCode(Enum) 조합
- Exception 클래스는 공통으로 사용하고, ErrorCode Enum을 통해 예외 메시지를 관리한다.

```java
public enum ErrorCode {

    // 400 BAD_REQUEST
    INVALID_REQUEST(400, "G001", "올바르지 않은 인자입니다."),
    PRIVATE_CHANNEL_UPDATE_NOT_SUPPORTED(400, "C001", "비공개 채널은 수정할 수 없습니다."),
    PUBLIC_CHANNEL_UPDATE_NOT_SUPPORTED(400, "C002", "공개 채널 수정 권한이 없습니다."),
    USER_NOT_FOUND(400, "U001", "해당 사용자를 찾을 수 없습니다."),

    UNAUTHORIZED(401, "C003", "권한이 없습니다."),

    // 500 server error
    INTERNAL_SERVER_ERROR(500, "C004", "Internal Server Error");

    private final int httpStatus;
    private final String code;
    private final String message;

    ErrorCode(int httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
```

장점
- 새로운 예외는 ErrorCode Enum에 추가
- 모든 예외를 같은 방식으로 처리함
- 예외 메시지 관리가 용이함
- 메시지만 i18n 처리하면 글로벌 대응도 가능해짐.

```java

public record ErrorResponse(String code, String message) {

    public ErrorResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage());
    }

}


public class LogicException extends RuntimeException {
    private final ErrorCode errorCode;

    public LogicException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
```
우선은 LogicException으로 만들었으나. 이 LogicException 자체도 상속받은 예외를 만들 수 있다.
예를 들어 
- CriticalException을 만들고, 이 exception에서는 500관련 에러만 처리하거나
- NormalException을 만들고, 이 exception에서는 400관련 에러만 처리하는 식으로
이렇게 했을때 CriticalException은 좀더 서버입장에서 심각한 에러를 발생하는 exception이므로 이후에 알람을 보내는 등의 처리를 할 수 있다.

```java
@ControllerAdvice
public class GlobalHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalHandler.class); // SLF4J

    @ExceptionHandler(LogicException.class)
    public ResponseEntity<ErrorResponse> handleLogicException(LogicException e) {
        ErrorCode code = e.getErrorCode();
        return ResponseEntity.status(code.getHttpStatus())
            .body(new ErrorResponse(code));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        logger.error("Unexpected error: {}", e.getLocalizedMessage(), e);
        return ResponseEntity.status(500)
            .body(new ErrorResponse(ErrorCode.UNEXPECTED_ERROR));
    }

}
```

전역 UNEXPECTED_ERROR 처리
- 실제 exception의 e.getMessage를 클라이언트에 노출시키지 않는다.
- UNEXPECTED_ERROR는 500으로 처리하고, 클라이언트에는 "서버 오류" 메시지만 전달한다.
- 실제 서버의 오류의 내용은 서버 로그에 남겨 문제가 되는 부분을 이후에 디버깅한다.
- e.getMessage()는 민감한 정보가 포함될 수 있어 운영 환경에서는 숨기는 게 안전하다. throwable stack trace를 클라이언트에 노출시키지 않는 것도 같은 맥락이다.


### 추가적인 이야기 : String code를 왜 쓸까?

1. 동일한 HTTP 상태 코드라도 의미는 다를 수 있다
같은 400 http status 이더라도 클라이언트 입장에서는 별도의 예외처리를 해야할수도있다.
- 400 BAD_REQUEST
  - 잘못된 요청
  - 잘못된 파라미터
  - 잘못된 인증 정보
  - 이미 나간 채팅방
  - 초대가 불가능한 유저

ex)
```javascript
if (error.code === '001') {
    alert('이미 사용 중인 이메일입니다.');
} else if (error.code === '002') {
    showPasswordHint();
}
```

이런 코드들의 경우엔 클라이언트와의 규약이므로 001, 002가 아니더라도 간단한 문자열을 사용해도 괜찮다.
심지어 기존 ErrrorCode의 name()을 사용해도 괜찮다.
```java
public enum ErrorCode {
    INVALID_REQUEST(400, "올바르지 않은 인자입니다."),
    USER_NOT_FOUND(400, "해당 사용자를 찾을 수 없습니다."),
    UNAUTHORIZED(401, "권한이 없습니다."),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");
}

```

```java
@ExceptionHandler(LogicException.class)
public ResponseEntity<ErrorResponse> handleLogicException(LogicException e) {
    ErrorCode code = e.getErrorCode();
    return ResponseEntity.status(code.getHttpStatus())
        .body(new ErrorResponse(code.name(), code.getMessage()));
}
```

그러나 ErrorCode의 이름이 길어지거나 하는 경우를 클라이언트에서 비호할수도있으니 클라이언트와 규약을 만들어서 관리하기때문에 001, 002 등으로 처음에 정의한 것이다.
이때 문자열은 오타가 날 수 있고, 중복, 관리 어려움이 생기므로 이런 경우에도 enum을 함께 사용하는 방법이 있다.
아래와 같이 여러가지 방식이 있을 수 있을 것으로 보인다.
```java
public enum ErrorStatus {
    INVALID_REQUEST("001"),
    NOT_SUPPORT_CHANNEL("002"),
    NOT_FOUND("003"),
    UNAUTHORIZED("004"),
    INTERNAL_SERVER_ERROR("005");

    ErrorStatus(String status) {
    }
}

public enum ErrorStatus {
    C001("invalid_request"),
    C002("not support channel"),
    C003("not found"),

    A001("unAuthorized"),

    S001("internal server error");

    ErrorStatus(String description) {
    }
}
```

### 한번더 추가적인 이야기 userMessage가 무조껀 한국어일까?

현재 메시지는 영어권, 일본어권 유저가 봤을 때 의미가 없습니다.
글로벌 확장을 고려한다면 메시지를 코드화해서 다국어 대응을 쉽게 해야한다.

유저에게 내려주는 메시지도 설정으로 관리하면 된다. 

```
src/main/resources/
├── messages.properties        // 기본(영문)
├── messages_ko.properties     // 한국어
├── messages_ja.properties     // 일본어 (필요시)
```

```messages.properties
error.user.not-found=User not found
error.channel.exists=Channel already exists
error.channel.private-update=Private channel cannot be updated
```

```messages_ko.properties
error.user.not-found=사용자를 찾을 수 없습니다.
error.channel.exists=채널이 이미 존재합니다.
error.channel.private-update=비공개 채널은 수정할 수 없습니다.
```

org.springframework.context.support.ResourceBundleMessageSource 사용한다.
```java
@Configuration
public class MessageConfig {
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages"); // 메시지 파일 이름 (확장자 제외)
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }
}
```
이렇게 가져온 messageSource의 내용 대로 errorcode에서 가져온다.
즉, error code에서 어떤 메시지를 가져올지를 지정해야한다.

```java
public class LogicException extends RuntimeException {
    private final ErrorCode errorCode;

    public LogicException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage("error." + errorCode.name().toLowerCase(), null, locale);
    }
}
```

이부분은 시간되면 직접 라이브코딩을 해보는걸로
