package com.shenbianys.assisant.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 数据转换工具
 *
 * @author Yang Hua
 */
public class ConvertUtils {

    /**
     * map转JAVA对象
     *
     * @param map
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz) throws Exception {
        T obj = clazz.newInstance();
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz, Object.class);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        // 循环处理JAVA对象的所有属性
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String key = propertyDescriptor.getName();
            if (map.containsKey(key)) {
                Method writeMethod = propertyDescriptor.getWriteMethod();
                String typeName = propertyDescriptor.getPropertyType().getTypeName();
                boolean isInteger = map.get(key) instanceof Integer;
                boolean isLong = map.get(key) instanceof Long;
                boolean isDate = map.get(key) instanceof Date;

                if (Integer.class.getName().equals(typeName) && !isInteger) {
                    writeMethod.invoke(obj, Integer.parseInt(String.valueOf(map.get(key))));
                } else if (Long.class.getName().equals(typeName) && !isLong) {
                    writeMethod.invoke(obj, Long.parseLong(String.valueOf(map.get(key))));
                } else if (Date.class.getName().equals(typeName) && !isDate) {
                    writeMethod.invoke(obj, new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(String.valueOf(map.get(key))));
                } else {
                    writeMethod.invoke(obj, map.get(key));
                }
            }
        }
        return obj;
    }
}
