const fs = require('fs');

const {parsePage} = require("./parser");

describe('Parser', () => {
  it('should parse speakers page', (done) => {
    fs.readFile('./extract.html', 'utf8', (err, data) => {
      const speakers = parsePage(data);
      expect(speakers).toEqual([
        {
          // bio: '',
          firstName: 'John Doe',
          // id: '',
          imageURL: 'https://xebicon.fr/wp-content/uploads/2019/10/benjamin-lacroix-300x300.jpg',
          // lastName: '',
          // talks: [{
          //   id: '',
          //   title: '',
          // }],
          // tweetHandle: ''
        },
        {
          // bio: '',
          firstName: 'Marc Tom',
          // id: '',
          imageURL: 'https://xebicon.fr/wp-content/uploads/2019/10/benjamin-lacroix-300x300.jpg',
          // lastName: '',
          // talks: [{
          //   id: '',
          //   title: '',
          // }],
          // tweetHandle: ''
        },
        {
          // bio: '',
          firstName: 'Mac Book',
          // id: '',
          imageURL: 'https://xebicon.fr/wp-content/uploads/2019/10/benjamin-lacroix-300x300.jpg',
          // lastName: '',
          // talks: [{
          //   id: '',
          //   title: '',
          // }],
          // tweetHandle: ''
        }
      ]);
      done();
    });
  });
});