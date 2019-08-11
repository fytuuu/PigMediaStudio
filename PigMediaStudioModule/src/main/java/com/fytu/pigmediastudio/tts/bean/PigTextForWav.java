package com.fytu.pigmediastudio.tts.bean;

/**
 * create by FengyiTu at 2019.8.10
 */

public class PigTextForWav {
    String fileName;
    String text;

    public PigTextForWav(String fileName, String text) {
        this.fileName = fileName;
        this.text = text;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
