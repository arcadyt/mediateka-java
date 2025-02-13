package com.acowg.utils;

import java.lang.management.ManagementFactory;

public class HostnameExtractor {
    public static String getHostName() {
        return ManagementFactory.getRuntimeMXBean().getName().split("@")[1];
    }
}
