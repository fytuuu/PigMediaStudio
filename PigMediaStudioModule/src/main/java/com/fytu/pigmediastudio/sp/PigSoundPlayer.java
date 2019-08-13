package com.fytu.pigmediastudio.sp;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.FileDescriptor;
import java.util.HashMap;

public class PigSoundPlayer {
    private final static String TAG = "PigSoundPlayer";

    //用于存储资源文件
    private static HashMap<String, Integer> resourceMap = new HashMap<>();

    //音效池
    private static SoundPool soundPool;

    //单例
    private static PigSoundPlayer pigSoundPlayer;

    //预存（预加载)者
    private static Loader loader;

    //上下文管理者
    private static ContextManager contextManager;

    //构造器私有
    private PigSoundPlayer(){}

    //初始化
    public static void initSoundPlayer(int maxStreams, AudioAttributes attributes, int quality){

        if (pigSoundPlayer == null) pigSoundPlayer = new PigSoundPlayer();

        if (maxStreams<=0) maxStreams = 1;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder spb  = new SoundPool.Builder();
            spb.setMaxStreams(maxStreams);
            if (attributes!=null )spb.setAudioAttributes(attributes);
            soundPool = spb.build();
        }else{
            soundPool = new SoundPool(maxStreams, AudioManager.STREAM_SYSTEM,quality);
        }
    }

    public static void initSoundPlayer(int maxStreams, int quality){
        initSoundPlayer(maxStreams,null,quality);
    }

    public static void initSoundPlayer(int maxStreams, AudioAttributes attributes){
        initSoundPlayer(maxStreams,attributes,5);
    }

    public static void initSoundPlayer(int maxStreams){
       initSoundPlayer(maxStreams,null);
    }

    public static void initSoundPlayer(){
        initSoundPlayer(1);
    }

    //获取对应处理者
    public static Loader getLoader(Context context){
        if(contextManager == null){
            contextManager = new ContextManager(context);
        }
        if (loader == null){
            loader = new Loader();
        }
        return loader;
    }

    public static int play(int soundId,int loop){
        return play(soundId,1.0f,1.0f,1,loop,1.0f);
    }

    public static int play(int soundId,int loop,float rate){
        return play(soundId,1.0f,1.0f,1,loop,rate);
    }

    public static int play(int soundId,float leftVolume,float rightVolume,int priority,int loop,float rate){
        if (pigSoundPlayer == null){
            return 0;
        }
        return soundPool.play(soundId, leftVolume, rightVolume, priority, loop, rate);
    }

    public static int play(String tag,int loop){
        return play(tag,1.0f,1.0f,1,loop,1.0f);
    }

    public static int play(String tag,int loop,float rate){
        return play(tag,1.0f,1.0f,1,loop,rate);
    }

    public static int play(String tag,float leftVolumn,float rightVolume,int priority,int loop,float rate){
        if (pigSoundPlayer == null){
            return 0;
        }
        int resid = 0;
        if(resourceMap.get(tag)!=null){
            resid = resourceMap.get(tag);
            return soundPool.play(resid,leftVolumn,rightVolume,priority,loop,rate);
        }else{
            return 0;
        }
    }


    //暂停
    public static void pause(int streamID){
        soundPool.pause(streamID);
    }
    //继续播放
    public static void resume(int streamId){
        soundPool.resume(streamId);
    }
    //全部暂停播放
    public static void autoPause(){
        soundPool.autoPause();
    }
    //全部继续播放
    public static void autoResume(){
        soundPool.autoResume();
    }
    //停止
    public static void stop(int streamID){
        soundPool.stop(streamID);
    }
    //设置音量
    public static void setVolume(int streamID, int leftVolume, int rightVolume){
        soundPool.setVolume(streamID,leftVolume,rightVolume);
    }
    //设置Priority
    public static void setPriority(int streamID, int priority){
        soundPool.setPriority(streamID,priority);
    }
    //设置Rate
    public static void setRate(int streamID, float rate){
        soundPool.setRate(streamID,rate);
    }
    //设置loop
    public static void setLoop(int streamID,int loop){
        soundPool.setLoop(streamID,loop);
    }


    public static class Loader{
        private Loader(){}

        public int load(String tag,int resId,int priority){
            int id = -1;
            if (pigSoundPlayer != null){
                id = soundPool.load(contextManager.getApplicationContext(),
                        resId,
                        priority);
                Log.d(TAG,"loadMusic result"+id);
                resourceMap.put(tag,id);
            }
            return id;
        }

        public int load(String tag,String path,int priority){
            int id = -1;
            if (pigSoundPlayer != null){
                id = soundPool.load(path,priority);
                Log.d(TAG,"loadMusic result"+id);
                resourceMap.put(tag,id);
            }
            return id;
        }

        public int load(String tag,FileDescriptor fd,long offset,long length,int priority){
            int id = -1;
            if (pigSoundPlayer != null){
                id = soundPool.load(fd,offset,length,priority);
                resourceMap.put(tag,id);
            }
            Log.d(TAG,"loadMusic result"+id);
            return id;
        }

        public int load(String tag, AssetFileDescriptor afd,int priority){
            int id = -1;
            if (pigSoundPlayer != null){
                id = soundPool.load(afd,priority);
                resourceMap.put(tag,id);
            }
            Log.d(TAG,"loadMusic result"+id);
            return id;
        }

    }



}
