let tbodyElement = document.querySelector('.printTable table tbody');
let trElements = tbodyElement.querySelectorAll('tr');
let data = [];
trElements.forEach(function(trElement) {
  let array = []
  let tdElements = trElement.querySelectorAll('td')
  tdElements.forEach(function(tdElement) {
    array.push(tdElement.textContent)
  })
  let beginLesson = parseInt(array[8].substring(array[8].indexOf("第") + 1, array[8].indexOf("-")))
  data.push({
    week: parseInt(array[6].substring(0, array[6].indexOf("周"))),
    weekNum: parseInt(array[7]),
    startTime: array[8].substring(array[8].indexOf("节") + 2, array[8].lastIndexOf("-")),
    endTime: array[8].substring(array[8].lastIndexOf("-") + 1),
    beginLesson: beginLesson,
    period: parseInt(array[8].substring(array[8].indexOf("-") + 1, array[8].indexOf("节"))) - beginLesson + 1,
    name: array[5],
    classNumber: array[4],
    type: array[3],
    room: array[9],
    seat: array[10]
  })
});

const json = JSON.stringify(data);
console.log(json);
cyxbsBridge.success(json);

