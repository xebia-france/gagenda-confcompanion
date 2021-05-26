# Spreadsheet to JSON converter
> Convert Spreadsheet speakers to Conf Companion JSON

# HowTo

## Setup environment variable
> I use [direnv](https://direnv.net/) to manage my environment.

```
export SPREADSHEET=11cjaUmiOkdPq0bxdu9NgWC2gGYkczPL3ZstdH9fymgg
export RANGE=A:F
export SCHEDULE=https://s3.eu-central-1.amazonaws.com/blacroix-conf-companion/dataxday/schedule.json
export S3_DIR=dataxday
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
> https://console.cloud.google.com/apis/credentials?project=conf-companion

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

💡 Ask [@blacroix](https://github.com/blacroix) for more information.