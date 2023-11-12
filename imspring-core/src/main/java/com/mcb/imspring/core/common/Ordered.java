package com.mcb.imspring.core.common;

public interface Ordered {
    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    int DEFAULT_PRECEDENCE = 0;

    int getOrder();
}
