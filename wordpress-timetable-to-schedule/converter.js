const CONFERENCE_ID = process.env.CONFERENCE_ID || 'cc-20191128';
const DATE = process.env.DATE || '2019-11-28';

function clean(description) {
  return description
    .replace(/\r\n/g, '')
    .replace(/<img.*level-on.png.*\/>/g, '🌶')
    .replace(/<img.*level-off.png.*\/>/g, '');
}

function convertSpeakers(speakers) {
  return speakers
    .split(',')
    .map(name => ({
      name: name.replace(/^ | $/, ''),
      id: name.toLocaleLowerCase().replace(/[^A-Za-z]/g, '')
    }));
}

function convertType(title) {
  if (title.indexOf('Pause déjeuner') >= 0) {
    return 'déjeuner';
  } else if (title.indexOf('Pause') >= 0) {
    return 'pause';
  } else if (title.indexOf('Cocktail') >= 0) {
    return 'cocktail';
  } else if (title.indexOf('Keynote') >= 0) {
    return 'keynote';
  } else {
    return 'talk';
  }
}

function convertKind(title) {
  if (title.indexOf('Pause') >= 0 || title.indexOf('Cocktail') >= 0 || title.indexOf('Keynote - XebiKart') >= 0) {
    return 'keynote';
  } else {
    return 'talk';
  }
}

function removeDuplicate(events) {
  const ref = [];
  ref['pausedjeuner1225'] = '';
  return events.filter(event => {
    const key = `${event.title}${event.start_hour}${event.start_minute}`
      .toLocaleLowerCase()
      .replace(/[^A-Za-z0-9]/g, '');

    if (ref[key] !== undefined) {
      return false;
    }

    ref[key] = '';
    return true;
  });
}

const COLORS_BY_TRACK = {
  '#69b42d': 'CRAFT',
  '#fe414d': 'DATA',
  '#8d61d3': 'DEVOPS',
  '#04c0e0': 'DEVOPS',
  '#d376c6': 'AGILITÉ',
  '#f9ca1d': 'CLOUD',
  '#ab9368': 'DBT',
  '#19bfa3': 'MOBILE',
  '#f3a86d': 'CLOUD',
  '#9ae161': 'BACK',
  '#3a71b8': 'WEB',
  '#46a3e8': 'CONF',
  '#c9c203': 'IOT',
};

function convertTrack(color) {
  return COLORS_BY_TRACK[color];
}

function convert(timetable) {
  const {schedule: {events, columns}} = timetable;

  const filteredEvents = events
    .map(event => ({column_id: parseInt(event.column_id, 10), ...event}))
    .filter(event => event.column_id > 0);

  const uniqEvents = removeDuplicate(filteredEvents);

  const convertedEvents = uniqEvents
    .map(event => ({
      conferenceId: CONFERENCE_ID,
      fromTime: `${DATE} ${event.start_hour}:${event.start_minute}`,
      toTime: `${DATE} ${event.end_hour}:${event.end_minute}`,
      id: `cc-${event.id}`,
      room: columns[event.column_id - 1].title,
      speakers: convertSpeakers(event.description_inline),
      summary: clean(event.description),
      title: event.title,
      type: convertType(event.title),
      kind: convertKind(event.title),
      track: convertTrack(event.color),
    }));

  for (let i = 0; i < convertedEvents.length; i++) {
    if (convertedEvents[i].kind === 'keynote') {
      convertedEvents[i].room = columns[0].title;
    }
    if (convertedEvents[i].room === columns[7].title) {
      convertedEvents[i].type = 'hands-on';
    }
  }

  return convertedEvents.sort((a, b) => a.room.localeCompare(b.room));
}

module.exports = {convert};
