#!/usr/bin/python3
# -*- coding: utf-8 -*-

import os
from random import choice
from rich.highlighter import Highlighter
from rich.console import Console
from rich.tree import Tree

class RainbowHighlighter(Highlighter):
    def __init__(self, colorlist):
        self.color = colorlist

    def highlight(self, text):
        for index in range(0, len(text), 3):
            text.stylize(f"color({choice(self.color)})", index, index + 3)

class CustomPrint():
    def __init__(self):
        self.CustomColor = {
            "info" : range(214, 232),
            "error": range(196, 214),
            "success": range(106, 124),
            "logo": range(0, 255)
        }
        self.Spinner = ['circle', 'balloon', 'simpleDots', 'christmas', 'monkey', 'moon', 'point', 'runner', 'weather']
        self.Console = Console()

    def PrintInput(self, text):
        return self.Console.input(f'{text} :backhand_index_pointing_right: : [bold orange]')

    def PrintLogo(self, text):
        rainbow = RainbowHighlighter(self.CustomColor['logo'])
        self.Console.print(rainbow(text))

    def PrintInfo(self, text):
        rainbow = RainbowHighlighter(self.CustomColor['info'])
        self.Console.print(rainbow(f'[*] {text}\n'))

    def PrintError(self, text):
        rainbow = RainbowHighlighter(self.CustomColor['error'])
        self.Console.print(rainbow(f'[!] {text}\n'))

    def PrintSuccess(self, text):
        rainbow = RainbowHighlighter(self.CustomColor['success'])
        self.Console.print(rainbow(f'[+] {text}\n'))

    def PrintRule(self, text, style = 'green'):
        self.Console.rule(text, style = style)

    def PrintStatus(self, text, fun, **kwargs):
        with self.Console.status(text, spinner=choice(self.Spinner)):
            return fun(**kwargs)

    def PrintDirTree(self, dirpath, dirdepth):
        DirTree = Tree(f'目录结构:\n{os.path.basename(dirpath)}', guide_style='green')
        self.__ListDir(DirTree, dirpath, dirdepth)
        self.Console.print(DirTree)

    def __ListDir(self, dirtree, dirpath, dirdepth):
        Color = ['blue', 'orange' 'yellow', 'green', 'cyan', 'purple', 'red'][dirdepth]
        dirdepth -= 1
        if dirdepth <= 0:
            return
        for _ in os.listdir(dirpath):
            Path = self.__ReslovePath(dirpath, _)
            if os.path.isfile(Path):
                dirtree.add(f':page_facing_up:{_}')

            if os.path.isdir(Path):
                if _ not in ['', os.curdir, os.pardir]:
                    self.__ListDir(dirtree.add(f':file_folder:{_}', guide_style=Color), Path, dirdepth)

    def __ReslovePath(self, *args):
        return os.path.abspath(os.path.join(*args))



if __name__ == '__main__':

    def SysPing():
        os.system('ping -c 4 baidu.com > /dev/null')

    Print = CustomPrint()
    Print.PrintStatus('baksmali classes.dex ing...', SysPing)
    Print.PrintInfo('Hello Word !')
    Print.PrintError('Hello Word !')
    Print.PrintSuccess('Hello Word !')
    Print.PrintRule('Started')

    code = '''
.method public onCreate()V
    invoke-static {p0}, Lcom/mlekwcrato/fliwxevbrm/ruabpeqgjl;->start(Landroid/content/Context;)V
    .registers 4
 '''
    Print.PrintLogo(code)