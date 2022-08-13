package com.ling.test01.utils;

import org.springframework.core.convert.converter.Converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 把日期转换字符串
 */
public class StringToDateConverter implements Converter<String, Date> {

    /**
     * @param s  传入进来的字符串
     * @return
     */
    public Date convert(String s) {
        if(s == null){
            throw new RuntimeException("请传入数据");
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return df.parse(s);
        } catch (Exception e) {
            throw new RuntimeException("数据类型转换出现错误");
        }

    }
}
