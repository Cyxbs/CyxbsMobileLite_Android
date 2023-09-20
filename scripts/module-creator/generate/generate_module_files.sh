# 生成模块以及其 api 模块通用的文件
function generate_module_files() {
  local module_name=$1 # 模块名字
  local group_name=$2  # 顶层 cyxbs- 后的名称
  local folder_head=$3 # 模块 src 的文件路径，填入脚本运行的相对位置

  # 创建模块文件夹
  mkdir -p "$folder_head/src/main/java/com/cyxbs/$group_name/$module_name"
  mkdir -p "$folder_head/src/main/res"
  # 创建杂文件
  echo "/build" >"$folder_head/.gitignore"
  echo "class Test" >"$folder_head/src/main/java/com/cyxbs/$group_name/$module_name/Test.kt"
  # 创建 AndroidManifest.xml
  local manifest='<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
  <application>
  </application>
</manifest>'
  echo "$manifest" >"$folder_head/src/main/AndroidManifest.xml"
}
