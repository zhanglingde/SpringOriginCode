package com.ling.test12.selfConverter;

import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义类型转换器从 String 转换为 Date
 *
 * @author zhangling
 * @date 2022/10/10 11:37 AM
 */
public class String2DateConverter implements Converter<String, Date> {

    @Override
    public Date convert(String arg0) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(arg0);
        } catch (ParseException e) {
            return null;
        }
    }

}

