package com.ling.test03.handlermapping;

import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * @author zhangling
 * @date 2022/9/11 2:19 PM
 */
public class AnimalHandlerMapping implements HandlerMapping {
    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        String url = request.getRequestURI().toString();
        String method = request.getMethod();
        if (url.startsWith("/animal")) {
            if (method.equalsIgnoreCase("GET")) {
                return new CatHandler();
            } else if (method.equalsIgnoreCase("POST")) {

            }
        }
        return null;
    }
}
