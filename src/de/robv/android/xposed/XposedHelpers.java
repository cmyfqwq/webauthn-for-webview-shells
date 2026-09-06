package de.robv.android.xposed;

/** 编译期桩：运行时由 LSPosed 提供真实实现 */
public final class XposedHelpers {
    private XposedHelpers() {}
    public static Class<?> findClass(String className, ClassLoader classLoader) { throw new IllegalStateException("stub"); }
    public static Class<?> findClassIfExists(String className, ClassLoader classLoader) { throw new IllegalStateException("stub"); }
    public static Object callMethod(Object obj, String methodName, Object... args) { throw new IllegalStateException("stub"); }
}
