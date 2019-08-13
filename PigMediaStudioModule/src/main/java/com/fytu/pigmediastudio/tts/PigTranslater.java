package com.fytu.pigmediastudio.tts;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.fytu.pigmediastudio.tts.bean.PigTextForWav;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * create by FengyiTu at 2019.8.10
 */

public class PigTranslater{

    private static final String TAG = "PigTTSController";
    private static final String TTS_TTW_FLAG = "TextToWaveUtterance";

    private static final String PERMISSION_ERROR = "write permission is denied";

    private PigTranslater pigTranslater;

    private Context applicationContext;

    //下载中的tts，最大只能是复用池中的数量，防止创建太多tts对象！
    private HashMap<String, TextToSpeech> ttsMap = new HashMap<>();
    //复用池
    private List<String> freePool = new ArrayList<>();

    //转换wav文件队列
    private PigBlockQueue<PigTextForWav> pigFileBlockQueue = new PigBlockQueue<>();//无限长;

    //复用池大小
    private int freePoolMaxSize = 3;

    private TextToSpeech ttsForWav;
    private boolean isTTWbegin = false;

    private boolean isTtsProduceSuccess = true;


    private boolean isValid2 = true;

    private Thread thread_ttw;

    private String parentPath;
    private boolean isDenied = true;

    public interface OnTranslateProgressListener {
        void onStart();
        void onComplete(String text,String fileAbsolutePath);
        void onError(String error);
    }

    private OnTranslateProgressListener onTranslateProgressListener;

    public PigTranslater setOnTranslateProgressListener(OnTranslateProgressListener onTranslateProgressListener) {
        this.onTranslateProgressListener = onTranslateProgressListener;
        return pigTranslater;
    }

    public PigTranslater (Context context) {
        this.applicationContext = context;
        pigTranslater = this;
        initTextToWav();
    }


    public PigTranslater setMaxPoolSize(int maxPoolSize){
        if (maxPoolSize < 1) maxPoolSize = 1;
        this.freePoolMaxSize = maxPoolSize;
        return pigTranslater;
    }

    //判断是否有权限
    private boolean isWriteDenied(){
        int permission = ContextCompat.checkSelfPermission(applicationContext,
                "android.permission.WRITE_EXTERNAL_STORAGE");

        if (permission != PackageManager.PERMISSION_GRANTED) {
            if (onTranslateProgressListener!=null){
                onTranslateProgressListener.onError(PERMISSION_ERROR);
            }
            isDenied = true;
        }else{
            isDenied = false;
        }

        return isDenied;
    }


    //未来可支持多层文件夹（其实就是连续从头创建到尾）
    //设置文件路径,判断是否加上斜线
    public PigTranslater setParentPath(String parentFileDir) {
        if (isWriteDenied()){
            Log.w(TTS_TTW_FLAG,"write permission denied");
        }else {
            if (!parentFileDir.endsWith(File.separator)) {
                parentFileDir = parentFileDir + File.separator;
            }
            if (!parentFileDir.startsWith(File.separator)) {
                parentFileDir = File.separator + parentFileDir;
            }

            File dir;
            if (!(dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + parentFileDir)).exists()) {
                boolean isCreated = dir.mkdir();
                if (!isCreated) {
                    Log.d(TTS_TTW_FLAG, "文件夹创建失败");
                } else {
                    Log.d(TTS_TTW_FLAG, "文件夹创建成功");
                    //写在判断里面，如果创建，那么修改
                    parentPath = parentFileDir;
                }
            }
        }
        return pigTranslater;
    }

    private void initTextToWav(){
        if (isWriteDenied()){
            Log.w(TTS_TTW_FLAG,"write permission denied");
        }else {
            if (!isTTWbegin) {
                isTTWbegin = true;

                thread_ttw = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (isValid2) {
                            if (freePool.size() > 0) {
                                //先判断是否有可用的
                                TextToSpeech ttsForWav = ttsMap.get(freePool.get(0));
                                if (ttsForWav == null) {

                                } else {
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, freePool.get(0));
                                    try {
                                        final PigTextForWav pigTTW = pigFileBlockQueue.take();
                                        if (wavSpeed != 0f)ttsForWav.setSpeechRate(wavSpeed);
                                        if (wavPitch!= 0f)ttsForWav.setPitch(wavPitch);
                                        if (loc != Locale.CHINA)ttsForWav.setLanguage(loc);
                                        ttsForWav.synthesizeToFile(pigTTW.getText(),
                                                map,
                                                Environment.getExternalStorageDirectory().getAbsolutePath() + parentPath + pigTTW.getFileName());

                                        ttsForWav.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                                            @Override
                                            public void onStart(String utteranceId) {
                                                Log.d(TTS_TTW_FLAG,"a ttsForWav started with utterance Id"+utteranceId);
                                                if (null != onTranslateProgressListener){
                                                    onTranslateProgressListener.onStart();
                                                }
                                            }

                                            @Override
                                            public void onDone(String utteranceId) {
                                                Log.d(TTS_TTW_FLAG,"a ttsForWav onDone with utterance Id"+utteranceId);
                                                onTtsForWavFree(utteranceId);

                                                if (null != onTranslateProgressListener){
                                                    onTranslateProgressListener.onComplete(pigTTW.getText(),Environment.getExternalStorageDirectory().getAbsolutePath()+parentPath+pigTTW.getFileName());
                                                }
                                            }

                                            @Override
                                            public void onError(String utteranceId) {

                                            }

                                            @Override
                                            public void onError(String utteranceId,int errorCode) {
                                                Log.d(TTS_TTW_FLAG,"a ttsForWav onError with utterance Id"+utteranceId+" and errorcode is"+errorCode);
                                                onError(utteranceId);
                                            }
                                        });


                                        freePool.remove(0);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                }
                            } else if (ttsMap.size() < freePoolMaxSize) {
                                //如果没有可用的，已存在的tts又不足3个，直接创建一个新的tts
                                if (isTtsProduceSuccess) {
                                    try {
                                        startTextToWave(pigFileBlockQueue.take());
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                //有3个都在工作中，等待它们空闲
                            }
                        }

                    }
                });
                thread_ttw.start();
            }
        }
    }


    /**
     * 添加任务
     */

    public PigTranslater putTFW(final String fileName,final String textForWav){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (fileName.endsWith(".wav")){
                        pigFileBlockQueue.put(new PigTextForWav(fileName+".wav",textForWav));
                    }else{
                        pigFileBlockQueue.put(new PigTextForWav(fileName+".wav",textForWav));
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.setPriority(5);
        t.start();
        return pigTranslater;
    }



    private void startTextToWave(final PigTextForWav pigTTW){
        isTtsProduceSuccess = false;
        ttsForWav = new TextToSpeech(applicationContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == 0){
                    Log.d(TAG,"tts init Success");
                }else{
                    Log.d(TAG,"tts init Failed");
                }
                HashMap<String, String> map = new HashMap<>();
                String utteranceId = "UtteranceId"+System.currentTimeMillis();
                map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
                if (wavSpeed != 0f)ttsForWav.setSpeechRate(wavSpeed);
                if (wavPitch!= 0f)ttsForWav.setPitch(wavPitch);
                if (loc != Locale.CHINA)ttsForWav.setLanguage(loc);
                ttsForWav.synthesizeToFile(pigTTW.getText(),
                        map,
                        Environment.getExternalStorageDirectory().getAbsolutePath()+parentPath+pigTTW.getFileName());
                ttsMap.put(utteranceId,ttsForWav);
                ttsForWav.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        Log.d(TTS_TTW_FLAG,"a ttsForWav started with utterance Id"+utteranceId);
                        if (null != onTranslateProgressListener){
                            onTranslateProgressListener.onStart();
                        }
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        Log.d(TTS_TTW_FLAG,"a ttsForWav onDone with utterance Id"+utteranceId);
                        onTtsForWavFree(utteranceId);

                        if (null != onTranslateProgressListener){
                            onTranslateProgressListener.onComplete(pigTTW.getText(),Environment.getExternalStorageDirectory().getAbsolutePath()+parentPath+pigTTW.getFileName());
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {

                    }

                    @Override
                    public void onError(String utteranceId,int errorCode) {
                        Log.d(TTS_TTW_FLAG,"a ttsForWav onError with utterance Id"+utteranceId+" and errorcode is"+errorCode);
                        onError(utteranceId);
                    }
                });
                isTtsProduceSuccess = true;
            }
        });
    }

    private void onTtsForWavFree(String freeUtteranceId){
        //如果复用池不够，就加进去
        if (freePool.size()< freePoolMaxSize && ttsMap.get(freeUtteranceId)!= null){
            freePool.add(freeUtteranceId);
        }
    }

    /*   wav速度 */
    private float wavSpeed;
    /*   wav音高 ,默认1,0f */
    private float wavPitch;
    /*   wav语言 ,默认汉语 */
    private Locale loc = Locale.CHINA;

    /**
     * 设置wav速度，设置后，只能改变pool中的值
     */
    public PigTranslater setWavSpeed(float wavSpeed){
        this.wavSpeed = wavSpeed;
        return pigTranslater;
    }

    /**
     * 设置wav音高
     */
    public PigTranslater setWavPitch(float wavPitch){
        this.wavPitch = wavPitch;
        return pigTranslater;
    }

    /**
     * 设置wav语言
     */
    public PigTranslater setWavLanguage(Locale loc){
        this.loc = loc;
        return pigTranslater;
    }
}

