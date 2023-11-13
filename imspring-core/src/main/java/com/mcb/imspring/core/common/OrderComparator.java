package com.mcb.imspring.core.common;

import java.lang.reflect.Method;
import java.util.Comparator;

public class OrderComparator implements Comparator<Object> {
    public static final OrderComparator INSTANCE = new OrderComparator();

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
