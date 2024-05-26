package com.example.myapplication;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.PowerManager;

import java.io.IOException;
import java.util.ArrayList;

class OCL implements MediaPlayer.OnCompletionListener {

    private MPlayer Mpl;

    public OCL(MPlayer mpl) {
        Mpl = mpl;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (this.Mpl.nextFile()) {
            try {
                this.Mpl.beginPlaying();
                this.Mpl.ma.setPlayingFile(this.Mpl.getCurrentFile());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

public class MPlayer {

    private Context context;

    public MainActivity ma;

    private MediaPlayer mp;
    private boolean isLooped;
    private ArrayList<String> files;
    private String currentFile;
    private int currentIndex;
    private OCL ocl;


    public MPlayer(Context context, MainActivity ma) {
        this.isLooped = true;
        this.mp = new MediaPlayer();
        this.ocl = new OCL(this);
        this.context = context;
        this.ma = ma;
    }

    public void stopPlaying() {
        mp.stop();
    }

    public String getCurrentFile() {return this.currentFile;}

    public void pausePlaying() {
        mp.pause();
    }

    public void setCurrentIndex(int index) {
        this.currentIndex = index;
    }

    public void changeLoopingState() {
        this.isLooped = !this.isLooped;
    }

    public boolean getLoopingState() {
        return this.isLooped;
    }

    public void addFile(String file) {
        this.files.add(file);
    }

    public void setFiles(ArrayList<String> files) {
        this.files = files;
    }

    public void removeFile(String file) {
        this.files.remove(file);
    }

    public void continuePlaying() {
        mp.start();
    }

    public ArrayList<String> getFilesInPlaylist() {
        return files;
    }

    public boolean nextFile() {
        try {
            this.currentIndex++;
            if (this.currentIndex >= this.files.size()) {
                this.currentIndex = 0;
                this.updateDataSource();
                if (this.isLooped) {
                    return true;
                } else {
                    return false;
                }
            }
            this.updateDataSource();
            return true;
        } catch (Exception e) {}
        return false;
    }

    public void previousFile() {
        try {
            this.currentIndex--;
            if (this.currentIndex < 0) {
                this.currentIndex = this.files.size() - 1;
                this.updateDataSource();
            }
        } catch (Exception e) {}
    }

    public void updateDataSource() {
        this.currentFile=this.files.get(this.currentIndex);
    }

    public void updateDataSource(String file) {
        this.currentFile=file;
    }

    public void beginPlaying() throws Exception {
        mp.setDataSource(currentFile);
        mp.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        mp.start();
        mp.setOnCompletionListener(this.ocl);
    }
}
