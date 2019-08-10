package com.fytu.pigmediastudio.tts;

import android.content.Context;

public class ContextManager {

    private Context context;

    public ContextManager(Context context){
        this.context = context.getApplicationContext();
    }

    public Context getApplicationContext(){
        return context;
    }
}
