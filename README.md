# PigMediaStudio
a tool to better use TextToSpeach and SoundPool


You can download a jar from GitHub's releases page.

Or use Gradle:
```gradle
repositories {
  mavenCentral()
  google()
  
  maven { url 'https://jitpack.io' }
}

dependencies {
 implementation 'com.github.fytuuu:PigMediaStudio:v1.0'
}
```

## get start to use it
---
### Use ``PigSoundPlayer`` 
init PigSoundPlayer before you play sound. you may call ``initSoundPlayer()`` in application.
```java
PigSoundPlayer.initSoundPlayer(1);
``` 


Before you play sound (resource or local file), load it in to the pool. The result is id of this file.
```java
int result = PigSoundPlayer.getLoader(getApplicationContext()).load(filePath,1);
```


then you may call ``int streamID = PigSoundPlayer.play(fileName,1.0f,1.0f,1,0,1.0f)`` to play target sound stream.

also you may pause one of them by calling 
```java
PigSoundPlayer.pause(streamID)
```

or pause all of them by calling 
```java
PigSoundPlayer.autoPause()
```

stop one of them by calling 
```java
PigSoundPlayer.stop(streamID)
```

### Use ``PigSpeechManager``
#### read a text
you may load it to the ``PigReader`` ,it will read the text after read all texts before it in the queue.
```java
PigSpeechManager.in(getApplicationContext()).getReader().setSpeed(3.0f).setPitch(0.5f).loadReadTTS(text);
```

or read a text now. In this case, the text that is read will be flushed and abandoned.After read the ``readNow(text)``, subsequent text int the queue will be read.
```java
PigSpeechManager.in(getApplicationContext()).getReader().readNow(str);
```

#### transform text to .wav file
before transforming, you'd better check the ``<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>`` permission
```java
PigSpeechManager.in(getApplicationContext()).getTranslater().setWavSpeed(1.0f).put(fileName,text);
```

you may set a ``translateProgressListener`` to see witch file has been created
```java
PigSpeechManager.in(context).getTranslater().setParentPath("test").setOnTranslatProgressListener(new PigTranslater.OnTranslateProgressListener(){
  @Override
  public void onStart(){}
  
  @Override
  public void onComplete(String text,String fileAbsolutePath){
    
  }
  
  @Override
  public void onError(String error){
  }

}
```
