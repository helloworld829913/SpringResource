package org.study.spring;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext {
    private Class Appconfig;
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();

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
                            //获取beanName
                            Component component = clazz.getAnnotation(Component.class);
                            String beanName = component.value();

                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setType(clazz);

                            // scope判断（单例、多例）
                            if (clazz.isAnnotationPresent(Scope.class)) {
                                Scope scope = clazz.getAnnotation(Scope.class);
                                beanDefinition.setScope(scope.value());
                            } else beanDefinition.setScope("singleton");
                            // 把beanName和bean定义，放入hashmap
                            beanDefinitionMap.put(beanName, beanDefinition);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        //============================5. 实例化单例Bean============================
        // keySet() 方法用于获取 beanDefinitionMap 中所有的键（即 bean 的名称）
        // 以便遍历并处理每个 Bean 定义。
        for (String beanName : beanDefinitionMap.keySet()) {
            // 根据bean的名字获取bean定义
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

            if (beanDefinition.getScope().equals("singleton")) {
                Object bean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, bean);
            }
        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        return null;

    }

    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

        if (beanDefinition == null) {
            throw new NullPointerException();
        } else {
            String scope = beanDefinition.getScope();
            if (scope.equals("singleton")) {
                Object bean = singletonObjects.get(beanName);
                // 单例池中没有，就手动创建bean
                if (bean == null) {
                    Object o = createBean(beanName, beanDefinition);
                    singletonObjects.put(beanName, o);
                }
                return bean;
            } else {
                //对于多例bean，每一次直接创建即可。
                return createBean(beanName, beanDefinition);
            }
        }
    }

}
