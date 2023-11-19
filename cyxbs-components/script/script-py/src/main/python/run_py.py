import io
import sys


# 获取print输出的装饰器
def __console(_fun):
    def wrapper(*args, **kw):
        old_stdout = sys.stdout
        sys.stdout = mystdout = io.StringIO()
        _fun(*args, **kw)
        sys.stdout = old_stdout
        tips = mystdout.getvalue()
        return tips

    return wrapper


@__console
def __run(code: str):
    # 提升code的函数作用域
    _globals={}
    exec(code,_globals)


def getByPyScript(code: str):
    '''
    运行一段python代码字符串并返回其print的内容
    :param code: 将要执行的python代码
    :return: 返回代码的print输出内容，请将程序执行的结果用print输出。
    '''
    return __run(code)
