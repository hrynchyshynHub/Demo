package com.eleks.filter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by ivan.hrynchyshyn on 24.07.2017.
 */
@WebListener
public class ResourceDownloader implements ServletContextListener {
    private Map<String, String> resource = new HashMap<>();
    private static final String FILENAME = "database.properties";

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        downloadResource();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
    public void downloadResource(){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Properties properties = new Properties();
        try(InputStream resourceStream = classLoader.getResourceAsStream(FILENAME)) {
            properties.load(resourceStream);
            resource.put("class",     properties.getProperty("db.driver"));
            resource.put("url",       properties.getProperty("db.url"));
            resource.put("username",  properties.getProperty("db.username"));
            resource.put("password",  properties.getProperty("db.password"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Map<String, String> getResource() {
        return resource;
    }
}

