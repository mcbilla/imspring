package com.mcb.imspring.core.resource;

import com.sun.istack.internal.Nullable;

import java.net.MalformedURLException;

/**
 * 用于扫描并加载指定路径下的.class文件
 */
public interface ResourceLoader {
    Resource getResource(String location) throws MalformedURLException;

    @Nullable
    ClassLoader getClassLoader();
}
