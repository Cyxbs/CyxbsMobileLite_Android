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