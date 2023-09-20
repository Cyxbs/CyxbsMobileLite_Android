

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