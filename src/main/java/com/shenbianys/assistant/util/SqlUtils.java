package com.shenbianys.assistant.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.Assert;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * SQL 语句生成工具
 *
 * @author Yang Hua
 */
@Slf4j
public class SqlUtils {
    /**
     * 生成JAVA对象的 insert sql语句（简易版）
     *
     * @param bean
     * @return
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static String generatorInsertSql(Object bean) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        Assert.notNull(bean, "待处理对象不允许为空");
        StringBuffer sqlBuffer = new StringBuffer();
        PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors();
        sqlBuffer.append("INSERT INTO ").append(bean.getClass().getAnnotation(Table.class).value());
        sqlBuffer.append(genKeys(propertyDescriptors));
        sqlBuffer.append(" VALUES ");
        sqlBuffer.append(genValues(bean, propertyDescriptors));
        return sqlBuffer.toString();
    }

    public static String generatorInsertSql(List<?> beans) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        Assert.notEmpty(beans, "待处理集合对象不允许为空");
        StringBuffer sqlBuffer = new StringBuffer();
        PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(beans.get(0).getClass(), Object.class).getPropertyDescriptors();
        sqlBuffer.append("INSERT INTO ").append(beans.get(0).getClass().getAnnotation(Table.class).value());
        sqlBuffer.append(genKeys(propertyDescriptors));
        sqlBuffer.append(" VALUES ");

        for (int i = 0; i < beans.size(); i++) {
            sqlBuffer.append(genValues(beans.get(i), propertyDescriptors));
            sqlBuffer.append(",");
        }

        int lenOfSql = sqlBuffer.length();
        sqlBuffer.delete(lenOfSql - 1, lenOfSql);

        return sqlBuffer.toString();
    }

    private static String genKeys(PropertyDescriptor[] propertyDescriptors) {
        StringBuffer keysBuffer = new StringBuffer();
        keysBuffer.append("(");

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String key = propertyDescriptor.getName();
            keysBuffer.append(key).append(",");
        }

        int lenOfSql = keysBuffer.length();
        keysBuffer.delete(lenOfSql - 1, lenOfSql);

        keysBuffer.append(")");
        return keysBuffer.toString();
    }

    private static String genValues(Object bean, PropertyDescriptor[] propertyDescriptors) throws InvocationTargetException, IllegalAccessException {
        StringBuffer valuesBuffer = new StringBuffer();
        valuesBuffer.append("(");

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String typeName = propertyDescriptor.getPropertyType().getTypeName();
            Method readMethod = propertyDescriptor.getReadMethod();
            Object value = readMethod.invoke(bean);
            if (value != null) {
                String sValue = "";
                if (typeName.equals(Date.class.getName())) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    sValue = formatter.format(value);
                    valuesBuffer.append("'").append(sValue).append("'").append(",");
                } else if (typeName.equals(Boolean.class.getName())) {
                    sValue = Boolean.parseBoolean(String.valueOf(value)) ? "1" : "0";
                    valuesBuffer.append(sValue).append(",");
                } else {
                    sValue = String.valueOf(value);
                    valuesBuffer.append("'").append(sValue).append("'").append(",");
                }
            } else {
                valuesBuffer.append("null").append(",");
            }
        }

        int lenOfVals = valuesBuffer.length();
        valuesBuffer.delete(lenOfVals - 1, lenOfVals);

        valuesBuffer.append(")");
        return valuesBuffer.toString();
    }
}
