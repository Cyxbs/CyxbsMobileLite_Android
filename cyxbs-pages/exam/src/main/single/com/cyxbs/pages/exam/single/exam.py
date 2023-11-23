import requests
import json
from lxml import etree


def GetExam(id=None):
    url = "http://jwzx.cqupt.edu.cn/ksap/showKsap.php?type=stu&id=" + str(id)
    req = requests.get(url)
    return getData(req.text)


def getData(text):
    # 使用 lxml 的 HTML 解析器解析 HTML 内容
    html_tree = etree.HTML(text)

    # 找到表格元素
    table = html_tree.xpath('//table')[0]

    # 提取表头信息
    headers = [convert(header) for header in table.xpath('.//thead/tr/td/text()')]

    # 提取表格数据
    exam_data = []
    rows = table.xpath('.//tbody/tr')
    for row in rows:
        row_data = [data.strip() for data in row.xpath('.//td/text()')]
        row_data[6] = row_data[6].replace("周","")
        result = dict(zip(headers, row_data))
        time = str(row_data[8])
        time = time.split(" ")
        startTime = time[1].split("-")[0]
        endTime = time[1].split("-")[1]
        result.update({"startTime": startTime})
        result.update({"endTime": endTime})
        result.update({"beginLesson": time[0].split("-")[0].replace("第", '')})
        result.update({"period": -eval(time[0].replace("第", '').replace("节", ''))})
        result.pop(None)
        result.pop('time')
        exam_data.append(result)

    return json.dumps(exam_data, ensure_ascii=False)


def convert(text):
    switcher = {
        "考试周次": "week",
        "星期": "weekNum",
        "考试类型": "type",
        "课程编号": "classNumber",
        "课程名称": "name",
        "考试地点": "room",
        "考生座位": "seat",
        "考试时间": "time"
    }
    res = switcher.get(text)
    return res


print(GetExam("{stuNum}"))