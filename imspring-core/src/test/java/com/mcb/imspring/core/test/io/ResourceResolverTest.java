package com.mcb.imspring.core.test.io;

import com.mcb.imspring.core.io.ResourceResolver;
import org.junit.Test;

import java.util.List;

public class ResourceResolverTest {
    @Test
    public void testScan() {
        List<String> scan = new ResourceResolver("com.mcb.imspring").scan();
        System.out.println(scan);

    }
}
