package de.robv.android.xposed;

/** 编译期桩：运行时由 LSPosed 提供真实实现 */
public abstract class XC_MethodHook {
    public static class MethodHookParam {
        public Object thisObject;
        public Object[] args;
        public Object result;
        public Throwable throwable;
        public boolean returnEarly;
        public Object getResult() { return result; }
        public void setResult(Object r) { result = r; returnEarly = true; }
        public Throwable getThrowable() { return throwable; }
        public void setThrowable(Throwable t) { throwable = t; returnEarly = true; }
    }
    protected XC_MethodHook() {}
    protected XC_MethodHook(int priority) {}
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {}
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {}
}
