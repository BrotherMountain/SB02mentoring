package com.jyami.singleton;

public class ThreadSafeSingleton {
    public static final String TYPE = "THREAD_SAFE";
    private static ThreadSafeSingleton instance;

    private ThreadSafeSingleton() {}

    public static synchronized ThreadSafeSingleton getInstance() { // 동기화 추가
        if (instance == null) {
            instance = new ThreadSafeSingleton();
        }
        return instance;
    }

    public void showMessage() {
        System.out.println("Thread Safe Singleton Instance");
    }
}
