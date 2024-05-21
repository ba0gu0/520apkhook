package com.android.a520apkbox;

import android.app.Application;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * author: huangDianHua
 * time: 2021/7/1 09:29:27
 * description:
 */
public class ClassLoaderUtils {

    private static final String TAG = "520ApkBox ClassLoader";

    public static void loadDex(Application application,List<File> dexFiles, File versionDir) throws Exception{
        //1.先从 ClassLoader 中获取 pathList 的变量
        Field pathListField = ProxyUtils.findField(application.getClassLoader(), "pathList");
        //1.1 得到 DexPathList 类
        Object pathList = pathListField.get(application.getClassLoader());
        //1.2 从 DexPathList 类中拿到 dexElements 变量
        Field dexElementsField= ProxyUtils.findField(pathList,"dexElements");
        //1.3 拿到已加载的 dex 数组
        Object[] dexElements=(Object[])dexElementsField.get(pathList);
        //2. 反射到初始化 dexElements 的方法，也就是得到加载 dex 到系统的方法
        Method makeDexElements= ProxyUtils.findMethod(pathList,"makePathElements",List.class,File.class,List.class);
        //2.1 实例化一个 集合  makePathElements 需要用到
        ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();
        //2.2 反射执行 makePathElements 函数，把已解码的 dex 加载到系统，不然是打不开 dex 的，会导致 crash
        Object[] addElements=(Object[])makeDexElements.invoke(pathList,dexFiles,versionDir,suppressedExceptions);
        //3. 实例化一个新数组，用于将当前加载和已加载的 dex 合并成一个新的数组
        Object[] newElements= (Object[]) Array.newInstance(dexElements.getClass().getComponentType(),dexElements.length+addElements.length);
        //3.1 将系统中的已经加载的 dex 放入 newElements 中
        System.arraycopy(dexElements,0,newElements,0,dexElements.length);
        //3.2 将解密后已加载的 dex 放入新数组中
        System.arraycopy(addElements,0,newElements,dexElements.length,addElements.length);
        //4. 将合并的新数组重新设置给 DexPathList的 dexElements
        dexElementsField.set(pathList,newElements);
    }

}