[![Build Status](https://travis-ci.org/phantompunk/vivify.svg?branch=master)](https://travis-ci.org/phantompunk/vivify)
# Vivify (Beta)
Vivify is a beautiful ad-free and open source Android Alarm Clock under GPLv3 license.Vivify is currently available in a beta release in the Google Play Store(App Store release coming soon).
<img style="margin-left:10px;" src="/Screenshots/vivifyFeature.png" >


Wake up your way with Vivify, by using Spotify's Android SDK we are able to give premium users access to Spotify entire music catalog. The goal is to allow users to take control of their morning be it waking up happy to Bob Marley or jumping out of bed to heavy rock.

<a href='https://play.google.com/store/apps/details?id=com.rva.mrb.vivify&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' width="35%" src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a>

## Screenshots
<img style="margin-left:10px;" src="/Screenshots/Screenshot_splash.png" width="30%" >
<img style="margin-left:10px;" src="/Screenshots/Screenshot_alarms.png" width="30%" >
<img style="margin-left:10px;" src="/Screenshots/Screenshot_details.png" width="30%" >
<img style="margin-left:10px;" src="/Screenshots/Screenshot_search.png" width="30%" >
<img style="margin-left:10px;" src="/Screenshots/Screenshot_wake.png" width="30%" >

## Development
1. Clone the project to your folder
```bash
$ mkdir yourAndroidProjects
$ cd yourAndroidProjects
$ git clone https://github.com/lemma-io/vivify.git
```

2. Create or download the *gradle.properties* file
You can get this file from a Lemma member or create your own. This should live in your Vivify root folder and should look like this:
```
android.useDeprecatedNdk=true
SPOTIFY_CLIENT_ID="yourSpotifyKey"
BUGFENDER_APP_KEY="yourBugFenderKey"
RELEASE_STORE_PASS=""
RELEASE_KEY_ALIAS=""
RELEASE_KEY_PASS=""
PATH_TO_CERT_FILE=""
```

3. If using your own keys make sure to register your app with the Spotify Developer Console and create a BugFender account. You will need to change the package name to com.YOUR.PACKAGE.NAME

4. Update the Spotify Developer Console with your SHA-1 Key
Before building your app locally make sure to generate a SHA-1 key this is required since Spotify needs to know your machine is an authenticated device.

#### Creating a Fingerprint
To generate a fingerprint on OS X or Linux run this in your terminal:
```bash
keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore -list -v | grep SHA1
```
On Windows run:
```bash
keytool -exportcert -alias androiddebugkey -keystore %HOMEPATH%\.android\debug.keystore -list -v | grep SHA1
```

The result should look something like this:

`SHA1: E7:47:B5:45:71:A9:B4:47:EA:AD:21:D7:7C:A2:8D:B4:89:1C:BF:75`

Share this info with a Lemma member or update your Spotify Console with your new fingerprint

## Open-Source Libraries
* [Retrofit 2](https://square.github.io/retrofit/)
* [Dagger 2](https://google.github.io/dagger/)
* [Butterknife](https://jakewharton.github.io/butterknife/)
* [Realm](https://github.com/realm/realm-java)
* [RxJava](https://github.com/ReactiveX/RxJava)
* [Glide](https://github.com/bumptech/glide)
* [Parceler](https://github.com/johncarl81/parceler)

## Contributing
Pull requests are welcome.

### Licensing
Vivify is made available under the terms of the GPLv3.

See the LICENSE file that accompanies this distribution for the full text of the license.
