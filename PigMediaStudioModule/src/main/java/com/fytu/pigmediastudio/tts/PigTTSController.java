package com.fytu.pigmediastudio.tts;

import android.content.Context;


/**
 * create by FengyiTu at 2019.8.9
 */
public class PigTTSController {

    private PigTTSController instance;
    private static final String TAG = "PigTTSController";

    private Context applicationContext;

    private PigReader pigReader;
    private PigTranslater pigTranslater;


    //该wav文件是否用于朗读（更快速度）
    private boolean isNeedFile = false;

    public PigTTSController(Context context) {
        this.applicationContext = context;
        instance = this;
    }


    public PigReader getReader(){
        if (null == pigReader){
            pigReader = new PigReader(applicationContext);
        }
        return  pigReader;
    }

    public PigTranslater getTranslater(){
        if (null == pigTranslater){
            pigTranslater = new PigTranslater(applicationContext);
        }
        return pigTranslater;
    }










}
