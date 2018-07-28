![Promo logo](docs/img/promo-logo-png.png)

Last `develop` branch's statistics :

[![Build Status](https://jenkins.tristan.moe/job/Lyrebird/job/master/badge/icon)](https://jenkins.tristan.moe/job/Lyrebird/job/master)
[![Sonar bugs](https://sonar.tristan.moe/api/project_badges/measure?project=moe.lyrebird%3Alyrebird-parent&metric=bugs)](https://sonar.tristan.moe/project/issues?id=moe.lyrebird%3Alyrebird-parent&resolved=false&types=BUG)
[![Lines of code](https://sonar.tristan.moe/api/project_badges/measure?project=moe.lyrebird%3Alyrebird-parent&metric=ncloc)](https://sonar.tristan.moe/component_measures?id=moe.lyrebird%3Alyrebird-parent&metric=ncloc)
[![Known Vulnerabilities](https://snyk.io/test/github/tristan971/lyrebird/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/tristan971/lyrebird?targetFile=pom.xml)
[![OpenHub Stats](https://www.openhub.net/p/Lyrebird-twitter/widgets/project_thin_badge?format=gif)](https://www.openhub.net/p/Lyrebird-twitter/)

[![Quality gate status](https://sonar.tristan.moe/api/project_badges/quality_gate?project=moe.lyrebird%3Alyrebird-parent)](https://sonar.tristan.moe/dashboard?id=moe.lyrebird%3Alyrebird-parent)

## Reasoning :
There are currently no effective Twitter clients on Windows and Linux.
The goal of Lyrebird is to fix this, support macOS, and remain fully open-source.

Hopefully this works out.


## How to get it

### [Precompiled](https://github.com/Tristan971/Lyrebird/releases)
Recommendations :

- Windows & macOS : download according [precompiled release](https://github.com/Tristan971/Lyrebird/releases).

- Debian/Ubuntu (& derivatives that use dpkg/apt) :
Add repository source `deb https://deb.lyrebird.moe any main`, then download signing key and add it to apt keyring
```bash
wget https://deb.lyrebird.moe/lyrebird_repositories.gpg
sudo apt-key add lyrebird_repositories.gpg
rm lyrebird_repositories.gpg
```
Then install latest version with `apt update && apt install lyrebird`.

- RedHat/CentOS/Fedora (& derivatives that use yum/dnf/rpm) :
Add repository source (unsure if gpgcheck=0 is needed as I sign rpm packets, but doesn't hurt)
```
[Lyrebird]
name=Lyrebird releases
baseurl=https://rpm.lyrebird.moe
gpgcheck=0
```
Then update your repositories and install package `lyrebird`.

### Build it yourself

Requirements:
- A Java 10+ development kit with JavaFX components included (i.e. an Oracle JDK or an OpenJDK+OpenJFX combo)
- Apache Maven (works at least with version 3.5.2)
- For native images you need either InnoSetup tools on Windows or ``dpkg-deb``/``rpmutils`` on Linux depending on
what system you want to target. You can only build native images for the current system type.

First (in project folder) : ``$> mvn clean install -DskipTests``

Then any of the following (in `Lyrebird/lyrebird` folder):
- Execute only (no packaging) : ``$> mvn clean spring-boot:run``
- For a native installer (will be in ``Lyrebird/lyrebird/target/jfx/native``) : ``$> mvn jfx:native -DskipTests``
- For a portable JAR file (will be in ``Lyrebird/lyrebird/target``) : ``$> mvn clean package -DskipTests``

## What does it look like?
[![Screenshot of current version](docs/img/screenshot.png)](docs/img/screenshot.png)

## Current state of development:

- [x] Log in 
- [ ] Tweets-based systems
    - [x] Base systems :
        - [x] Timeline
        - [x] Mentions
        - [x] Direct messages
    - [ ] Display features :
        - [x] Basic textual display
        - [x] Display username, @screenname and user profile picture
        - [ ] Display for chosen list
        - [ ] Text highlighting
            - [ ] Mentions
                - [ ] Color
                - [ ] Clickable
            - [ ] Hashtags
                - [ ] Color
                - [ ] Clickable
            - [x] URLs
                - [x] Color
                - [x] Clickable
        - [ ] Media embedding
            - [ ] Officially supported
                - [x] Twitter image embedding
                - [x] Twitter video embedding
                - [x] Twitter gif images embedding
            - [ ] Easy embedding
                - [ ] Direct image embedding
                - [ ] Direct video embedding
            - [ ] Why is it not easy to do ?
                - [ ] Youtube video embedding
    - [x] Data loading features :
        - [x] Seeking older tweets by-demand (scroll or button)
        - [x] Seek newer tweets all the time using streaming API

- [ ] Tweeting
    - [x] Support text
    - [x] Support attachments
    - [ ] Support geolocation
    
- [ ] Interraction with tweet
    - [x] Reply
    - [ ] Quote
    - [x] Like (formerly _favourite_)
    - [x] Retweet

- [x] Interraction with users
    - [x] Follow/unfollow
    - [ ] Block/mute
    - [ ] List management
    - [ ] Report

- [ ] Searches

- [ ] Miscellanelous
    - [x] System notifications
    - [x] Internal notifications
    - [x] Update system
        - [x] Basic update system
        - [x] Auto-update

## Can I contribute?
Please do!

It should be fairly easy to understand the general idea of the back-end model for any
seasoned Java developer with some Spring Framework experience.

For the UI/JavaFX model a lot of the work is delegated to a JavaFX framework called
EasyFXML which you can read about [here](https://github.com/Tristan971/EasyFXML).
