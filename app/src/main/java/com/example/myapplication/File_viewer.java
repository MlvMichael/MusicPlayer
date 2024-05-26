package com.example.myapplication;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.view.View;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;

public class File_viewer {

    private Path currentDir;
    private DirectoryStream<Path> dirStream;
    private ArrayList<Path> filesInCurrDir;

    private MainActivity ma;

    public File_viewer(Context context, MainActivity ma) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.currentDir = Paths.get("/data/data");
        }
        this.ma = ma;
        try {
            this.update();
        } catch (Exception e) {}
    }

    public ArrayList<Path> getFilesInCurrDir() {
        return filesInCurrDir;
    }

    public Path lookAtPlace(Path p) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Files.isDirectory(p)) {
                currentDir = p;
                update();
                this.ma.setDirectoryPath(p);
            } else {
                return p;
            }
        }
        return null;
    }

    public Path getCurrentDir() {
        return currentDir;
    }
    public void previousDir() throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (currentDir.getParent()!=null) {
                currentDir = currentDir.getParent();
            }
        }
        update();
    }

    public void update() throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dirStream = Files.newDirectoryStream(currentDir);
            filesInCurrDir.clear();
            for (Path path : dirStream) {
                filesInCurrDir.add(path);
            }
        }
    }
    
}
