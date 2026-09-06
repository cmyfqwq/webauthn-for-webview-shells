package io.github.cmyfqwq.webauthnshell;

import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * WebAuthn for WebView shells.
 *
 * <p>Android's system WebView ships WebAuthn support, but per the official guideline
 * every host app must opt in ({@code WebSettingsCompat.setWebAuthenticationSupport}).
 * Lightweight "shell" browsers that render via WebView almost never do, so
 * {@code navigator.credentials()} never reaches the system credential stack.
 *
 * <p>This module flips that official switch for target packages from inside their
 * own process. It never touches the system password manager; disable the module
 * in LSPosed to fully revert.
 */
public class MainHook implements IXposedHookLoadPackage {
    private static final String TAG = "[WAShell] ";

    /** Add package names of WebView-shell browsers here (verify by pulling the
     *  APK and checking its real label — never guess from a package name). */
    private static final List<String> TARGETS = Arrays.asList(
            "mark.via.gp"            // Via 7.3.3 — tested, works
    );

    private static volatile boolean announced = false;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpp) {
        if (!TARGETS.contains(lpp.packageName)) return;
        if (announced) return;
        announced = true;
        XposedBridge.log(TAG + "active in " + lpp.processName + " (" + lpp.packageName + ")");

        if (!WebViewFeature.isFeatureSupported(WebViewFeature.WEB_AUTHENTICATION)) {
            XposedBridge.log(TAG + "this device's WebView does not support WEB_AUTHENTICATION; nothing to do");
            return;
        }

        XC_MethodHook enable = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                try {
                    Object webView = param.thisObject != null ? param.thisObject : param.args[0];
                    if (webView == null) return;
                    apply((WebSettings) ((WebView) webView).getSettings());
                } catch (Throwable t) {
                    XposedBridge.log(TAG + "apply failed: " + t);
                }
            }
        };

        try {
            XposedBridge.hookAllConstructors(WebView.class, enable);
            // Re-apply whenever a host touches its settings, so our flag survives churn.
            XposedBridge.hookAllMethods(WebSettings.class, "setJavaScriptEnabled",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            try {
                                WebSettingsCompat.setWebAuthenticationSupport(
                                        (WebSettings) param.thisObject,
                                        WebSettingsCompat.WEB_AUTHENTICATION_SUPPORT_FOR_APP);
                            } catch (Throwable ignored) {
                            }
                        }
                    });
            XposedBridge.log(TAG + "bridge armed (WebView constructors + settings reapply)");
        } catch (Throwable t) {
            XposedBridge.log(TAG + "hook failed: " + t);
        }
    }

    private static void apply(WebSettings ws) {
        if (WebSettingsCompat.getWebAuthenticationSupport(ws)
                != WebSettingsCompat.WEB_AUTHENTICATION_SUPPORT_FOR_APP) {
            WebSettingsCompat.setWebAuthenticationSupport(
                    ws, WebSettingsCompat.WEB_AUTHENTICATION_SUPPORT_FOR_APP);
            XposedBridge.log(TAG + "WebAuthn = FOR_APP");
        }
    }
}
