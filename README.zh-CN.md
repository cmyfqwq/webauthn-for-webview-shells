<div align="center">

# 🔑 WebAuthn for WebView Shells

**让轻量 WebView 壳浏览器用上通行密钥——拨的只是官方本来就有的那个开关。**

[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Requires LSPosed](https://img.shields.io/badge/requires-LSPosed-%23f43f5e)](https://github.com/LSPosed/LSPosed)
![Android 7.0+](https://img.shields.io/badge/Android-7.0%2B-3ddc84)
![System WebView 124+](https://img.shields.io/badge/System%20WebView-124%2B-4285f4)

English README above.

</div>

---

## 🧠 这到底是什么

通行密钥的能力**本来就在系统 WebView 里**（v124+）。官方规范要求：宿主 app 必须自己声明一行

```java
WebSettingsCompat.setWebAuthenticationSupport(settings, WEB_AUTHENTICATION_SUPPORT_FOR_APP);
```

Via、夸克这类轻量壳浏览器全部走系统 WebView 渲染，但**没有一家调用它**——于是 `navigator.credentials()` 到不了凭据栈：不是系统不给，是壳没伸手。本模块就是在宿主进程内部替它拨机关，自带一份 androidx.webkit，不改动任何系统组件或密码本本体。

> 回滚 = LSPosed 里关掉开关。就这么简单喵。

**实测**：Via 7.3.3 创建 + 验证通行密钥正常工作（ColorOS 原生密码本接管弹窗）。

其余细节（原理图 / 安装 / 自编 / FAQ）见上方英文 README 喵。构建命令一样：`pwsh build.ps1`。
