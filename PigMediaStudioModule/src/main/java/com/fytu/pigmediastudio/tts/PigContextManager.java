package com.fytu.pigmediastudio.tts;

import android.content.Context;


/**
 * create by FengyiTu at 2019.8.9
 */
public class PigContextManager {

    private Context context;

    public PigContextManager(Context context){
        this.context = context.getApplicationContext();
    }

    public Context getApplicationContext(){
        return context;
    }
}
