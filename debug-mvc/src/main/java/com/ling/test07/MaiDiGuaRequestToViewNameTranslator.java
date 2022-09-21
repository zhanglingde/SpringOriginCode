package com.ling.test07;

import org.springframework.web.servlet.RequestToViewNameTranslator;

import javax.servlet.http.HttpServletRequest;

public class MaiDiGuaRequestToViewNameTranslator implements RequestToViewNameTranslator {

    @Override
    public String getViewName(HttpServletRequest request) throws Exception {
        if (request.getRequestURI().toString().startsWith("/tudou") && request.getMethod().equalsIgnoreCase("GET")) {
            return "maidigua";
        } else {
            return "404";
        }
    }
}