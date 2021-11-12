#!/usr/bin/python3
# -*- coding: utf-8 -*-

import zipfile
import os
from time import sleep

class ZipApkFile():

    def __init__(self, filepath, mode, options):
        self.FilePath = os.path.abspath(filepath)
        self.zip = zipfile.ZipFile(self.FilePath, mode)
        self.ZipExtractPath = options['ZipExtractPath']
        self.ZipCompressPath = options['ZipCompressPath']

    def UnZipApk(self, **kwargs):
        '''

        :param kwargs: zipextractpath
        :return:
        '''
        self.zip.extractall(self.ZipExtractPath)
        sleep(0)
        self.zip.close()

    def ZipApk(self, **kwargs):
        '''

        :param kwargs: zipcompresspath
        :return:
        '''

        for path in os.listdir(self.ZipCompressPath):
            zippath = os.path.basename(path)
            if not zippath:
                zippath = os.path.basename(os.path.dirname(path))
            if zippath in ('', os.curdir, os.pardir):
                zippath = ''
            path = os.path.join(self.ZipCompressPath, path)
            self.__addToZip(path, zippath)
        sleep(0)
        self.zip.close()

    def __addToZip(self, path, zippath):
        # print(path, zippath)
        if os.path.isfile(path):
            self.zip.write(path, zippath, zipfile.ZIP_DEFLATED)
        elif os.path.isdir(path):
            if zippath:
                self.zip.write(path, zippath)
            for nm in os.listdir(path):
                self.__addToZip(os.path.join(path, nm), os.path.join(zippath, nm))


if __name__ == '__main__':
    OPTIONS = {
        'ZipExtractPath': '../WorkDir/apkfile/app/',
        'ZipCompressPath': '../WorkDir/apkfile/app/'
    }
    # apk = ZipApkFile(r'../app.apk', 'r', OPTIONS)
    # apk.UnZipApk()

    apk = ZipApkFile(r'../WorkDir/AllFinish.apk', 'w', OPTIONS)
    apk.ZipApk()
