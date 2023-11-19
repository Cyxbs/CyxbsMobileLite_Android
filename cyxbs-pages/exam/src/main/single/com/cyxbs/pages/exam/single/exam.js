let tbodyElement = document.querySelector('.printTable table tbody');
let trElements = tbodyElement.querySelectorAll('tr');
let data = [];
trElements.forEach(function(trElement) {
  data.push(transformTrElement(trElement))
});

function transformTrElement(trElement) {
  let data = []
  let tdElements = trElement.querySelectorAll('td')
  tdElements.forEach(function(tdElement) {
    data.push(tdElement.textContent)
  }
  let beginLesson = parseInt(data[8].substring(data[8].indexOf("第") + 1, data[8].indexOf("-")))
  return {
    week: parseInt(data[6].substring(0, data[6].indexOf("周"))),
    weekNum: parseInt(data[7]),
    startTime: data[8].substring(data[8].indexOf("节") + 2, data[8].lastIndexOf("-")),
    endTime: data[8].substring(data[8].lastIndexOf("-") + 1),
    beginLesson: beginLesson,
    period: parseInt(data[8].substring(data[8].indexOf("-") + 1, data[8].indexOf("节"))) - beginLesson + 1,
    name: data[5],
    classNumber: data[4],
    type: data[3],
    room: data[9],
    seat: data[10]
  }
}

const json = JSON.stringify(data);
console.log(json);
//cyxbsBridge.success(json);

