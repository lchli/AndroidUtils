package com.lch.util;

import java.util.Collection;
import java.util.Map;

public class StrUtil {

    public static boolean equal(String s1, String s2) {
        if (s1 == null) {
            return s2 == null;
        }

        return s1.equals(s2);

    }

    public static boolean isEmpty(Collection c) {
        return c == null || c.isEmpty();
    }

    public static boolean isEmpty(Map c) {
        return c == null || c.isEmpty();
    }

    public static boolean isEmpty(Object[] c) {
        return c == null || c.length <= 0;
    }
}
