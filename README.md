# CyxbsMobileLite_Android
掌上重邮极速版

**注：目前仍处于开发阶段，欢迎大家对于代码的批评指正**

## 规划
极速版不作为只有重邮能使用的 App，而是作为高校都可以使用的通用 App，目前计划提供以下功能
- 课表
- 课表事务
- 考试
- 查找学生
- 查找老师

## 架构设计
https://www.processon.com/view/link/650a6e022e87a366062e0479

目前还处于探索阶段，并不代表最终的架构设计，欢迎大家对于架构进行批评指正

## 数据源
作为通用 App，极速版将不搭建数据源，不依赖固定的后端服务  
数据可通过以下方式进行获取: 
- 某后端接口 + js 进行数据转换
- 通过 js 获取数据

## 进度
- [x] 基础框架的搭建
- [x] idea 配套模块构建工具 ([项目链接](https://github.com/Cyxbs/CyxbsIdeaPlugin))
- [x] 服务提供框架 (路由框架) ([KtProvider](https://github.com/985892345/KtProvider))
- [x] 单模块功能搭建
- [ ] 数据源层搭建
