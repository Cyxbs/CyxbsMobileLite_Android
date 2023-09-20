#!/bin/bash
. ../scripts/generate-functions.sh

# 输入模块名
read -rp "请输入模块名称 (只能包含小写字母和数字): " module_name
while ! [[ "$module_name" =~ ^[a-z][a-z0-9]+$ ]]; do
  read -rp "只能包含小写字母和数字, 请重新输入: " module_name
done

# 生成模块文件
generate_common_files "$module_name" "components/$module_name"

# 生成 build.gradle.kts
generate_build_gradle "$module_name"

# 生成 api 子模块
read -rp "是否需要 api 子模块 (y 需要): " need_child
if [ "$need_child" = "y" ] || [ "$need_child" = "Y" ]; then
  generate_api_module_files_and_build_gradle "$module_name"
fi

# 在 settings.gradle.kts 的 // cyxbs-components 下面插入 include
insert_line="\/\/ cyxbs-components"
insert_content="include(\":cyxbs-components:$module_name\")"
if [ "$need_child" = "y" ] || [ "$need_child" = "Y" ]; then
  insert_content="include(\":cyxbs-components:$module_name\", \":cyxbs-components:$module_name:api-$module_name\")"
fi
insert_include "$insert_line" "$insert_content"

# 结果输出
GREEN='\033[0;32m'
if [ "$need_child" = "y" ] || [ "$need_child" = "Y" ]; then
  echo -e "${GREEN}已生成 $module_name 模块以及其 api 模块 api-$module_name${NC}"
else
  echo -e "${GREEN}已生成 $module_name 模块${NC}"
fi
RED='\033[0;31m'
echo -e "${RED}最后请点击右上角的大象刷新 gradle${NC}"
