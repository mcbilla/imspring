package com.mcb.imspring.core.io;

import com.sun.istack.internal.Nullable;

public interface ResourceLoader {
    Resource getResource(String location);

    @Nullable
    ClassLoader getClassLoader();
}
