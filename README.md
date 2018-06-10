# Lyrebird
###### A next-generation cross-platform Twitter client that doesn't suck.

Last `master` branch's statistics :

Build : 
[![Build Status](https://jenkins.tristan.moe/job/Lyrebird/job/master/badge/icon)](https://jenkins.tristan.moe/job/Lyrebird/job/master)

Quality : 
[![Coverage](https://sonar.tristan.moe/api/project_badges/measure?project=moe.lyrebird%3Alyrebird&metric=coverage)](https://sonar.tristan.moe/project/activity?graph=coverage&id=moe.lyrebird%3Alyrebird)
[![Sonar bugs](https://sonar.tristan.moe/api/project_badges/measure?project=moe.lyrebird%3Alyrebird&metric=bugs)](https://sonar.tristan.moe/project/issues?id=moe.lyrebird%3Alyrebird&resolved=false&types=BUG)
[![Known Vulnerabilities](https://snyk.io/test/github/tristan971/lyrebird/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/tristan971/lyrebird?targetFile=pom.xml)

Trivia : 
[![LoC](https://sonar.tristan.moe/api/project_badges/measure?project=moe.lyrebird%3Alyrebird&metric=ncloc)](https://sonar.tristan.moe/project/activity?graph=coverage&id=moe.lyrebird%3Alyrebird)
[![Technical debt](https://sonar.tristan.moe/api/project_badges/measure?project=moe.lyrebird%3Alyrebird&metric=sqale_index)](https://sonar.tristan.moe/project/issues?facetMode=effort&id=moe.lyrebird%3Alyrebird&resolved=false&types=CODE_SMELL)

[![Quality gate status](https://sonar.tristan.moe/api/project_badges/quality_gate?project=moe.lyrebird%3Alyrebird)](https://sonar.tristan.moe/dashboard?id=moe.lyrebird%3Alyrebird)

## Reasoning :
Currently there is not one good Windows Twitter client.
We aim to fix this, but multiplatform-wise.

Hopefully this works out.

Check out the release pages once we have something working decently.

## Screenshot of current version :
[![Screenshot of current version](docs/img/screenshot.png)](docs/img/screenshot.png)

## Currently working :
- [x] Log in 

- [ ] Tweets-based systems
    - Systems :
        - [x] Timeline
        - [x] Mentions
    - Display features :
        - [x] Basic textual display
        - [x] Display username, @screenname and user profile picture
        - [ ] Display for chosen list
        - [ ] Text highlighting
            - [ ] Mentions
            - [ ] Hashtags
        - [ ] Media embedding
            - [ ] Officially supported
                - [ ] Twitter image embedding
                - [ ] Twitter video embedding
            - [ ] Easy embedding
                - [ ] Direct image embedding
                - [ ] Direct video embedding
            - [ ] Why is it not easy to do ?
                - [ ] Youtube video embedding
    - Loading features :
        - [x] Seeking older tweets by-demand (scroll or button)
        - [x] Seek newer tweets all the time using streaming API

- [ ] Tweeting
    - [x] Support text
    - [ ] Support image
    - [ ] Support location
    
- [ ] Interraction with tweet
    - [ ] Reply
    - [ ] Quote
    - [x] Like (formerly _favourite_)
    - [x] Retweet

- [ ] Add searches

- [ ] Add reply and retweet options

- [ ] Open pictures from within the app

- [ ] Show DM list
