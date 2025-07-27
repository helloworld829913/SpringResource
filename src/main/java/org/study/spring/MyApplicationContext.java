package org.study.spring;

public class MyApplicationContext {
    private Class Appconfig;

    public MyApplicationContext(Class appconfig) {
        Appconfig = appconfig;
    }

    public Object getBean(String beanName){
        return null;
    }
}
