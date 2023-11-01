package com.mcb.imspring.web.listener;

import com.mcb.imspring.core.AnnotationConfigApplicationContext;
import com.mcb.imspring.web.exception.ServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.InputStream;
import java.util.Properties;

public class SpringServletContextListener implements ServletContextListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 当Servlet容器启动Web应用时调用该方法。在调用完该方法之后，容器再对Filter初始化，
     * 并且对那些在Web 应用启动时就需要被初始化的Servlet 进行初始化。
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("servlet context init");

        // 1、获取配置文件的名称
        ServletContext servletContext = sce.getServletContext();
        String contextConfigLocation = servletContext.getInitParameter("contextConfigLocation");

        try {
            // 2、读取配置文件的内容
            InputStream fis = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
            Properties contextConfig = new Properties();
            contextConfig.load(fis);
            String config = contextConfig.getProperty("configClass");

            // 3、创建 IOC 容器
            Class<?> configClass = Class.forName(config);
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(configClass);

            // 4、把 IOC 容器放在 ServletContext 的一个属性中.
            servletContext.setAttribute("ApplicationContext", context);
        } catch (Exception e) {
            throw new ServerErrorException(e);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("servlet context destroy");
    }
}
