# 在终端输出红色文本
function echo_red() {
  local text=$1
  local RED='\033[0;31m'
  echo -e "${RED}$text${NC}"
}

# 在终端输出绿色文本
function echo_green() {
  local text=$1
  local GREEN='\033[0;32m'
  echo -e "${GREEN}$text${NC}"
}

# 在终端输出黄色文本
function echo_yellow() {
  local text=$1
  local YELLOW="\033[33m"
  echo -e "${YELLOW}$text${NC}"
}


