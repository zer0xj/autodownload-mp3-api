# Autodownload MP3 API

## Overview

This project leverages Spring Boot, Google's YouTube API, and youtube-dl to provide an API to download videos and convert them to MP3s. It optionally supports saving a list of the queries searched for to make finding songs easier in the event that the wrong song was downloaded.

## Endpoints

### SearchController
```
GET /v1/search?query={query}
```
This endpoint queries YouTube for the specified string, and returns a list of results:
```
[
  {
    "id": "tbfUQ8k9jzk",
    "title": "Guess the Popular Song from 2010 - 2020 Music Quiz",
    "filename": null,
    "url": "https://www.youtube.com/watch?v=tbfUQ8k9jzk"
  },
  {
    "id": "95EFNsXgRhQ",
    "title": "My Name Song  CoComelon Nursery Rhymes & Kids Songs",
    "filename": null,
    "url": "https://www.youtube.com/watch?v=95EFNsXgRhQ"
  },
  {
    "id": "ejE-4uaE3GA",
    "title": "David Guetta - Say My Name (Lyrics) ft. Bebe Rexha, J Balvin",
    "filename": null,
    "url": "https://www.youtube.com/watch?v=ejE-4uaE3GA"
  },
  {
    "id": "L1yXwxr18PM",
    "title": "Kishore Kumar Hit Songs",
    "filename": null,
    "url": "https://www.youtube.com/watch?v=L1yXwxr18PM"
  },
  {
    "id": "yNr6Yi_D4HE",
    "title": "Song name",
    "filename": null,
    "url": "https://www.youtube.com/watch?v=yNr6Yi_D4HE"
  }
]
```
&nbsp;
```
GET /v1/search/download?query={query}
```
This endpoint searches YouTube, downloads the top hit, converts it to an MP3, and saves it to the location configured in application.yml:
```
Successfully downloaded https://www.youtube.com/watch?v=tbfUQ8k9jzk for search query[song name] as "Guess the Popular Song from 2010 - 2020 Music Quiz-tbfUQ8k9jzk.mp3"
```
&nbsp;
