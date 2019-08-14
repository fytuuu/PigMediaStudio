# PigMediaStudio
a tool to better use TextToSpeach and SoundPool


You can download a jar from GitHub's releases page.

Or use Gradle:
```gradle
repositories {
  mavenCentral()
  google()
}

dependencies {
  implementation 'com.github.bumptech.glide:glide:4.9.0'
  annotationProcessor 'com.github.bumptech.glide:compiler:4.9.0'
}
```

## get start to use it
---
### Use ``PigSoundPlayer`` 
init PigSoundPlayer before you play sound. you may call
```java
PigSoundPlayer.initSoundPlayer(1);
``` 
in application.

Before you play sound (resource or local file), load it in to the pool. Call 
```java
int result = PigSoundPlayer.getLoader(getApplicationContext()).load(filePath,1);
```
,the result is id of this file.

then you may call ``int streamID = PigSoundPlayer.play(fileName,1.0f,1.0f,1,0,1.0f)`` to play target sound stream.

also you may pause one of them by calling `` PigSoundPlayer.pause(streamID)``
or pause all of them by calling ``PigSoundPlayer.autoPause()``
and stop one of them by calling ``PigSoundPlayer.stop(streamID)``


