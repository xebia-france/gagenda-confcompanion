const request = require('request');
const cheerio = require('cheerio');
const url = process.env.URL || 'https://xebicon.fr/our-speakers/';

const getPage = cb => {
  request(url, {
    timeout: 3000
  }, (error, response, body) => {
    if (!error) {
      cb(body);
    }
  });
};

function setName($, e, speaker) {
  const elements = $(e).find('strong');
  if (elements.length > 0) {
    speaker.firstName = elements.first().text().replace(/\s+/g, ' ').split('–')[0].trim();
  }
}

function setImage($, e, speaker) {
  const elements = $(e).find('img.popupaoc-img');
  if (elements.length > 0) {
    speaker.imageURL = elements.first().attr('src');
  }
}

const parsePage = (data) => {
  const $ = cheerio.load(data);
  const r = [];
  $('.vc_row .vc_column_container .wpb_wrapper')
    .each((i, e) => {
      const speaker = {};
      setName($, e, speaker);
      setImage($, e, speaker);
      console.log(speaker);
      if (speaker.firstName) {
        r.push(speaker);
      }
    });
  return r;
};

module.exports = {parsePage, getPage};