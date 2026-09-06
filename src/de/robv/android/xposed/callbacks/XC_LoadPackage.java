package de.robv.android.xposed.callbacks;

/** 编译期桩 */
public abstract class XC_LoadPackage {
    public static class LoadPackageParam {
        public String packageName;
        public String processName;
        public ClassLoader classLoader;
        @SuppressWarnings("unused")
        public android.content.pm.ApplicationInfo appInfo;
        public boolean isFirstApplication;
    }
    public interface IXposedHookLoadPackage { void handleLoadPackage(LoadPackageParam lpparam); }
}
