package com.example.medicinalplant.util;

public class UserIdThreadLocal {
    private static final ThreadLocal<Integer> USER_ID_THREAD_LOCAL = new ThreadLocal<>();

    public static void set(Integer userId) {
        USER_ID_THREAD_LOCAL.set(userId);
    }

    public static Integer get() {
        return USER_ID_THREAD_LOCAL.get();
    }

    public static void remove() {
        USER_ID_THREAD_LOCAL.remove();
    }
}