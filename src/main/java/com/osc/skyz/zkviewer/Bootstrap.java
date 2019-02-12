package com.osc.skyz.zkviewer;

import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletException;
import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 功能描述:
 * <p/>
 * 创建人: shoujun.yang
 * <p/>
 * 创建时间: 2019/1/10 10:36 AM.
 * <p/>
 */
public class Bootstrap {

    private static final String USEWR_HOME = System.getProperty("user.home");
    private static final String WEB_APP_ROOT_DIR = USEWR_HOME + File.separator + ".zk-viewer-web";
    private static final String CONTEXT_PATH = "/zkviewer";
    private static final String WEB_APP_BASE_DIR = "web";
    private static final int START_PORT = 6789;
    private static final int SHUTDOWN_PORT = 7890;
    private static final int MY_SHUTDOWN_PORT = 12345;
    private static final String SHUTDOWN = "SHUTDOWN";

    public static void main(String[] args) {

        tryStopLastRunProcess();

        unzipWebApp();

        String catalinaHome = WEB_APP_ROOT_DIR;
        Tomcat tomcat = new Tomcat();
        tomcat.setHostname("localhost");
        tomcat.setPort(START_PORT);
        tomcat.setBaseDir(catalinaHome);
        tomcat.getHost().setAppBase(WEB_APP_ROOT_DIR);
        StandardServer server = (StandardServer) tomcat.getServer();
        AprLifecycleListener listener = new AprLifecycleListener();
        server.addLifecycleListener(listener);
        tomcat.getServer().setPort(SHUTDOWN_PORT);
        tomcat.getServer().setShutdown("SHUTDOWN");

        try {
            tomcat.addWebapp(CONTEXT_PATH, WEB_APP_ROOT_DIR + File.separator + WEB_APP_BASE_DIR);
        } catch (ServletException e) {
            e.printStackTrace();
        }

        try {
            tomcat.start();
            Thread.sleep(100);
            Utils.openBrowser("http://localhost:6789/zkviewer");
            startShutDownServer();
            tomcat.getServer().await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void unzipWebApp() {
        try {
            String jarPath = new Bootstrap().getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

            File targetFile = new File(WEB_APP_ROOT_DIR);
            targetFile.mkdir();

            File jarFile = new File(jarPath);
            ZipInputStream zis = new ZipInputStream(new FileInputStream(jarFile));
            ZipEntry entry = null;
            while( (entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                if(!name.startsWith("web/")) {
                    continue;
                }

                File file = new File(WEB_APP_ROOT_DIR + File.separator + name);
                if(entry.isDirectory()) {
                    if(!file.exists()) {
                        file.mkdirs();
                    }
                    continue;
                }

                FileOutputStream ous = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int len = 0;
                while ( (len = zis.read(buf, 0, buf.length)) != -1 ) {
                    ous.write(buf, 0, len);
                }
                ous.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void tryStopLastRunProcess() {
        try {
            Socket socket = new Socket("localhost", MY_SHUTDOWN_PORT);
            OutputStream ous = socket.getOutputStream();
            ous.write(SHUTDOWN.getBytes());
            ous.close();
            Thread.sleep(100);
        } catch (ConnectException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startShutDownServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    ServerSocket serverSocket = new ServerSocket();
                    serverSocket.bind(new InetSocketAddress("localhost", 12345));
                    while (true) {
                        Socket socket = serverSocket.accept();
                        InputStream ins = socket.getInputStream();
                        byte[] buf = new byte[SHUTDOWN.length()];
                        ins.read(buf, 0, SHUTDOWN.length());
                        if(SHUTDOWN.equals(new String(buf))) {
                            System.exit(-1);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
