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
init PigSoundPlayer before you play sound. you may call ``PigSoundPlayer.initSoundPlayer(1);`` in application.
Before you play sound (resource or local file), load it in to the pool.
call ``int result = PigSoundPlayer.getLoader(getApplicationContext()).load(filePath,1);``,the result is id of this file.
then you may call ``int streamID = PigSoundPlayer.play(fileName,1.0f,1.0f,1,0,1.0f)`` to play target sound stream.


