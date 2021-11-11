#!/usr/bin/python3
# -*- coding: utf-8 -*-

import os
import subprocess

from time import sleep
class JarCommand:
    def __init__(self, options):
        self.JarFilePath = options['JarFilePath']
        self.KeystoreFile = options['Keystorefile']
        self.KeystorePass = options['KeystorePass']

        if options['JavaPath'] and os.path.exists(options['JavaPath']):
            self.JavaCommand = self.__ReslovePath(options['JavaPath'])
        else:
            self.JavaCommand = 'java'

    def Dex2Smail(self, **kwargs):
        '''
        :param dexfilepath: test/dexfile/msf_classes.dex test/dexfile/app_classes.dex
        :param smaildirpath: test/dexfile/msf_classes test/dexfile/app_classes
        :return: True/False
        '''
        JarBakSmaliFile = self.__ReslovePath(f'{self.JarFilePath}/baksmali.jar')
        JavaCommand = self.JavaCommand
        SmaliDirPath = self.__ReslovePath(kwargs['smalidirpath'])
        DexFile = self.__ReslovePath(kwargs['dexfilepath'])
        sleep(2)
        # print(f'DEBUG INFO: {JavaCommand} -jar {JarBakSmaliFile} d {DexFile} -o {SmailDirPath}')
        return subprocess.Popen(f'{JavaCommand} -jar {JarBakSmaliFile} d {DexFile} -o {SmaliDirPath}', stdout=subprocess.PIPE, shell=True, stderr=subprocess.STDOUT).communicate()

    def Smail2Dex(self, **kwargs):

        '''
        :param smailpdirath: test/dexfile/msf_classes test/dexfile/app_classes
        :param dexfilepath: test/dexfile/msf_classes.dex test/dexfile/app_classes.dex
        :return: True/False
        '''

        JarSmaliFile = self.__ReslovePath(self.JarFilePath, 'smali.jar')
        JavaCommand = self.JavaCommand
        SmaliDirPath = self.__ReslovePath(kwargs['smalidirpath'])
        DexFilePath = self.__ReslovePath(kwargs['dexfilepath'])
        sleep(2)

        # print(f'DEBUG INFO: {JavaCommand} -jar {JarSmaliFile} a {SmaliDirPath} -o {DexFilePath}')
        return subprocess.Popen(f'{JavaCommand} -jar {JarSmaliFile} a {SmaliDirPath} -o {DexFilePath}', stdout=subprocess.PIPE, shell=True, stderr=subprocess.STDOUT).communicate()

    def ApkSigner(self, **kwargs):
        '''
        :param apkfilepath: test/Finish_All.apk
        :return:
        '''

        JarApkSignerFile = self.__ReslovePath(self.JarFilePath, 'apksigner.jar')
        JavaCommand = self.JavaCommand
        KeystoreFile = self.__ReslovePath(self.KeystoreFile)
        KeystorePass = self.KeystorePass
        ApkFile = self.__ReslovePath(kwargs['apkfilepath'])
        sleep(3)
        # print(f'DEBUG INFO: {JavaCommand} -jar {JarApkSignerFile} sign --ks {KeystoreFile} --ks-pass pass:{KeystorePass} {ApkFile}')
        return subprocess.Popen(f'{JavaCommand} -jar {JarApkSignerFile} sign --ks {KeystoreFile} --ks-pass pass:{KeystorePass} {ApkFile}', stdout=subprocess.PIPE, shell=True, stderr=subprocess.STDOUT).communicate()

    def ManifestEditor(self, **kwargs):
        '''
        :param androidmanifestfile: test/apkfile/AndroidManifest.xml
        :param newandroidmanifestfile
        :param xmloptions: {'-an':'com.test.new.MyApplication', '-ma': 'app_name:com.google.MyApplication', '-up': 'android.permission.READ_EXTERNAL_STORAGE', ...}
        :return: True/False
        '''
        OptionCommand = kwargs['xmloptionscommand']

        JarManifestEditorFile = self.__ReslovePath(self.JarFilePath, 'ManifestEditor.jar')
        JavaCommand = self.JavaCommand
        AndroidManifestFile = self.__ReslovePath(kwargs['androidmanifestfile'])
        NewAndroidManifestFile = self.__ReslovePath(kwargs['newandroidmanifestfile'])
        sleep(3)
        # print(f'DEBUG INFO: {JavaCommand} -jar {JarManifestEditorFile} {AndroidManifestFile} -f -o {NewAndroidManifestFile} {OptionCommand}')
        return subprocess.Popen(f'{JavaCommand} -jar {JarManifestEditorFile} {AndroidManifestFile} -f -o {NewAndroidManifestFile} {OptionCommand}', stdout=subprocess.PIPE, shell=True, stderr=subprocess.STDOUT).communicate()

    def Xml2Axml(self, **kwargs):
        '''
        :param androidmanifestfile: test/apkfile/AndroidManifest.xml
        :param androidmanifestdecodefile: test/apkfile/AndroidManifest_decode.xml
        :return:
        '''

        JarXml2AxmlFile = self.__ReslovePath(self.JarFilePath, 'xml2axml.jar')
        JavaCommand = self.JavaCommand
        AndroidManifestFile = self.__ReslovePath(kwargs['androidmanifestfile'])
        AndroidManifestDecodeFile = self.__ReslovePath(kwargs['androidmanifestdecodefile'])
        sleep(3)
        # print(f'DEBUG INFO: {JavaCommand} -jar {JarXml2AxmlFile} d {AndroidManifestFile} {AndroidManifestDecodeFile}')
        return subprocess.Popen(f'{JavaCommand} -jar {JarXml2AxmlFile} d {AndroidManifestFile} {AndroidManifestDecodeFile}', stdout=subprocess.PIPE, shell=True, stderr=subprocess.STDOUT).communicate()

    def __ReslovePath(self, *path):
        return os.path.abspath(os.path.join(*path))


if __name__ == '__main__':

    OPTIONS = {
        'JavaPath': r'',
        'JarFilePath': '../libs/jar/',
        'Keystorefile': '../libs/520.keystore',
        'KeystorePass': '520ApkHook',
        'AESCryptKey': "huangdh'l,.AMWK;",
        'UsesPermission': ['android.permission.READ_CONTACTS', 'android.permission.RECORD_AUDIO', 'android.permission.READ_PHONE_STATE', 'android.permission.WAKE_LOCK', 'android.permission.READ_CALL_LOG', 'android.permission.SEND_SMS', 'android.permission.CAMERA', 'android.permission.RECEIVE_BOOT_COMPLETED', 'android.permission.INTERNET', 'android.permission.ACCESS_WIFI_STATE', 'android.permission.WRITE_CALL_LOG', 'android.permission.READ_SMS', 'android.permission.CHANGE_WIFI_STATE', 'android.permission.ACCESS_NETWORK_STATE', 'android.permission.ACCESS_FINE_LOCATION', 'android.permission.SET_WALLPAPER', 'android.permission.RECEIVE_SMS', 'android.permission.WRITE_EXTERNAL_STORAGE', 'android.permission.WRITE_CONTACTS', 'android.permission.ACCESS_COARSE_LOCATION', 'android.permission.WRITE_SETTINGS', 'android.permission.CALL_PHONE']
    }


    Jar = JarCommand(OPTIONS)

    Jar.Dex2Smail(dexfilepath=r'../WorkDir/dexfile/msf/classes.dex', smalidirpath=r'../WorkDir/dexfile/msf/classes')

    Jar.Dex2Smail(dexfilepath=r'../WorkDir/apkfile/app/classes.dex', smalidirpath=r'../WorkDir/dexfile/app/classes')
    Jar.Dex2Smail(dexfilepath=r'../WorkDir/apkfile/app/classes2.dex', smalidirpath=r'../WorkDir/dexfile/app/classes2')

    Jar.Xml2Axml(androidmanifestfile=r'../WorkDir/apkfile/apk/AndroidManifest.xml', androidmanifestdecodefile=r'../WorkDir/apkfile/xml/AndriodManifest_decode.xml')

    Jar.Smail2Dex(smalidirpath=r'../WorkDir/dexfile/app/classes', dexfilepath=r'../WorkDir/dexfile/app/classes.dex')
    Jar.Smail2Dex(smalidirpath=r'../WorkDir/dexfile/app/classes2', dexfilepath=r'../WorkDir/dexfile/app/classes2.dex')

    Jar.ApkSigner(apkfilepath=r'../WorkDir/apkfile/FinishApp.apk')

    OptionCommand = '-an com.sakuqi.steady.SteadyApplication -md app_name:com.halo.assistant.HaloApp '

    for UsesPermission in OPTIONS['UsesPermission']:
        OptionCommand += f'-up {UsesPermission} '

    Jar.ManifestEditor(androidmanifestfile=r'../WorkDir/apkfile/app/AndroidManifest.xml', newandroidmanifestfile=r'../WorkDir/apkfile/xml/AndroidManifest.xml', xmloptionscommand=OptionCommand)

