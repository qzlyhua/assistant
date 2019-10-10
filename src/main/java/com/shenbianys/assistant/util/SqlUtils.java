package com.shenbianys.assistant.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.mapping.Table;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    public static String generatorInsertSql(Object bean) throws IntrospectionException, InvocationTargetException, IllegalAccessException, ParseException {
        Table table = bean.getClass().getAnnotation(Table.class);
        String tableName = table.value();
        StringBuffer sqlBuffer = new StringBuffer();
        StringBuffer valuesBuffer = new StringBuffer();
        sqlBuffer.append("INSERT INTO ").append(tableName).append("(");
        BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        // 循环处理JAVA对象的所有属性
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String key = propertyDescriptor.getName();
            sqlBuffer.append(key).append(",");

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
        int lenOfSql = sqlBuffer.length();
        sqlBuffer.delete(lenOfSql - 1, lenOfSql);
        int lenOfVals = valuesBuffer.length();
        valuesBuffer.delete(lenOfVals - 1, lenOfVals);
        sqlBuffer.append(") VALUES (").append(valuesBuffer).append(")");
        return sqlBuffer.toString();
    }
}
