package me.saurpuss.dutymode.duty.util;

import com.google.common.base.Enums;

public enum StorageType {
    KRYO, MYSQL;

    public static StorageType getIfPresent(String name) {
        if (name.equalsIgnoreCase("file")) return KRYO;

        return Enums.getIfPresent(StorageType.class, name).orNull();
    }
}
