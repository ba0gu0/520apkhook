#!/usr/bin/python3
# -*- coding: utf-8 -*-
from Crypto.Util.Padding import pad
from Crypto.Cipher import AES
import os
from time import sleep
class EncryptFile:
    """
    AES/CBC/PKCS5Padding 加密
    """
    def __init__(self, options):
        """
        使用密钥,加密模式进行初始化
        :param key:
        """
        key = options['AESCryptKey']
        if len(key) != 16:
            raise RuntimeError('密钥长度非16位!!!')

        self.key = key.encode('UTF-8')
        self.MODE = AES.MODE_ECB
        self.block_size = 16

    def EnCryptDexFile(self, **kwargs):
        OldDexFile = self.__ReslovePath(kwargs['olddexfile'])
        NewDexFile = self.__ReslovePath(kwargs['newdexfile'])

        with open(OldDexFile, 'rb') as file:
            data: bytes = file.read()

        EncryptFileData = self.aes_encrypt(data)

        with open(NewDexFile, 'wb') as file:
            file.write(EncryptFileData)

        sleep(0)

    def aes_encrypt(self, plaintext):
        """
        加密
        :param plaintext: 明文
        :return:
        """
        try:
            # 填充16位
            padding_text = pad(plaintext, self.block_size)
            # 初始化加密器
            cryptor = AES.new(self.key, self.MODE)
            # 进行AES加密
            encrypt_aes = cryptor.encrypt(padding_text)
            # 进行BASE64转码
            return encrypt_aes
        except Exception as e:
            print(e)

    def __ReslovePath(self, *path):
        return os.path.abspath(os.path.join(*path))


if __name__ == '__main__':
    # 测试
    OPTIONS = {
        'AESCryptKey': "huangdh'l,.AMWK;"
    }
    cryptor = EncryptFile(OPTIONS)

    for _ in ['../WorkDir/dexfile/app/classes.dex', '../WorkDir/dexfile/app/classes2.dex']:
        print(f'[*] Cryptor file {_} ...')
        cryptor.EnCryptDexFile(olddexfile=_, newdexfile=f'{_}.EnCrypt')

    print(f'Finish !')

