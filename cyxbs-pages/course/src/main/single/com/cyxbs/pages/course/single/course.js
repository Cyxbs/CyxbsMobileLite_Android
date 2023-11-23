var xhr = new XMLHttpRequest();
xhr.open('POST', 'https://be-prod.redrock.cqupt.edu.cn/magipoke-jwzx/kebiao', true);
xhr.onload = function() {
  if (xhr.status === 200) {
    androidBridge.success(xhr.responseText);
  } else {
    androidBridge.error('请求失败: ' + xhr.status);
  }
};
var formData = new FormData();
formData.append('stu_num', '{stu_num}');
xhr.send(formData);