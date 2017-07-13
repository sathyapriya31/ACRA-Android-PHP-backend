# ACRA-Android-PHP-backend
An ACRA backend that allows you to view and save to an SQL database on your Android device!

This app is designed to gather the crash logs you receive from the server and save them into a local SQL database. The server component is bundled in /server/, and the app code is stored here. The server component is not run on an Android device. It is run on a server(with PHP).

# How it works(after setup)

Your app crashed. ACRA is installed and forwards the error to the backend script. This script writes the error as a text file. (the report script is based on [this gist](https://gist.github.com/KevinGaudin/5560305)) (If running,) the app's background service detects that there is a new error in the folder. It then sends you a push notification telling you "your app crashed". When you then open the app, the errors will be indexed, sliced, protected from SQL injection and stored in the SQLite database the app has created. After refreshing the window automatically, you will be able to see all the errors, both newly reported and those who have been there a while.

The stacktrace is picked out carefully, by detecting E/ACRA logtag. That tag is present in the file, because ACRA detected a crash and sends it to the server. After the stacktrace has been discovered, it uses MD5 hashing to give the stacktrace a code. After the hash is created, it determines whether or not the error exists already. If it exists, it updates the previous entry with any new devices, android versions and updates the date and amount of times reported. 

If you have resolved all the errors, or just feel like having a database reset, press the icon in the top right corner and press "wipe database". It deletes all the contents of the table, and resets the ID's auto increment value. 

You can also delete single issues, by tapping the issue you want to delete and press "delete". Due to circumstances, the entry itself will not disappear from the list instantly, and if you are on a tablet and see two pane mode, the error details will still be visible(until you press on a different issue or on the "home"-button). 

These fields have custom spaces in the report section:

* ReportField.APP_VERSION_NAME
* ReportField.PACKAGE_NAME
* ReportField.ANDROID_VERSION
* ReportField.PHONE_MODEL
* ReportField.LOGCAT

Other fields reported are gathered under "other information" (see update 1.1)

## Update 1.1

Any fields are now supported in input. The fields mentioned above have their own fields, while other fields have a collected field name called `other information`. If you use any other fields than those mentioned above, the `other information` field is where those fields are stored.

## Update 1.2

(Still in progress):

* Remove fragment/activity when issue is deleted (as per #2)
* Check password to make sure it is correct on setup (as per #1)
* Other fixes

# Worth noting

* Because the same issues are detected using a hashed code, no different issues can have the same error. A single number in difference will make the hash different. This means you do not have to worry about two different issues having the same hash

# Setup

Please refer to [the wiki](https://github.com/GamersCave/ACRA-Android-PHP-backend/wiki/Setup)

# Screenshots


<img src="https://raw.githubusercontent.com/GamersCave/ACRA-Android-PHP-backend/master/img/device-2017-04-12-175310.png">
The home screen
<hr>

<img src="https://raw.githubusercontent.com/GamersCave/ACRA-Android-PHP-backend/master/img/device-2017-04-12-175108.png">
An issue as seen from the app
<hr>

<img src="https://raw.githubusercontent.com/GamersCave/ACRA-Android-PHP-backend/master/img/device-2017-04-12-175219.png">
Configuration screen(as of v 1.0)
<hr>

<img src="https://raw.githubusercontent.com/GamersCave/ACRA-Android-PHP-backend/master/img/device-2017-04-12-175422.png">
The persistent notification shown by the background service
