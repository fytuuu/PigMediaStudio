package com.fytu.pigmediastudio.tts;

import android.content.Context;


/**
 * create by FengyiTu at 2019.8.9
 */
public class PigSpeechManager {

    private static PigSpeechManager instance;
    private static PigTTSController pigTTSController;
    private static PigContextManager pigContextManager;

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
        if (pigContextManager == null || context != pigContextManager.getApplicationContext() || pigContextManager.getApplicationContext() == null) {
            pigContextManager = new PigContextManager(context);
        }
        return instance;
    }


    public PigReader getReader(){//提前准备好
        if (pigTTSController == null){
            pigTTSController =  new PigTTSController(pigContextManager.getApplicationContext());
        }
        return pigTTSController.getReader();
    }


    public PigTranslater getTranslater(){
        if (pigTTSController == null){
            pigTTSController =  new PigTTSController(pigContextManager.getApplicationContext());
        }
        return pigTTSController.getTranslater();
    }



}
