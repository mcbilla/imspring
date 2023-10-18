package com.mcb.imspring.aop.test.matcher;

import com.mcb.imspring.aop.AspectJExpressionPointcut;
import com.mcb.imspring.aop.test.service.MyService;
import com.mcb.imspring.aop.test.service.MyTest;
import org.junit.Test;

import java.lang.reflect.Method;

public class AspectJExpressionPointcutTest {

    @Test
    public void testClazz() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* com.mcb.imspring.aop.test.service.MyService.*(..))");
        System.out.println(pointcut.matchers(MyService.class));
        System.out.println(pointcut.matchers(MyTest.class));
    }

    @Test
    public void testMethod() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* com.mcb.imspring.aop.test.service.*Service.*(..))");
        Method[] methods1 = MyService.class.getDeclaredMethods();
        for (Method method : methods1) {
            System.out.println("1" + pointcut.matchers(method, MyService.class));
        }

        Method[] methods2 = MyTest.class.getDeclaredMethods();
        for (Method method : methods2) {
            System.out.println("2" + pointcut.matchers(method, MyTest.class));
        }
    }
}
