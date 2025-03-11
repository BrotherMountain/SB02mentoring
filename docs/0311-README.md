# File 동기화

파일 기반 저장소(FileRepository)를 사용할 때, 데이터 일관성과 무결성을 보장하는 것은 데이터베이스에서의 트랜잭션 처리와 유사한 문제를 가집니다.
특히, 여러 개의 스레드가 동시에 파일을 읽고 쓸 경우, 데이터가 손상될 가능성이 높습니다.

따라서, 파일을 저장하는 과정에서 트랜잭션처럼 안정적으로 데이터를 저장하고, 오류가 발생했을 때 롤백할 수 있는 구조가 필요합니다.
이번 자료에서는 파일 트랜잭션 개념과 이를 적용하는 방법을 살펴보겠습니다.

### 문제 정의


```java
public class FileChannelRepository {
    private final String fileName = "channel.ser";
    private final Map<UUID, Channel> channelMap;

    public FileChannelRepository() {
        this.channelMap = loadChannelList();
    }

    public void saveChannelList() {
        try (FileOutputStream fos = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(channelMap);
        } catch (IOException e) {
            throw new RuntimeException("데이터를 저장하는데 실패했습니다.", e);
        }
    }

    public Map<UUID, Channel> loadChannelList() {
        try (FileInputStream fis = new FileInputStream(fileName);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Object channelMap = ois.readObject();
            return (Map<UUID, Channel>) channelMap;
        } catch (FileNotFoundException e) {
            return new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("데이터를 불러오는데 실패했습니다", e);
        }
    }

}

```

문제점
1. 파일을 캐싱하는 메모리(Map)를 사용하지만, 동기화 처리가 안 되어 있음
  → 다중 스레드 접근 시 동기화 문제 발생 가능
2. 파일 저장 중 오류 발생 시 데이터가 손상될 가능성이 있음 
  → saveChannelList()에서 파일을 덮어쓰는 도중 오류가 발생하면 파일이 손상됨
3. 파일 읽기(load)와 저장(save) 과정이 원자적(Atomic)이지 않음 
  → 여러 스레드가 동시에 loadChannelList()를 실행하면, 기존 데이터를 불러오는 도중 파일이 변경될 가능성이 있음

### 1. 파일 트랜잭션
파일 저장 시 임시 파일(temp)을 활용하여 원자성(Atomicity) 보장

파일을 덮어쓰기 전에 임시 파일을 생성하고, 모든 데이터가 정상적으로 저장되면 기존 파일을 교체하는 방식
이 방식은 데이터가 손상되지 않도록 보호하며, 저장 도중 오류가 발생하면 기존 데이터를 유지할 수 있음.

```java
public void saveChannelList() {
    File tempFile = new File(fileName + ".temp");
    File originalFile = new File(fileName);

    try (FileOutputStream fos = new FileOutputStream(tempFile);
         ObjectOutputStream oos = new ObjectOutputStream(fos)) {
        oos.writeObject(channelMap);
        oos.flush(); // 파일이 완전히 쓰여졌음을 보장
    } catch (IOException e) {
        throw new RuntimeException("데이터를 저장하는데 실패했습니다.", e);
    }

    // 기존 파일을 삭제하고, 임시 파일을 원본 파일로 변경
    if (!tempFile.renameTo(originalFile)) {
        throw new RuntimeException("파일 교체 중 오류 발생");
    }
}
```

### 2. 파일을 읽을 때 다중 스레드 안전하게 만들기 (synchronized 적용)

여러 스레드가 동시에 파일을 로드하는 문제를 방지하기 위해 synchronized 키워드를 적용
ConcurrentHashMap을 사용하여 스레드 안전한 Map을 활용할 수도 있음

```java
public synchronized Map<UUID, Channel> loadChannelList() {
    File file = new File(fileName);
    
    if (!file.exists()) {
        return new HashMap<>();
    }

    try (FileInputStream fis = new FileInputStream(file);
         ObjectInputStream ois = new ObjectInputStream(fis)) {
        return (Map<UUID, Channel>) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException("데이터를 불러오는데 실패했습니다.", e);
    }
}
```

### 3. 데이터 동기화를 고려한 객체 사용

channelMap이 여러 스레드에서 접근될 경우, 스레드 안전한 객체(ConcurrentHashMap)로 변경 가능
또는, Map 내부 동기화(Collections.synchronizedMap()) 적용 가능

```java
private final Map<UUID, Channel> channelMap = Collections.synchronizedMap(new HashMap<>());
```

----

