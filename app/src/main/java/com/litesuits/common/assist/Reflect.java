package com.litesuits.common.assist;

import com.litesuits.android.log.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by yanxq on 17/3/29.
 */

public class Reflect {

    private static final String TAG ="Reflect";

    public static Reflect on(Object obj) {
        return new Reflect(obj);
    }

    public static Reflect on(String className) {
        Object obj = null;
        if (className != null && className.length() > 0) {
            try {
                obj  = Class.forName(className);
            } catch (Throwable throwable) {
                Log.e(TAG,"find class fail.",throwable);
            }
        }
        if (obj == null) {
            obj = className;
        }
        return new Reflect(obj);
    }

    private Object mObj;
    private Method mTargetMethod;

    public Reflect(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Obj must not be null!");
        }
        mObj = obj;
    }

    public Object call(String method, Object... args) {
        Class[] classes = null;
        if (args != null && args.length > 0) {
            classes = new Class[args.length];
            for (int i=0,length=args.length; i < length; i++) {
                if (args[i] == null) {
                    continue;
                }
                classes[i] = args[i].getClass();
            }
        }
        Class clazz = getClazz(mObj);
        Method targetMethod = findMethod(clazz,method,classes);
        return targetMethod == null? null : callMethod(mObj,targetMethod,args);
    }

    public Reflect find(String method,Class... argClasses) {
        Class clazz = getClazz(mObj);
        mTargetMethod = findMethod(clazz,method,argClasses);
        return this;
    }

    public Object call(Object[] args) {
        return mTargetMethod == null? null : callMethod(mObj, mTargetMethod,args);
    }

    private Method findMethod(Class clazz,String method,Class[]classes) {
        try {
            return clazz.getDeclaredMethod(method,classes);
        } catch (Throwable throwable) {
            Log.e(TAG,"find method fail.",throwable);
        }
        return null;
    }

    private Object callMethod(Object obj,Method method,Object[] args) {
        try {
            method.setAccessible(true);
            return method.invoke(obj instanceof Class? null:obj,args);
        } catch (Throwable throwable) {
            Log.e(TAG,"call method fail.",throwable);
        }
        return null;
    }

    public Object get(String field) {
        Class clazz  = getClazz(mObj);
        Field targetField = findField(clazz,field);
        return targetField == null? null : getValue(mObj,targetField);
    }

    private Field findField(Class clazz,String field) {
        try {
            return clazz.getDeclaredField(field);
        } catch (Throwable throwable) {
            Log.e(TAG,"find field fail.",throwable);
        }
        return null;
    }

    private Object getValue(Object obj,Field field) {
        try {
            field.setAccessible(true);
            return field.get(obj instanceof Class? null : obj);
        } catch (Throwable throwable) {
            Log.e(TAG,"get value fail.",throwable);
        }
        return null;
    }


    private Class getClazz(Object obj) {
        return obj instanceof Class ? (Class)obj : obj.getClass();
    }

}
