# Birthday Reminder 3.0
Birthday reminder is a simple Material Design Android app which notifies users of upcoming birthdays. Released in 2015, it has had many design and feature update since then, many coming from user requests. Recent additions include Firebase auth and realtime database for data backup. As I built this when I was quite inexperienced, it doesn't follow any of the common Android architecture patterns, so I wouldn't recommend using this structure elsewhere.

![](http://i.imgur.com/zcF2X4Z.png)![](http://i.imgur.com/PaiXGEV.png)
![](http://i.imgur.com/VXJWF8g.png)![](http://i.imgur.com/yC20tDa.png)


[<img src="http://i.imgur.com/aL8bBy5.png?1">](https://play.google.com/store/apps/details?id=website.julianrosser.birthdays)

### Update 3.0: ###

- Added Google sign in & migrated to Firebase database
- Added Thai language support


### Update 2.4: ###

- Added ability to import contacts from device if birthday information is available
- Added privacy policy page


# Building the project #

In order to build the project you will need to follow the steps below: 

First, ensure that you have the following minimum requirements:
```
Android Studio 3.1.1
Android SDK Platform 23
Gradle 4.4
Gradle plugin 3.1.1
```

Once this is resolved, you should see the following error: 
```
File google-services.json is missing. The Google Services Plugin cannot function without it. 
```
This is because the app depends on Firebase for auhentication and data persistance. You will need to set up your own Firebase account and project here: https://firebase.google.com/

Once you have an app environemt set up, you should be prompted to download a google-services.json file (you can also find it later on the project settings page). Drop this file under the 'app' module and you should now be able to build the app to a device. 

One futher note, you may need to enable Google auth on the firebase console.

