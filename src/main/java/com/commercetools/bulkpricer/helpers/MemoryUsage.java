package com.commercetools.bulkpricer.helpers;

public class MemoryUsage {

  private static long mb = 1024 * 1024;

  private static Runtime runtime = Runtime.getRuntime();

  public static String memoryReport() {
    StringBuffer out = new StringBuffer();
    out.append("JVM Memory (MB): Max " + runtime.maxMemory() / mb);
    out.append(" / Total " + runtime.totalMemory() / mb);
    out.append(" / Free " + runtime.freeMemory() / mb);
    out.append(" / -> Used " + (runtime.totalMemory() - runtime.freeMemory()) / mb);
    return out.toString();
  }

  public static long getUsedMb() {
    return (runtime.totalMemory() - runtime.freeMemory()) / mb;
  }
}
