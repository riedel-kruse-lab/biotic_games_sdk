# How to use the SDK
## Android Studio
1. Create a new Android Studio project.
2. Include the Biotic Games SDK by going to File > New Module and selecting "Import .JAR or .AAR Package" from the list. Then hit "Next."
3. For the "File name" navigate to where `bioticgamessdk.aar` has been downloaded onto your computer. For the "Subjproject name," we recommend putting "sdk."
4. In the `build.gradle` file for your application's module (by default, the "app" module), add the line `compile project(':sdk')` under `dependencies`.
5. Include the OpenCV library.
5. Create a new class that extends the `BioticGameActivity` class.
