package com.example.myapplication;

import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class OCL implements MediaPlayer.OnCompletionListener {

    private MPlayer Mpl;

    public OCL(MPlayer mpl) {
        Mpl = mpl;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        TextView tv = (TextView)Mpl.ma.listView.getChildAt(Mpl.currentIndex);
        tv.setTypeface(Typeface.DEFAULT);
        mp.release();
        this.Mpl.nextFile();
        if (this.Mpl.currentIndex==0 && this.Mpl.isLooped) {
            try {
                this.Mpl.beginPlaying();
            } catch (Exception e) {}
        } else if(this.Mpl.currentIndex==0) {
            this.Mpl.isprepared = false;
            this.Mpl.ma.PPButton.setImageResource(R.drawable.play);
            this.Mpl.ma.isPaused=false;
        }
    }
}

public class MPlayer {

    private Context context;

    public MainActivity ma;

    private MediaPlayer mp;
    public boolean isLooped;
    private ArrayList<String> files;
    private String currentFile;
    public int currentIndex;
    private OCL ocl;

    boolean isprepared;

    public Map<String, Integer> m;

    public MPlayer(Context context, MainActivity ma) {
        this.isLooped = true;
        this.mp = new MediaPlayer();
        this.ocl = new OCL(this);
        this.context = context;
        this.ma = ma;
        this.currentIndex=0;
        isprepared = false;
        m = new HashMap<String, Integer>()
        {
            {
                put("Idle browsing", R.raw.idlebrowsing);
                put("Alderheim", R.raw.aldrheim);
            }
        };
    }

    public void stopPlaying() {
        TextView tv = (TextView)ma.listView.getChildAt(currentIndex);
        tv.setTypeface(Typeface.DEFAULT);
        mp.stop();
        mp.release();
        mp = null;
        isprepared = false;
    }

    public String getCurrentFile() {return this.currentFile;}

    public void pausePlaying() {
        mp.pause();
    }

    public void setCurrentIndex(int index) {
        this.currentIndex = index;
        updateDataSource();
    }

    public void changeLoopingState() {
        this.isLooped = !this.isLooped;
    }

    public int getCurrentTime() {return mp.getCurrentPosition()/1000;}

    public void goTo(int pos) {
        mp.seekTo(pos*1000);
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

    public void nextFile() {
        try {
            TextView tv = (TextView)ma.listView.getChildAt(currentIndex);
            tv.setTypeface(Typeface.DEFAULT);
            this.currentIndex++;
            if (this.currentIndex >= this.files.size()) {
                this.currentIndex = 0;
            }
            this.updateDataSource();
        } catch (Exception e) {}
    }

    public void previousFile() {
        try {
            TextView tv = (TextView)ma.listView.getChildAt(currentIndex);
            tv.setTypeface(Typeface.DEFAULT);
            this.currentIndex--;
            if (this.currentIndex < 0) {
                this.currentIndex = this.files.size() - 1;
            }
            this.updateDataSource();
        } catch (Exception e) {}
    }

    public void updateDataSource() {
        this.currentFile=this.files.get(this.currentIndex);
    }

    public void beginPlaying() throws Exception {
        TextView tv = (TextView)ma.listView.getChildAt(currentIndex);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        mp = MediaPlayer.create(context, m.get(currentFile));
        ma.totalTime.setText(Integer.toString(mp.getDuration()/1000));
        ma.playProgressBar.setMax(mp.getDuration()/1000);
        mp.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        mp.start();
        mp.setOnCompletionListener(this.ocl);
        isprepared = true;
    }
}
