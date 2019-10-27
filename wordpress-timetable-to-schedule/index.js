const fs = require('fs');
const {convert} = require('./converter');

const FILENAME = process.env.FILE || './timetable.json';

async function run() {
  const content = await new Promise((resolve, reject) => {
    fs.readFile(FILENAME, 'utf8', (error, data) => {
      if (error) return reject(error);
      resolve(data);
    })
  });
  const data = convert(JSON.parse(content));
  console.log(JSON.stringify(data));
}

run();
