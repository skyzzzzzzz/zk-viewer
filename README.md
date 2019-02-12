# zk-viewer

#### 介绍
基于web界面的zookeeper管理工具，内嵌tomcat，无需部署应用，单独一个jar包运行

web界面效果图: https://gitee.com/skyz/zk-viewer/blob/master/src/main/webapp/zkviewer.jpg

#### 设计思路
启动类为com.osc.skyz.zkviewer.Bootstrap，启动后解压jar包中的web应用到用户主目录下，然后启动内置的tomcat服务器，再打开浏览器访问页面。
代码使用maven assembly构建，以便把所有class、页面文件打包到一个jar包中，直接运行jar文件即可。