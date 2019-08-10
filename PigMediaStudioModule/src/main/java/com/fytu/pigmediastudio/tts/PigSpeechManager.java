package com.fytu.pigmediastudio.tts;

import android.content.Context;

import java.util.Locale;

public class PigSpeechManager {

    private static PigSpeechManager instance;
//    private static  List<TextToSpeech> textToSpeeches = new ArrayList<>();
    private static PigTTSController ttsController;
    //private int playMode;//我们只需要flush add只要在监听后再读就好
    private static ContextManager contextManager;

    private PigSpeechManager(){
        //初始化
    }

    /**
     * 为tts准备好applicationContext
     * @param context
     * @return
     */
    public static PigSpeechManager in(Context context){
        if (instance == null){
            instance = new PigSpeechManager();
        }
        if (contextManager == null || context != contextManager.getApplicationContext() || contextManager.getApplicationContext() == null) {
            contextManager = new ContextManager(context);
        }
        return instance;
    }


    public PigTTSController getReader(){//提前准备好
        if (ttsController == null){
            ttsController =  new PigTTSController(contextManager.getApplicationContext());
        }
        ttsController.initRead();
        return ttsController;
    }



}
