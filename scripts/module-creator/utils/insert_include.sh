# 在 settings.gradle.kts 中插入 include
function insert_include() {
  local setting_file=$1      # settings.gradle.kts 的位置
  local group_name=$2        # 顶层 cyxbs- 后的名称，注意不包含 cyxbs-
  local primary_module=$3    # 一级模块名称
  local secondary_modules=$4 # 二级模块名称，注意包含 api-

  local insert_line="\/\/ cyxbs-$group_name"
  local insert_content="include(\":cyxbs-$group_name:$primary_module\")"
  if [ -n "$secondary_modules" ]; then
    insert_content="include(\":cyxbs-$group_name:$primary_module:$secondary_modules\")"
  fi
  if [ -z "$setting_file" ]; then
    setting_file="$(dirname "$(pwd)")/settings.gradle.kts"
  fi
  if [ "$(uname -s)" = "Darwin" ]; then
    # 注意这里 sed -i 后面的空串，在 Mac 上需要单独添加
    sed -i "" "/$insert_line/a\\\n$insert_content" "$setting_file"
  else
    sed -i "/$insert_line/a\\\n$insert_content" "$$setting_file"
  fi
}