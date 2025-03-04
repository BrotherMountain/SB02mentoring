package com.jyami.singleton;

public class EagerSingleton {
    public static final String TYPE = "EAGER";
    public static final EagerSingleton INSTANCE = new EagerSingleton(); // 단 하나의 인스턴스
    private final String name = "EagerSingleton";

    private EagerSingleton() {} // 외부에서 인스턴스 생성 방지

    public void showMessage() {
        System.out.println("Eager Singleton Instance");
    }
}
