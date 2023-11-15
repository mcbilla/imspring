package com.mcb.imspring.aop.test.matcher;

import com.mcb.imspring.aop.config.AopConfig;
import com.mcb.imspring.aop.pointcut.AspectJExpressionPointcut;
import com.mcb.imspring.aop.test.service.MyServiceImpl;
import com.mcb.imspring.aop.test.service.MySingleService;
import com.mcb.imspring.aop.utils.AopUtils;
import org.junit.Test;

import java.lang.reflect.Method;

public class AspectJExpressionPointcutTest {

    @Test
    public void testClazz() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* cn.javass..*.*(..))");
        System.out.println(pointcut.matches(MyServiceImpl.class));
        System.out.println(pointcut.matches(MySingleService.class));
        System.out.println(pointcut.matches(AopConfig.class));
        System.out.println(pointcut.matches(AopUtils.class));
    }

    @Test
    public void testMethod() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* com.mcb.imspring.aop..*.*Service*.*(..))");
        Method[] methods1 = MyServiceImpl.class.getDeclaredMethods();
        for (Method method : methods1) {
            System.out.println("1" + pointcut.matches(method, MyServiceImpl.class));
        }

        Method[] methods2 = MySingleService.class.getDeclaredMethods();
        for (Method method : methods2) {
            System.out.println("2" + pointcut.matches(method, MySingleService.class));
        }
    }
}
