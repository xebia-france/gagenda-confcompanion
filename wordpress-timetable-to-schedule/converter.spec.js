const {convert} = require('./converter');

describe('Convert', () => {
  it('should convert timetable talk to cc talk', () => {
    const schedule = convert(
      {
        schedule: {
          columns: [
            {
              id: '1',
              schedule_id: 0,
              title: 'Grand auditorium',
              description: '',
              color: '#F00',
              custom_class: 'grand-auditorium'
            },
            {
              id: '1',
              schedule_id: 0,
              title: 'Grand auditorium',
              description: '',
              color: '#F00',
              custom_class: 'grand-auditorium'
            },
            {
              id: '1',
              schedule_id: 0,
              title: 'Grand auditorium',
              description: '',
              color: '#F00',
              custom_class: 'grand-auditorium'
            },
            {
              id: '1',
              schedule_id: 0,
              title: 'Grand auditorium',
              description: '',
              color: '#F00',
              custom_class: 'grand-auditorium'
            },
            {
              id: '1',
              schedule_id: 0,
              title: 'Grand auditorium',
              description: '',
              color: '#F00',
              custom_class: 'grand-auditorium'
            },
            {
              id: '1',
              schedule_id: 0,
              title: 'Grand auditorium',
              description: '',
              color: '#F00',
              custom_class: 'grand-auditorium'
            },
            {
              id: '1',
              schedule_id: 0,
              title: 'Grand auditorium',
              description: '',
              color: '#F00',
              custom_class: 'grand-auditorium'
            },
            {
              id: '1',
              schedule_id: 0,
              title: 'Hands on',
              description: '',
              color: '#F00',
              custom_class: 'grand-auditorium'
            }
          ],
          events: [
            {
              id: '11',
              column_id: '1',
              schedule_id: '1',
              title: 'REX : Maintenant, Orange Bank devient une Entreprise Agile',
              location: '',
              start_hour: '11',
              start_minute: '55',
              end_hour: '12',
              end_minute: '25',
              length: '30',
              size: '1',
              timeslot_parent_id: '0',
              timeslots: '',
              timeslot_fields: 'a:15:{s:5:"title";i:1;s:5:"color";i:1;s:15:"background_type";i:1;s:20:"background_image_url";i:1;s:23:"background_image_darken";i:1;s:7:"onclick";i:1;s:12:"onclick_link";i:1;s:21:"onclick_link_new_page";i:1;s:10:"media_type";i:1;s:11:"media_image";i:1;s:13:"media_youtube";i:1;s:12:"custom_class";i:1;s:11:"meta_fields";i:1;s:11:"description";i:1;s:18:"description_inline";i:1;}',
              timeslot_fields_method: '0',
              color: '#d376c6',
              onclick: 'popup',
              onclick_link: '',
              onclick_link_new_page: '0',
              media_type: '0',
              media_image: '',
              media_youtube: '',
              custom_class: '',
              meta_fields: 'a:2:{i:1;a:3:{s:7:"enabled";i:0;s:6:"line_1";s:20:"{custom_field_title}";s:6:"line_2";s:0:"";}i:2;a:3:{s:7:"enabled";i:0;s:6:"line_1";s:20:"{custom_field_title}";s:6:"line_2";s:0:"";}}',
              description_inline: 'Laurent Dussault, John Doe',
              description: 'Retour d\'Exp\u00e9rience Client (REX)\r\n<br\/><br\/>\r\nOrange Bank f\u00eate son 2\u00e8me anniversaire, l\'occasion pour l\'agence de la transformation digitale & agile de faire son retour d\'exp\u00e9rience sur les changements qu\'elle accompagne.\r\nPlanification trimestrielle  commune, Obeya, Tribus, DevOps\u2026 d\u00e9couvrez les diff\u00e9rentes escales du voyage vers l\u2019Entreprise Agile.\r\n<br\/><br>\r\nLaurent Dussault, Coach DevOps & Agile<br\/>\r\nMarina Sosniak et Delphine Le Gal, charg\u00e9es de la transformation digitale et agile chez Orange Bank\r\n\r\n<br><br>\r\n<span style="background:#D376C6;width:fit-content;font-size:10px;text-transform:uppercase;color:#fff;padding:8px 12px;font-weight:600;margin-bottom: 10px;">Agilit\u00e9<\/span>\r\n\r\n<br><br>LEVEL <img title="level" src="https:\/\/xebicon.fr\/wp-content\/uploads\/2019\/09\/level-on.png" alt="level" width="20" \/>\r\n<img title="level" src="https:\/\/xebicon.fr\/wp-content\/uploads\/2019\/09\/level-off.png" alt="level" width="20">\r\n<img title="level" src="https:\/\/xebicon.fr\/wp-content\/uploads\/2019\/09\/level-off.png" alt="level" width="20" \/>',
              background_type: 'color',
              background_image_url: '',
              background_image_darken: '0'
            }
          ]
        }
      }
    );
    expect(schedule).toEqual(
      [
        {
          'conferenceId': 'cc-20191128',
          'fromTime': '2019-11-28 11:55',
          'id': 'cc-11',
          'kind': 'talk',
          'room': 'Grand auditorium',
          'speakers': [
            {
              'id': 'laurentdussault',
              'name': 'Laurent Dussault'
            },
            {
              'id': 'johndoe',
              'name': 'John Doe'
            }
          ],
          'summary': 'Retour d\'Expérience Client (REX)<br/><br/>Orange Bank fête son 2ème anniversaire, l\'occasion pour l\'agence de la transformation digitale & agile de faire son retour d\'expérience sur les changements qu\'elle accompagne.Planification trimestrielle  commune, Obeya, Tribus, DevOps… découvrez les différentes escales du voyage vers l’Entreprise Agile.<br/><br>Laurent Dussault, Coach DevOps & Agile<br/>Marina Sosniak et Delphine Le Gal, chargées de la transformation digitale et agile chez Orange Bank<br><br><span style="background:#D376C6;width:fit-content;font-size:10px;text-transform:uppercase;color:#fff;padding:8px 12px;font-weight:600;margin-bottom: 10px;">Agilité</span><br><br>LEVEL 🌶',
          'title': 'REX : Maintenant, Orange Bank devient une Entreprise Agile',
          'toTime': '2019-11-28 12:25',
          'track': 'AGILITÉ',
          'type': 'talk'
        }
      ]
    )
  });
});
