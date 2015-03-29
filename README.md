# How to use the SDK
## Setup
1. If you do not already have it, download and install [Android Studio](http://developer.android.com/sdk/index.html). This will also install the Android SDK.
2. If you do not already have it, download and install the [Android NDK](https://developer.android.com/tools/sdk/ndk/index.html).
2. Create a new Android Studio project for your game.
3. Download the latest version of the [OpenCV4Android SDK](http://docs.opencv.org/doc/tutorials/introduction/android_binary_package/O4A_SDK.html). Unzip the OpenCV4Android SDK to a location of your choice.
4. Include the OpenCV library in your Android Studio project, as it is a dependency of the Biotic Games SDK.
    1. In Android Studio, hit File > Import Module.
    2. In the prompt box, choose `OpenCV-android-sdk/sdk/java` as the source directory. Name the module "opencv." Hit "Next."
    3. Keep all of the default import options checked on the next screen and hit "Finish."
    4. In the `build.gradle` file of your application's module (by default, the "app" module), add the line `compile project(':opencv')` under `dependencies`. Our `dependencies` section looks like this after the change:
    ```
    dependencies {
        compile fileTree(dir: 'libs', include: ['*.jar'])
        compile 'com.android.support:appcompat-v7:22.0.0'
        compile project(':opencv')
    }
    ```
5. Grab the latest version of the Biotic Games SDK (`bioticgamessdk.aar`) from the [releases](https://github.com/riedel-kruse-lab/biotic_games_sdk/releases) section of this GitHub repository.
6. Include the Biotic Games SDK in your Android Studio project.
    1. In Android Studio, hit File > New Module (not Import Module like last time).
    2. In the prompt box, select "Import JAR or AAR Package" then hit "Next."
    3. In the next screen, select the `bioticgamessdk.aar` file in the first box. Type "sdk" as the subproject name in the second box. Hit "Finish."
    4. In the `build.gradle` file of your application's module (by default, the "app" module), add the line `compile project(':sdk')` under `dependencies`. Our `dependencies` section looks like this after the change:
    ```
    dependencies {
        compile fileTree(dir: 'libs', include: ['*.jar'])
        compile 'com.android.support:appcompat-v7:22.0.0'
        compile project(':opencv')
        compile project(':sdk')
    }
    ```
8. Add this line to the `local.properties` file in your Android Studio project: `ndk.dir=/Users/dchiu/Developer/android/android-ndk-r10b`. Make sure to replace the path with the path to the NDK on your own machine.
7. Create a new class that extends `BioticGameActivity`.

## Documentation

## Examples
A limited set of examples of SDK usage has been made available in the `examples/` folder in this repository.
