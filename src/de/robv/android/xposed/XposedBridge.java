package de.robv.android.xposed;

/** 编译期桩：运行时由 LSPosed 提供真实实现 */
public final class XposedBridge {
    private XposedBridge() {}
    public static Object hookMethod(Object method, XC_MethodHook hook) { throw new IllegalStateException("stub"); }
    public static Object[] hookAllConstructors(Class<?> clazz, XC_MethodHook callback) { throw new IllegalStateException("stub"); }
    public static Object[] hookAllMethods(Class<?> clazz, String methodName, XC_MethodHook callback) { throw new IllegalStateException("stub"); }
    public static void log(String text) { throw new IllegalStateException("stub"); }
}
