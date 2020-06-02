package com.xsic.xsic.utils;

public class ObjectUtil {
    public static boolean equals(Object o1, Object o2){
        if (o1 == null){
            return o2 == null;
        }
        return o1.equals(o2);
    }
}
