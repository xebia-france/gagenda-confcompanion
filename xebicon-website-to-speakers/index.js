const {parsePage} = require("./parser");
const {getPage} = require("./parser");

getPage((html) => {
  const data = parsePage(html);
  console.log(data);
});