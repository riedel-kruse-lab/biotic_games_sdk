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
Detailed documentation is provided along with the full source code in this GitHub repository.

## Features
### Bluetooth
The Biotic Games SDK provides developers with the ability to connect to an Arduino broadcasting joystick inputs. This allows the the developer to respond to joystick events on the Android device (e.g. one of the lights has turned on or the joystick has been pushed down).

In order to use this functionality, the Android app must request Bluetooth permissions by adding the following two lines as a sibling to the `<application>` tag in `AndroidManifest.xml`:
```
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />

<application>
    ...
</application>
```

The developer must then call the function `startBluetooth(BluetoothThreadListener)` in `onCreate()` or before the game starts. In order to respond to the events received, write a class that implements the `BluetoothThreadListener` interface.

## Examples
A limited set of examples of SDK usage has been made available in the `examples/` folder in this repository. There are also a few code snippets and documentation links provided in this document for common game tasks.

### Changing Camera Parameters
Sometimes it is useful to change certain parameters of the camera for example the zoom level and autofocus settings. Check out the [`Camera.Parameters`](http://developer.android.com/reference/android/hardware/Camera.Parameters.html) documentation for more information about what parameters can be changed. The Biotic Games SDK supplies a `CameraView` class which simplifies this process. To change the camera parameters, call `getCameraView()` in `initGame()`, then call `getCameraParameters()` on the `CameraView`, and set values on the `Camera.Parameters` object that is returned before setting the `CameraView`'s new parameters with `setCameraParameters`. This looks like the following, which sets the zoom level of the camera to half of its maximum:

```
@Override
protected void initGame(final int width, final int height) {
    CameraView cameraView = getCameraView();
    Camera.Parameters params = cameraView.getCameraParameters();
    params.setZoom(params.getMaxZoom() / 2);
    cameraView.setCameraParameters(params);
    ...
}
```

### Sound Effects
Sound effects are supported using the Android API rather than the Biotic Games SDK. To add sound effects to your game, create a new `raw` resource folder (i.e. `res/raw`) and place your sound effect files in this folder. Then use the [SoundPool](http://developer.android.com/reference/android/media/SoundPool.html) object to load the sound effects from the resources and to play them.
