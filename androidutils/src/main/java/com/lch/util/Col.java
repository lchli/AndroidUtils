package com.lch.util;

import org.json.JSONArray;

import java.util.Collection;
import java.util.Map;

public class Col {

    public static int size(Collection c) {
        return c != null ? c.size() : 0;
    }


    public static boolean isEmpty(Collection c) {
        return c == null || c.isEmpty();
    }

    public static boolean isEmpty(Map c) {
        return c == null || c.isEmpty();
    }

    public static boolean isEmpty(JSONArray c) {
        return c == null || c.length() <= 0;
    }

    public static boolean isEmpty(Object[] c) {
        return c == null || c.length <= 0;
    }

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

}
