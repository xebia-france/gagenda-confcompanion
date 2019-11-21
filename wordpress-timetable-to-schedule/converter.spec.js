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
              description: "Fast Track <br/><br/> Spark, couplé à des solutions de type Object Storage, offre désormais la possibilité d'exécuter des traitements sur un Kubernetes managé sans passer par une distribution lourde à mettre en oeuvre. <br/><br/> Ce talk vous présentera les méthodes que l'on a expérimentées pour y parvenir et vous présentera les pièges à éviter mais surtout les solutions qui ont fonctionné dans notre cas. <br/><br/> On vous présentera : <br/><br/> - les possibilités pour déclencher un job (opérateurs Kubernetes, spark-submit, etc.)<br/> - les ressources Kubernetes générées par Spark<br/> - les paramètres utiles à l'exécution des Jobs<br/> - la configuration de la connexion avec l'object storage<br/> - une méthode pour suivre l'exécution de ses Jobs  <br/><br> Sergio Dos Santos, Consultant<br/> Guillaume Albini, Consultant  <br><br> <span style=\"background:#FE414D;width:fit-content;font-size:10px;text-transform:uppercase;color:#fff;padding:8px 12px;font-weight:600;margin-bottom: 10px;\">Data</span>  <br><br> LEVEL 🌶 🌶 🌶",
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
          "summary": "Fast Track <br/><br/> Spark, couplé à des solutions de type Object Storage, offre désormais la possibilité d'exécuter des traitements sur un Kubernetes managé sans passer par une distribution lourde à mettre en oeuvre. <br/><br/> Ce talk vous présentera les méthodes que l'on a expérimentées pour y parvenir et vous présentera les pièges à éviter mais surtout les solutions qui ont fonctionné dans notre cas. <br/><br/> On vous présentera : <br/><br/> - les possibilités pour déclencher un job (opérateurs Kubernetes, spark-submit, etc.)<br/> - les ressources Kubernetes générées par Spark<br/> - les paramètres utiles à l'exécution des Jobs<br/> - la configuration de la connexion avec l'object storage<br/> - une méthode pour suivre l'exécution de ses Jobs  <br/><br> Sergio Dos Santos, Consultant<br/> Guillaume Albini, Consultant  <br><br> <span style=\"background:#FE414D;width:fit-content;font-size:10px;text-transform:uppercase;color:#fff;padding:8px 12px;font-weight:600;margin-bottom: 10px;\">Data</span>  <br><br> LEVEL 🌶 🌶 🌶",
          'title': 'REX : Maintenant, Orange Bank devient une Entreprise Agile',
          'toTime': '2019-11-28 12:25',
          'track': 'AGILITÉ',
          'type': 'talk'
        }
      ]
    )
  });
});
