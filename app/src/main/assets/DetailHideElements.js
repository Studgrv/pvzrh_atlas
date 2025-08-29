setTimeout(() => {
  const elementsToDelete = document.querySelector('.icon-delete');

  if (elementsToDelete) {
    elementsToDelete.click();
  }
}, 500);

var selectorsToHide = [
  '#wikiToapp',
  '.wiki-header',
  "#siteNotice",
];

selectorsToHide.forEach(function (selector) {
  var elements = document.querySelectorAll(selector);
  elements.forEach(function (element) {
    if (element) {
      element.style.display = 'none';
    }
  });
});

