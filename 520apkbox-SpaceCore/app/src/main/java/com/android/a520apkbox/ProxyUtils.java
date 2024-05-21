package com.android.a520apkbox;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;


public class ProxyUtils {


    /**
     * 反射获得 指定对象(当前-》父类-》父类...)中的 成员属性
     * @param instance
     * @param name
     * @return
     * @throws NoSuchFieldException
     */
    public static Field findField(Object instance, String name) throws NoSuchFieldException {
        Class clazz = instance.getClass();
        //反射获得
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(name);
                //如果无法访问 设置为可访问
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                return field;
            } catch (NoSuchFieldException e) {
                //如果找不到往父类找
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field " + name + " not found in " + instance.getClass());
    }


    /**
     * 反射获得 指定对象(当前-》父类-》父类...)中的 函数
     * @param instance
     * @param name
     * @param parameterTypes
     * @return
     * @throws NoSuchMethodException
     */
    public static Method findMethod(Object instance, String name, Class... parameterTypes)
            throws NoSuchMethodException {
        Class clazz = instance.getClass();
        while (clazz != null) {
            try {
                Method method = clazz.getDeclaredMethod(name, parameterTypes);
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                return method;
            } catch (NoSuchMethodException e) {
                //如果找不到往父类找
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchMethodException("Method " + name + " with parameters " + Arrays.asList
                (parameterTypes) + " not found in " + instance.getClass());
    }
}
