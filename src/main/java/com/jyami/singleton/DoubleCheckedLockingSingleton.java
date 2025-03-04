package com.jyami.singleton;

public class DoubleCheckedLockingSingleton {
    public static final String TYPE = "DOUBLE_CHECKED_LOCKING";
    private static volatile DoubleCheckedLockingSingleton instance; // volatile 추가

    private DoubleCheckedLockingSingleton() {}

    public static DoubleCheckedLockingSingleton getInstance() {
        if (instance == null) {
            synchronized (DoubleCheckedLockingSingleton.class) { // 동기화 추가
                if (instance == null) {
                    instance = new DoubleCheckedLockingSingleton();
                }
            }
        }
        return instance;
    }

    public void showMessage() {
        System.out.println("Double Checked Locking Singleton Instance");
    }
}
