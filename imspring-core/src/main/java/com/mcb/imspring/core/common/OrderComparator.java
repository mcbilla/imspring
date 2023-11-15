package com.mcb.imspring.core.common;

import java.lang.reflect.Method;
import java.util.Comparator;

public class OrderComparator implements Comparator<Object> {
    public static final OrderComparator INSTANCE = new OrderComparator();

    /**
     * 当前对象（调用这个方法的对象）和形参对象（被比较的另一个对象）进行比较
     * a - b < 0，说明 a < b，a排前面，b排后面
     * a - b > 0，说明 a > b，b排前面，a排后面
     */
    @Override
    public int compare(Object o1, Object o2) {
        return getOrder(o1) - getOrder(o2);
    }

    public static int getOrder(Object obj) {
        int order = Ordered.DEFAULT_PRECEDENCE;
        if (obj instanceof Ordered) {
            order = ((Ordered) obj).getOrder();
        } else {
            try {
                Method method = obj.getClass().getMethod("getOrder", null);
                order = (int) method.invoke(obj.getClass().newInstance(), null);
            } catch (Exception e) {

            }
        }
        return order;
    }
}
