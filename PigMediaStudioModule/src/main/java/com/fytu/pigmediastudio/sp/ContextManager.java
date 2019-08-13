package com.fytu.pigmediastudio.sp;

import android.content.Context;

public class ContextManager {
    private Context context;
    private PigSoundPlayer pigSoundPlayer;

    public ContextManager(Context context){
        this.context = context.getApplicationContext();
    }

    public Context getApplicationContext(){
        return context;
    }

    public void setContext(Context context){
        this.context  = context;
    }
}
