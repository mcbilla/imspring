package com.mcb.imspring.web.context;


import com.mcb.imspring.core.ApplicationContext;
import com.sun.istack.internal.Nullable;

import javax.servlet.ServletContext;

public interface WebApplicationContext extends ApplicationContext {
    @Nullable
    ServletContext getServletContext();
}
