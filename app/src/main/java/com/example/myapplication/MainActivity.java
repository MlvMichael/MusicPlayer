package com.example.myapplication;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ArrayAdapter<String> listViewAdapter;

    ListView listView;
    String[] listviewContent;
    ArrayList<String> filesInCurrDir;
    ArrayList<String> filesInPlaylist;

    MPlayer mplayer;

    ImageButton PPButton;

    Button fileViewerButton;

    ImageButton StopButton;
    ImageButton loopingButton;

    TextView totalTime;

    TextView currentTime;

    SeekBar playProgressBar;

    boolean isPaused;

    boolean isLooped;

    boolean showsMusic;

    String playingFile;

    int choosenFileIndex;

    public String[] ArraylistToArray(ArrayList<String> ar) {
        String[] a = new String[ar.size()];
        for (int i = 0; i<ar.size(); i++) {
            a[i]=ar.get(i);
        }
        return a;
    }

    public void updateView() {
        if (showsMusic) {
            listviewContent = ArraylistToArray(filesInPlaylist);
        } else {
            listviewContent = ArraylistToArray(filesInCurrDir);
        }
        listViewAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listviewContent);
        listView.setAdapter(listViewAdapter);
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //My code
        //Объявление переменных
        filesInPlaylist = new ArrayList<String>();
        filesInCurrDir = new ArrayList<String>();
        isPaused = false;
        showsMusic = true;
        isLooped = true;
        choosenFileIndex = 0;
        mplayer = new MPlayer(this, this);
        mplayer.setFiles(filesInPlaylist);
        playingFile = mplayer.getCurrentFile();
        filesInPlaylist = mplayer.getFilesInPlaylist();
        listviewContent = new String[0];
        //Создание массива с именами файлов
        filesInCurrDir = new ArrayList<String>();
        filesInCurrDir.add("Idle browsing");
        filesInCurrDir.add("Alderheim");
        //Объявление и настройка виджетов

        listViewAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listviewContent);
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(listViewAdapter);
        int i = listView.getChildCount();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.this.choosenFileIndex=position;
            }
        });

        ImageButton previousFileButton = (ImageButton) findViewById(R.id.previousFileButton);
        previousFileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.mplayer.previousFile();
                try {
                    MainActivity.this.mplayer.stopPlaying();
                } catch (Exception e) {}
                try {
                    MainActivity.this.mplayer.beginPlaying();
                    MainActivity.this.isPaused=false;
                    MainActivity.this.PPButton.setImageResource(R.drawable.pause);
                } catch (Exception e) {}
            }
        });
        PPButton = (ImageButton) findViewById(R.id.PPButton);
        PPButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!MainActivity.this.mplayer.isprepared) {
                    try {
                        MainActivity.this.mplayer.setCurrentIndex(choosenFileIndex);
                        MainActivity.this.mplayer.beginPlaying();
                        MainActivity.this.isPaused = false;
                        MainActivity.this.PPButton.setImageResource(R.drawable.pause);
                    } catch (Exception e) {}
                } else {
                    if (MainActivity.this.isPaused) {
                        MainActivity.this.mplayer.continuePlaying();
                        MainActivity.this.PPButton.setImageResource(R.drawable.pause);
                    } else {
                        MainActivity.this.mplayer.pausePlaying();
                        MainActivity.this.PPButton.setImageResource(R.drawable.play);
                    }
                    MainActivity.this.isPaused = !MainActivity.this.isPaused;
                }
            }
        });
        ImageButton nextFileButton = (ImageButton) findViewById(R.id.nextFileButton);
        nextFileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.mplayer.nextFile();
                try {
                    MainActivity.this.mplayer.stopPlaying();
                } catch (Exception e) {}
                try {
                    MainActivity.this.mplayer.beginPlaying();
                    MainActivity.this.isPaused=false;
                    MainActivity.this.PPButton.setImageResource(R.drawable.pause);
                } catch (Exception e) {}
            }
        });
        loopingButton = (ImageButton) findViewById(R.id.loopingButton);
        loopingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (MainActivity.this.isLooped) {
                    MainActivity.this.loopingButton.setImageResource(R.drawable.notlooped);
                } else {
                    MainActivity.this.loopingButton.setImageResource(R.drawable.looped);
                }
                MainActivity.this.isLooped=!MainActivity.this.isLooped;
                MainActivity.this.mplayer.changeLoopingState();
            }
        });
        StopButton = (ImageButton) findViewById(R.id.beginStopButton);
        StopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    MainActivity.this.mplayer.stopPlaying();
                    MainActivity.this.PPButton.setImageResource(R.drawable.play);
                    MainActivity.this.isPaused=false;
                    MainActivity.this.playProgressBar.setProgress(0);
                    MainActivity.this.currentTime.setText("0");
                } catch (Exception e) {}
            }
        });
        ImageButton addFileButton = (ImageButton) findViewById(R.id.addButton);
        addFileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (showsMusic) {
                        MainActivity.this.mplayer.addFile(MainActivity.this.filesInPlaylist.get(MainActivity.this.choosenFileIndex));
                    } else {
                        MainActivity.this.mplayer.addFile(MainActivity.this.filesInCurrDir.get(MainActivity.this.choosenFileIndex));
                    }
                } catch (Exception e) {}
                MainActivity.this.updateView();
            }
        });
        ImageButton removeFileButton = (ImageButton) findViewById(R.id.removeButton);
        removeFileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (showsMusic) {
                        MainActivity.this.mplayer.removeFile(MainActivity.this.filesInPlaylist.get(MainActivity.this.choosenFileIndex));
                    } else {
                        MainActivity.this.mplayer.removeFile(MainActivity.this.filesInCurrDir.get(MainActivity.this.choosenFileIndex));
                    }
                }catch (Exception e) {}
                MainActivity.this.updateView();
            }
        });
        fileViewerButton = (Button) findViewById(R.id.fileViewerButton);
        fileViewerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (showsMusic) {
                    MainActivity.this.fileViewerButton.setText("Просмотреть плейлист");
                } else {
                    MainActivity.this.fileViewerButton.setText("Просмотреть файлы");
                }
                MainActivity.this.showsMusic=!MainActivity.this.showsMusic;
                MainActivity.this.updateView();
            }
        });
        currentTime = (TextView) findViewById(R.id.currentTime);
        totalTime = (TextView) findViewById(R.id.totalTime);
        playProgressBar = findViewById(R.id.playProgressBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            playProgressBar.setMin(0);
        }
        playProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    try {
                        MainActivity.this.mplayer.goTo(progress);
                    } catch (Exception e) {
                        MainActivity.this.playProgressBar.setProgress(0);
                        MainActivity.this.currentTime.setText("0");
                    }
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });



        //Цикл для обновления текущего времени, время sleep-а может быть любым, но я считаю что лучшим будет 500 милисекунд
        timeSetter ts = new timeSetter(this);
        ts.start();
    }
}


class timeSetter extends Thread {

    private MainActivity ma;

    public void run() {
        while (true) {
            if (ma.mplayer.isprepared) {
                try {
                    ma.currentTime.setText(Integer.toString(ma.mplayer.getCurrentTime()));
                    ma.playProgressBar.setProgress(ma.mplayer.getCurrentTime());
                    Thread.sleep(500);
                } catch (Exception e) {}
            } else {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {}
            }
        }
    }
    public timeSetter(MainActivity ma) {
        this.ma = ma;
    }
}