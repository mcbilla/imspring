package com.mcb.imspring.core.resource;

import java.net.MalformedURLException;
import java.net.URL;

public class FileResource implements Resource{
    // 全路径类名
    private final String name;

    // 类文件绝对路径
    private final URL url;

    public FileResource(String name, String path) throws MalformedURLException {
        this.name = name;
        this.url = new URL("file", path, "");
    }

    public FileResource(String name, URL url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public URL getURL() {
        return this.url;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("FileResource{");
        sb.append("name='").append(name).append('\'');
        sb.append(", url=").append(url);
        sb.append('}');
        return sb.toString();
    }
}
