package com.mcb.imspring.tx.jdbc;

import com.sun.istack.internal.Nullable;

public interface SqlProvider {
    @Nullable
    String getSql();
}
