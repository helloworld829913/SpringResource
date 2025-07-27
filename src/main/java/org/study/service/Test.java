package org.study.service;

import org.study.spring.MyApplicationContext;

public class Test {

    public static void main(String[] args) {
        MyApplicationContext myApplicationContext=new MyApplicationContext(AppConfig.class);
        UserService userService = (UserService) myApplicationContext.getBean("UserService");
    }
}
