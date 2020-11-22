package com.ithillel.appcontext;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.ithillel.service.InMemoryTextProcessor;
import com.ithillel.service.Storage;

public class PropertiesApplicationContext implements ApplicationContext {
    private Map<String, Object> beans = new HashMap<>();
    private Properties applicationProperties;

    public static void main(String[] args) {
        PropertiesApplicationContext pap = new PropertiesApplicationContext();
        ((InMemoryTextProcessor) pap.getBean("textProcessor")).save("1", "2");
        System.out.println(((InMemoryTextProcessor) pap.getBean("textProcessor")).getByKey("1"));
    }

    public PropertiesApplicationContext() {
        applicationProperties = new Properties();
        try {
            applicationProperties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        createBeans(applicationProperties);
    }

    public void createBeans(Properties applicationProperties) {
        applicationProperties.forEach((k, v) -> {
            if (String.valueOf(k).endsWith("name")) {
                String key = String.valueOf(k).split("\\.")[0];
                try {
                    Object bean = applicationProperties.containsKey(String.format("%s.args", key)) ?
                            Class.forName(applicationProperties.getProperty(String.format("%s.type", key))).getDeclaredConstructor(Storage.class).newInstance(beans.get(applicationProperties.getProperty(String.format("%s.args", key)))) :
                            Class.forName(applicationProperties.getProperty(String.format("%s.type", key))).newInstance();

                    beans.put(applicationProperties.getProperty(String.format("%s.name", key)), bean);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public Object getBean(String name) {
        return beans.get(name);
    }
}