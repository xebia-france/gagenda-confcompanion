[![Waffle.io - Columns and their card count](https://badge.waffle.io/xebia-france/google-calendar-conf-companion.svg?columns=all)](https://waffle.io/xebia-france/google-calendar-conf-companion)

# Google Agenda to JSON Converter
> Convert Google Agenda events to Conf Companion JSON

# HowTo

## Setup environment variable
> I use [direnv](https://direnv.net/) to manage my environment.

```
export CALENDAR_ID=xebia.fr_sh679blpn2vkmhk7i1rdllo3t0@group.calendar.google.com
export DAY_FROM=20180517
export DURATION=1
export ROOM=false
export S3_DIR=xke
export SPEAKER=true
```

```
$> ./gradlew run
```

# Secret files in resources/credentials/

- aws_secret.json

> See your AWS account.

```
{
  "access_key": "",
  "secret_key": "",
  "region": "eu-central-1",
  "bucketName": ""
}
```

- client_secret.json

> See Google Api application credentials.

```
{
  "installed": {
    "client_id": "",
    "project_id": "",
    "auth_uri": "",
    "token_uri": "",
    "auth_provider_x509_cert_url": "",
    "client_secret": "",
    "redirect_uris": [
        ...
    ]
  }
}
```

- firebase_adminsdk.json

> See Firebase Admin Sdk application credentials.

```
{
  "type": "",
  "project_id": "",
  "private_key_id": "",
  "private_key": "",
  "client_email": "",
  "client_id": "",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://accounts.google.com/o/oauth2/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": ""
}
```

💡 Ask [@blacroix](https://github.com/blacroix) for more information.