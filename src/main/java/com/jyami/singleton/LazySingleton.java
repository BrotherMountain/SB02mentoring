package com.jyami.singleton;

public class LazySingleton {
    public static final String TYPE = "LAZY";
    private static LazySingleton instance; // 인스턴스 변수

    private LazySingleton() {} // 외부에서 생성 불가능

    public static LazySingleton getInstance() {
        if (instance == null) { // 최초 호출 시 인스턴스 생성
            instance = new LazySingleton();
        }
        return instance;
    }

    public void showMessage() {
        System.out.println("Lazy Singleton Instance");
    }
}
