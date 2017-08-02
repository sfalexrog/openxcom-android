# OpenXcom for Android

OpenXcom for Android is, as the name suggests, a port of [OpenXcom](http://openxcom.org) to the
Android platform. In order to achieve this, it uses SDL2 set of libraries, and a portion of Java
code for some convenient features like auto-updating files after installing new version.

Uses [afiledialog](https://github.com/jfmdev/afiledialog) library.

# Building [![Build Status](https://travis-ci.org/sfalexrog/openxcom-android.svg?branch=master)](https://travis-ci.org/sfalexrog/openxcom-android)

In order to build OpenXcom for Android, you'll need:

 - A current Android SDK, and
 - A current Android NDK (tested with r15c).

Additionally, you'll need Java development kit and Java runtime environment version 1.8
(OpenJDK 8 seems to be preferred, although you could probably get by with Oracle JDK 8), which is required for building Android applications.

Ideally, you should install Android Studio and try building some basic projects. If everything works,
then it should work just fine with this project.

OpenXcom uses [transifex](https://www.transifex.com) for translations, so you might want to have
its tx command-line tool for translation updates.

## Getting the Code

This project uses git submodules, so in order to get the code, you'll have to do the following:

1. Clone this project:


    $ git clone https://github.com/sfalexrog/openxcom-android.git
    $ cd openxcom-android

or

    $ mkdir oxc-android
    $ cd oxc-android
    $ git init
    $ git remote add upstream https://github.com/sfalexrog/openxcom-android.git
    $ git checkout master

2. Get submodules


    $ git submodule init
    $ git sibmodule update

3. Since this project uses Android NDK (currently built with r15c), you'll need to provide path
to it. Additionally, you'll have to provide path to Android SDK as well. These paths should be in
the local.properties file in the project root. The file should contain the following lines:


    sdk.dir=/path/to/Android/sdk
    ndk.dir=/path/to/Android/ndk

with your own actual paths substituted instead of these placeholders.

Note that gradle will attempt to install all dependencies (except for ndk for some reason) if you're missing some of them.
You'll need to have licenses for them, however. In order to get those licenses, run `${ANDROID_SDK}/tools/bin/sdkmanager --licenses`
and accept at least the `android-sdk-license`.

# Building the app

At this point you may just run the Gradle wrapper with the `assemble` task:


    $ ./gradlew assemble

The resulting .apk will be in `app/build/outputs/apk` folder.

You might also include some additional data in the apk itself. Everything you put in `app/src/main
/jni/OpenXcom/bin` subdirectories will be packed as assets, so you might include some mods that will be
automatically installed or even the game data that won't require to go through the data copying
process. Note that redistribution of such builds might be illegal in some countries, and as
such they should be only used for debugging purposes.

## Translations

If you have a working, properly configured Transifex client, you might want to download the latest
translations. From `app/src/main/jni/OpenXcom`, run


    $ tx pull
    $ cd bin
    $ ./translations_merge.sh

and rebuild the project.

# Mod compatibility

Since most of the code was not touched in SDL1.2 to SDL2 transition, most mods should work out-of-box.
However, that's not the case with binary mods (i.e. mods that provide a separate executable) or
mods that require a custom version of the executable. Fortunately, such mods are usually open-source
and should merge with little to no conflicts.

In order to apply your modifications to the source code, you should do the following (I'll use
OpenXcom Extended as an example):

1. Go to `app/src/main/jni/OpenXcom`

2. Add your mod repository as a remote:


    $ git remote add extended https://github.com/Yankes/OpenXcom.git

3. Merge the code (preferably in a separate branch):


    $ git fetch extended
    $ git merge extended/OpenXcomExtended

4. Resolve merge conflicts (there shouldn't be many, and they shouldn't be major)

5. Build and test your mod!

# Caveats

This project uses Google's modified cmake to build native code. It's installed automatically through gradle
(if you have the `android-sdk-license`, at least), but it might not work on some distributions due to its reliance
on a particular version of OpenSSL. Seems to work just fine on Ubuntu, required a custom build of OpenSSL on Fedora. YMMV.

I'm no expert in Gradle build system, so most of the code is not very good. Anyway, it seems to work,
and I'm not touching it anytime soon.

This project is basically my "hello world", and it started at the time when I didn't really know what
Java was. Expect bad code and inefficient solutions. Better yet, send me a P/R to fix something :-)

