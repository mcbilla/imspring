package com.mcb.imspring.boot;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class ImspringApplication {

    private static int port = 8080;

    private static String contextPath = "/";

    private static String baseDir = new File("imspring-web/src/main/resources").getAbsolutePath();



    public static void main(String[] args) throws LifecycleException {
        // 创建tomcat并绑定端口
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        // Tomcat 9.0 必须调用 Tomcat#getConnector() 方法之后才会监听端口
        tomcat.getConnector();

        // 创建webapp，contextPath为URL的基路径，baseDir为包含web.xml的路径
        tomcat.addWebapp(contextPath, baseDir);

        tomcat.start();
        // 等待，避免马上运行结束。
        tomcat.getServer().await();
    }
}
