package com.osc.skyz.zkviewer;


import java.net.URI;

/**
 * 功能描述:
 * <p/>
 * 创建人: shoujun.yang
 * <p/>
 * 创建时间: 2019/1/11 2:16 PM.
 * <p/>
 */
public class Utils {

    public static void openBrowser(String url) throws Exception {
        try {
            String osName = System.getProperty("os.name", "linux");
            Runtime rt = Runtime.getRuntime();
            String browser = System.getenv("BROWSER");
            if (browser != null) {
                if (osName.contains("windows")) {
                    rt.exec(new String[] { "cmd.exe", "/C",  browser, url });
                } else {
                    rt.exec(new String[] { browser, url });
                }
                return;
            }
            try {
                Class<?> desktopClass = Class.forName("java.awt.Desktop");
                Boolean supported = (Boolean) desktopClass.getMethod("isDesktopSupported").invoke(null, new Object[0]);
                URI uri = new URI(url);
                if (supported) {
                    Object desktop = desktopClass.getMethod("getDesktop").invoke(null);
                    desktopClass.getMethod("browse", URI.class).invoke(desktop, uri);
                    return;
                }
            } catch (Exception e) {
            }
            if (osName.contains("windows")) {
                rt.exec(new String[] { "rundll32", "url.dll,FileProtocolHandler", url });
            } else if (osName.contains("mac") || osName.contains("darwin")) {
                Runtime.getRuntime().exec(new String[] { "open", url });
            } else {
                String[] browsers = { "xdg-open", "chromium", "google-chrome",
                        "firefox", "mozilla-firefox", "mozilla", "konqueror",
                        "netscape", "opera", "midori" };
                boolean ok = false;
                for (String b : browsers) {
                    try {
                        rt.exec(new String[] { b, url });
                        ok = true;
                        break;
                    } catch (Exception e) {
                    }
                }
                if (!ok) {
                    throw new Exception("Browser detection failed");
                }
            }
        } catch (Exception e) {
            throw new Exception("Failed to start a browser to open the URL " + url + ": " + e.getMessage());
        }
    }
}
