package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
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
    ArrayList<String> listviewContent;
    ArrayList<String> filesInCurrDir;

    ArrayList<Path> fullPathsOfFilesInCurrDir;
    ArrayList<String> filesInPlaylist;

    ArrayList<String> fullPathsOfFilesInPlaylist;

    MPlayer mplayer;

    File_viewer fileviewer;

    ImageButton PPButton;

    Button fileViewerButton;

    ImageButton beginStopButton;
    ImageButton loopingButton;

    boolean isPaused;

    boolean isLooped;

    boolean isStopped;

    boolean showsMusic;

    TextView pathFileView;

    TextView choosenFileView;

    String playingFile;

    String directoryPath;

    int choosenFileIndex;

    public void updateView() {
        if (showsMusic) {
            this.pathFileView.setText(this.playingFile);
            this.listviewContent = filesInPlaylist;
        } else {
            try {
                this.pathFileView.setText(this.directoryPath);
                listviewContent = filesInCurrDir;
            } catch (Exception e) {}
        }
        this.listViewAdapter.notifyDataSetChanged();
    }

    public void setPlayingFile(String s) {
        this.playingFile = s;
        if (showsMusic) {
            this.pathFileView.setText(s);
        }
    }

    public void setDirectoryPath(Path p) {
        this.directoryPath = p.toString();
        if (!showsMusic) {
            this.pathFileView.setText(directoryPath);
        }
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
        fullPathsOfFilesInPlaylist = new ArrayList<String>();
        fullPathsOfFilesInCurrDir = new ArrayList<Path>();
        isPaused = true;
        isStopped = true;
        showsMusic = true;
        isLooped = true;
        choosenFileIndex = -1;
        mplayer = new MPlayer(this, this);
        mplayer.setFiles(filesInPlaylist);
        fileviewer = new File_viewer(this, this);
        fullPathsOfFilesInCurrDir = fileviewer.getFilesInCurrDir();
        fullPathsOfFilesInPlaylist = mplayer.getFilesInPlaylist();
        listviewContent = new ArrayList<String>();
        //Объявление и настройка виджетов
        listViewAdapter = new ArrayAdapter<String>(this, R.id.listview, listviewContent);
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!showsMusic) {
                    Path p = null;
                    try {
                        p = MainActivity.this.fileviewer.lookAtPlace(MainActivity.this.fullPathsOfFilesInCurrDir.get(position));
                    } catch (Exception e) {}
                    if (p!=null) {
                        MainActivity.this.choosenFileIndex=position;
                        MainActivity.this.choosenFileView.setText(p.toString());
                    } else {
                        MainActivity.this.choosenFileIndex=-1;
                        MainActivity.this.choosenFileView.setText("здесь будет отображаться выбранный файл");
                    }
                } else {
                    MainActivity.this.choosenFileIndex=position;
                    MainActivity.this.choosenFileView.setText(MainActivity.this.listviewContent.get(position));
                }
            }
        });
        ImageButton previousFileButton = (ImageButton) findViewById(R.id.previousFileButton);
        previousFileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.mplayer.previousFile();
                MainActivity.this.setPlayingFile(MainActivity.this.mplayer.getCurrentFile());
            }
        });
        PPButton = (ImageButton) findViewById(R.id.PPButton);
        PPButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (MainActivity.this.isPaused) {
                    MainActivity.this.mplayer.continuePlaying();
                    MainActivity.this.PPButton.setImageResource(R.drawable.pause);
                } else {
                    MainActivity.this.mplayer.pausePlaying();
                    MainActivity.this.PPButton.setImageResource(R.drawable.play);
                }
                MainActivity.this.isPaused=!MainActivity.this.isPaused;
            }
        });
        ImageButton nextFileButton = (ImageButton) findViewById(R.id.nextFileButton);
        nextFileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.mplayer.nextFile();
                MainActivity.this.setPlayingFile(MainActivity.this.mplayer.getCurrentFile());
                MainActivity.this.updateView();
            }
        });
        loopingButton = (ImageButton) findViewById(R.id.loopingButton);
        loopingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.mplayer.changeLoopingState();
                if (MainActivity.this.isLooped) {
                    MainActivity.this.loopingButton.setImageResource(R.drawable.notlooped);
                } else {
                    MainActivity.this.loopingButton.setImageResource(R.drawable.looped);
                }
                MainActivity.this.isLooped=!MainActivity.this.isLooped;
                MainActivity.this.mplayer.changeLoopingState();
            }
        });
        beginStopButton = (ImageButton) findViewById(R.id.beginStopButton);
        beginStopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (MainActivity.this.isStopped) {
                    try {
                        MainActivity.this.mplayer.beginPlaying();
                        MainActivity.this.beginStopButton.setImageResource(R.drawable.stop);
                    } catch (Exception e) {}
                } else {
                    MainActivity.this.mplayer.stopPlaying();
                    MainActivity.this.beginStopButton.setImageResource(R.drawable.begin);
                }
                MainActivity.this.isStopped=!MainActivity.this.isStopped;
            }
        });
        ImageButton addFileButton = (ImageButton) findViewById(R.id.addButton);
        addFileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (showsMusic) {
                    try {
                        MainActivity.this.mplayer.addFile(MainActivity.this.fullPathsOfFilesInPlaylist.get(MainActivity.this.choosenFileIndex));
                    } catch (Exception e) {}
                } else {
                    try {
                        MainActivity.this.mplayer.addFile(MainActivity.this.fullPathsOfFilesInCurrDir.get(MainActivity.this.choosenFileIndex).toString());
                    } catch (Exception e) {}
                }
            }
        });
        ImageButton removeFileButton = (ImageButton) findViewById(R.id.removeButton);
        removeFileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (showsMusic) {
                    try {
                        MainActivity.this.mplayer.removeFile(MainActivity.this.fullPathsOfFilesInPlaylist.get(MainActivity.this.choosenFileIndex));
                    } catch (Exception e) {}
                } else {
                    try {
                        MainActivity.this.mplayer.removeFile(MainActivity.this.fullPathsOfFilesInCurrDir.get(MainActivity.this.choosenFileIndex).toString());
                    } catch (Exception e) {}
                }
            }
        });
        ImageButton previousDirectoryButton = (ImageButton) findViewById(R.id.previousDirectoryButton);
        previousDirectoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    MainActivity.this.fileviewer.previousDir();
                } catch (Exception e) {}
            }
        });
        pathFileView = (TextView) findViewById(R.id.pathfileView);
        choosenFileView = (TextView) findViewById(R.id.choosenFileView);
        fileViewerButton = (Button) findViewById(R.id.fileViewerButton);
        fileViewerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String[] sa;
                if (showsMusic) {
                    try {
                        MainActivity.this.fileviewer.update();
                        MainActivity.this.fullPathsOfFilesInCurrDir = MainActivity.this.fileviewer.getFilesInCurrDir();
                        MainActivity.this.directoryPath = MainActivity.this.fileviewer.getCurrentDir().toString();
                        for (int i = 0; i<MainActivity.this.fullPathsOfFilesInCurrDir.size(); i++) {
                            sa = MainActivity.this.fullPathsOfFilesInCurrDir.get(i).toString().split("/");
                            MainActivity.this.filesInCurrDir.set(i, sa[sa.length-1]);
                        }
                        MainActivity.this.showsMusic=!MainActivity.this.showsMusic;
                        MainActivity.this.fileViewerButton.setText("Просмотреть плейлист");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    MainActivity.this.fileViewerButton.setText("Просмотреть файлы");
                    MainActivity.this.fullPathsOfFilesInPlaylist = MainActivity.this.mplayer.getFilesInPlaylist();
                    MainActivity.this.playingFile = MainActivity.this.mplayer.getCurrentFile();
                    for (int i = 0; i<MainActivity.this.fullPathsOfFilesInPlaylist.size(); i++) {
                        sa = MainActivity.this.fullPathsOfFilesInPlaylist.get(i).split("/");
                        MainActivity.this.filesInCurrDir.set(i, sa[sa.length-1]);
                    }
                    MainActivity.this.showsMusic=!MainActivity.this.showsMusic;
                }
                MainActivity.this.updateView();
            }
        });
    }
}