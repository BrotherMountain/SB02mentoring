package com.jyami.singleton;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class SingletonTest {

    @Test
    @DisplayName("EagerSingleton 싱글톤 테스트. 두 객체는 같은 객체이다.")
    void eagerSingletonTest() {
        EagerSingleton eagerSingleton = EagerSingleton.INSTANCE;
        EagerSingleton eagerSingleton2 = EagerSingleton.INSTANCE;
        assertEquals(eagerSingleton, eagerSingleton2);
    }

    @Test
    @DisplayName("LazySingleton 싱글톤 테스트. 두 객체는 같은 객체이다.")
    void lazySingletonTest() {
        LazySingleton lazySingleton = LazySingleton.getInstance();
        LazySingleton lazySingleton2 = LazySingleton.getInstance();
        assertEquals(lazySingleton, lazySingleton2);
    }

    @Test
    @DisplayName("eager, lazy 싱글톤 각각 instance가 있는지 확인")
    void eagerLazySingletonInstance() throws NoSuchFieldException, IllegalAccessException {
        // Thread 시작 전에 Reflection을 이용하여 instance 값 확인
        Field eagerInstanceField = EagerSingleton.class.getDeclaredField("INSTANCE");
        eagerInstanceField.setAccessible(true); // private 필드 접근 허용
        Object eagerInstance = eagerInstanceField.get(null); // 특정 인스턴스 없이 static 필드 접근

        assertNotNull(eagerInstance);
        assertTrue(eagerInstance instanceof EagerSingleton);

        Field lazyInstance = LazySingleton.class.getDeclaredField("instance");
        lazyInstance.setAccessible(true);
        Object lazyInstanceObject = lazyInstance.get(null); // 특정 인스턴스 없이 static 필드 접근
        assertNull(lazyInstanceObject);

        LazySingleton.getInstance(); // LazySingleton의 instance 필드에 값이 할당됨

        lazyInstanceObject = lazyInstance.get(null);
        assertNotNull(lazyInstanceObject);
        assertTrue(lazyInstanceObject instanceof LazySingleton);
    }

    @Test
    @DisplayName("ThreadSafeSingleton 싱글톤 테스트. 두 객체는 같은 객체이다 : 가끔 보장 못할수도 있다.")
    void testLazySingletonThreadSafetyIssue() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);

        final LazySingleton[] instances = new LazySingleton[2];

        // 두 개의 스레드를 동시에 시작하기 위해 CountDownLatch 사용
        Runnable task = () -> {
            try {
                latch.await(); // 다른 스레드와 동시에 실행되도록 대기
                instances[0] = LazySingleton.getInstance();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Runnable task2 = () -> {
            try {
                latch.await();
                instances[1] = LazySingleton.getInstance();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        executorService.execute(task);
        executorService.execute(task2);

        // 모든 스레드가 동시에 실행되도록 카운트다운
        latch.countDown();

        // 실행 완료 후 shutdown
        Thread.sleep(100); // 실행을 기다려야 확실하게 두 개의 인스턴스가 생성되는지 확인 가능
        executorService.shutdown();

        // 두 개의 객체가 다르면 Thread-Safety 문제가 있는 것!
        assertEquals(instances[0], instances[1]);
    }

    @Test
    @DisplayName("ThreadSafeSingleton 싱글톤 테스트. 두 객체는 같은 객체이다.")
    void threadSafeSingletonTest() {
        ThreadSafeSingleton threadSafeSingleton = ThreadSafeSingleton.getInstance();
        ThreadSafeSingleton threadSafeSingleton2 = ThreadSafeSingleton.getInstance();
        assertEquals(threadSafeSingleton, threadSafeSingleton2);
    }

    @Test
    @DisplayName("DoubleCheckedLockingSingleton 싱글톤 테스트. 두 객체는 같은 객체이다.")
    void doubleCheckedLockingSingletonTest() {
        DoubleCheckedLockingSingleton doubleCheckedLockingSingleton = DoubleCheckedLockingSingleton.getInstance();
        DoubleCheckedLockingSingleton doubleCheckedLockingSingleton2 = DoubleCheckedLockingSingleton.getInstance();
        assertEquals(doubleCheckedLockingSingleton, doubleCheckedLockingSingleton2);
    }

}
