package com.yjy.liverequest.request;


import java.lang.reflect.Method;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/11/12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ReflectUtils {
    public static Object invokeMethod(Method method,Object object,Object[] args){
        try {
            return method.invoke(object,args);
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

}
