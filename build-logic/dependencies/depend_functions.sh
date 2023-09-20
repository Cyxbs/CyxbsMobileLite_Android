# 遍历文件中 fun Project.depend*() 中的 * 内容
# 输入一个文件地址，输出一个可以依赖的 list 集合
function get_dependency_list_by_file() {
  local file=$1 # 需要读取文件的文件路径
  local chosen_dependency_list=("${@:2}")

  # 判断文件是否存在且为普通文件
  if [ -f "$file" ]; then
    # 逐行读取文件内容，并匹配以"fun Project.depend"开头的行
    while IFS= read -r line; do
      if echo "$line" | grep -q "^fun Project.depend"; then
        # 提取出 fun Project.depend*() 中 * 的内容 (GPT 生成的，看不懂可以问问 GPT)
        dependency=$(echo "$line" | sed -n 's/^fun Project.depend\([^()]*\)(.*/\1/p')
        # 去掉 chosen_dependency_list 已经存在的项
        found=false
        for chosen_dependency in "${chosen_dependency_list[@]}"; do
          if [[ "$dependency" == "$chosen_dependency" ]]; then
            found=true
            break
          fi
        done
        # 如果不存在 chosen_dependency_list 中则输出，最后会变成函数的返回值
        if [[ "$found" == false ]]; then
          echo "$dependency"
        fi
      fi
    done <"$file"
  fi
}

# 遍历指定目录下的所有文件，并读取 fun Project.depend*() 中的 * 内容
# 输入一个目录地址，输出一个可以依赖的 list 集合
function get_dependency_list_dir() {
  local directory=$1 # 需要读取文件的文件夹路径
  local chosen_dependency_list=("${@:2}")

  # 遍历指定文件夹下的所有文件
  for file in "$directory"/*; do
    get_dependency_list_by_file "$file" "${chosen_dependency_list[@]}"
  done
}

# 得到选择的依赖集合
# 输入一个依赖 list 集合，输出一个选择的依赖 list 集合
function get_chosen_dependency_list() {
  local list=("$@")

  local chosen_dependency_list=()
  local list_len=${#list[@]}
  local group_size=9 # 定义每组的元素数量
  local start=0
  while ((start < list_len)); do
    end=$((start + group_size - 1))
    # 如果结束索引超出列表长度，将其设置为列表的最后一个索引
    if ((end >= list_len)); then
      end=$((list_len - 1))
    fi
    # 打印 [i]XXX
    for ((j = start; j <= end; j++)); do
      echo "[$((j - start + 1))]${list[j]}" >/dev/tty # 重定向到终端输出
    done
    # 选择编号
    read -rp "选择需要的依赖: " chose
    # 去除重复的数字
    chose=$(echo "$chose" | sed 's/\(.\)/\1\n/g' | sort -u | tr -d '\n')
    # 循环遍历 chose 并获取对应依赖保存到 result_chosen_dependency_list 中
    for ((i = 0; i < ${#chose}; i++)); do
      char="${chose:i:1}"
      chosen_dependency_list+=("${list[char - 1 + start]}")
    done
    # 清除列表
    for ((j = start; j <= end + 1; j++)); do
      tput cuu1 >/dev/tty # 光标上移一行
      tput el >/dev/tty   # 清除当前行
    done
    start=$((start + group_size - 1))
  done

  # 输出结果
  echo "${chosen_dependency_list[@]}"
}


#############################################
#
#                 向外暴露方法区
#
#############################################


# 添加 android 相关依赖到 build.gradle.kts
function depend_android() {
  local build_logic_path=$1 # build-logic 的目录位置
  local build_gradle_path=$2 # 模块 build.gradle.kts 的文件地址，用于读取已经选择的依赖

  local core_dir="$build_logic_path/dependencies/android/src/main/kotlin"

  # 已经依赖了的，如果你想实现黑名单，则可以给他添加元素
  local chosen_dependency_list_old=($(get_dependency_list_by_file "$build_gradle_path"))
  chosen_dependency_list_old+=("AndroidBase")
  chosen_dependency_list_old+=("AndroidView")
  chosen_dependency_list_old+=("AndroidKtx")
  chosen_dependency_list_old+=("AndroidKtx")
  chosen_dependency_list_old+=("LifecycleKtx")

  local dependency_list=($(get_dependency_list_dir "$core_dir" "${chosen_dependency_list_old[@]}"))
  local chosen_dependency_list_new=($(get_chosen_dependency_list "${dependency_list[@]}"))

  # 打印输出保存的列表
  for dependency in "${chosen_dependency_list_new[@]}"; do
    echo "depend$dependency" >> "$build_gradle_path"
  done
}

# 添加其他第三方相关依赖到 build.gradle.kts
function depend_others() {
  local build_logic_path=$1  # build-logic 的目录位置
  local build_gradle_path=$2 # 模块 build.gradle.kts 的文件地址，用于读取已经选择的依赖

  local core_dir="$build_logic_path/dependencies/others/src/main/kotlin"

  # 已经依赖了的，如果你想实现黑名单，则可以给他添加元素
  local chosen_dependency_list_old=($(get_dependency_list_by_file "$build_gradle_path"))

  local dependency_list=($(get_dependency_list_dir "$core_dir" "${chosen_dependency_list_old[@]}"))
  local chosen_dependency_list_new=($(get_chosen_dependency_list "${dependency_list[@]}"))

  # 打印输出保存的列表
  for dependency in "${chosen_dependency_list_new[@]}"; do
    echo "depend$dependency" >>"$build_gradle_path"
  done
}
