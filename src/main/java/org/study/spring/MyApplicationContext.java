package org.study.spring;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;

public class MyApplicationContext {
    private Class Appconfig;

    public MyApplicationContext(Class configClass) {
        this.Appconfig = configClass;
        //============================1. 扫描============================
        //需要判断，传递的类上面有没有ComponentScant注解。有的话，就需要获取注解的信息。
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan annotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            //拿到注解后，获取扫描路径
            String path = annotation.value(); // org.study.service

            path = path.replace(".", "/");

            // 扫描的是编译后的 class文件
            ClassLoader classLoader = MyApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);

            //============================2. 文件处理============================
            File file = new File(resource.getFile());
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                // 遍历文件。
                for (File f : files) {
                    String fileName = f.getAbsolutePath();
                    //============================3. Bean的判断============================

                    // 路径=>类名的转换。（这里采用简单直接的写法，后续可以完善）
                    String className = fileName.substring(fileName.indexOf("org"), fileName.indexOf(".class"));
                    className = className.replace("\\", ".");

                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        if (clazz.isAnnotationPresent(Component.class)) {
                            //============================4. 如何获取Bean============================
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }


                }
            }
        }
    }

    public Object getBean(String beanName) {
        return null;
    }
}
