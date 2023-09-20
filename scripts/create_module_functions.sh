# 生成模块以及其 api 模块通用的文件
function generate_common_files() {
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

# 生成通用的 build.gradle.kts
function generate_common_build_gradle() {
  local folder_head=$1      # build.gradle.kts 的文件路径，填入脚本运行的相对位置
  local is_single_module=$2 # 是否单模块调试，y 则是，其他则否

  local plugin_id="module_name"
  if [ "$is_single_module" = "y" ] || [ "$is_single_module" = "Y" ]; then
    plugin_id="module-single"
    capitalized="$(echo "${module_name:0:1}" | tr '[:lower:]' '[:upper:]')${module_name:1}"
    # 生成单模块调试的界面入口文件
    echo "package $module_name

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.cyxbs.frameworks.singlemodule.ISingleModuleEntry

@AutoService(ISingleModuleEntry::class.java)
class ${capitalized}SingleModuleEntry : ISingleModuleEntry {
  override fun getPage(): Any {
    TODO(\"返回一个 Intent 或者 Fragment\")
  }
}" >"$module_name/src/single/$module_name/${capitalized}SingleModuleEntry.kt"
  fi
  # 输出 gradle 文件
  echo -e "plugins {\n  id(\"$plugin_id\")\n}\n" >"$folder_head/build.gradle.kts"
}

# 创建普通模块
function create_module() {
  local module_name=$1      # 模块名字
  local group_name=$2       # 顶层 cyxbs- 后的名称，注意不包含 cyxbs-
  local is_single_module=$3 # 是否单模块调试，y 则是，其他则否

  # 生成模块文件
  generate_common_files "$module_name" "$group_name" "$module_name"
  # 生成通用的 build.gradle.kts
  generate_common_build_gradle "$module_name" "$is_single_module"

  # 结果输出
  local GREEN='\033[0;32m'
  echo -e "${GREEN}已生成 $module_name 模块${NC}"
}

# 创建 api 子模块
function create_api_module() {
  local parent_name=$1 # 父模块名称
  local group_name=$2  # 顶层 cyxbs- 后的名称，注意不包含 cyxbs-

  # 生成模块文件
  generate_common_files "$parent_name" "$group_name" "$parent_name/api-$parent_name"
  # 生成通用的 build.gradle.kts
  generate_common_build_gradle "$parent_name/api-$parent_name" "n"

  # 结果输出
  local GREEN='\033[0;32m'
  echo -e "${GREEN}已在 $parent_name 模块下生成 api-$parent_name 模块${NC}"
}


