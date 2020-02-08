package com.shwe.item;


import java.io.Serializable;

public class ItemDown implements Serializable {
    private String name;
    private String duration;
    private String size;
    private String filepath;
    private String thumbnailpath;

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getThumbnailpath() {
        return thumbnailpath;
    }

    public void setThumbnailpath(String thumbnailpath) {
        this.thumbnailpath = thumbnailpath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "ItemDown{" +
                "name='" + name + '\'' +
                ", duration='" + duration + '\'' +
                ", size='" + size + '\'' +
                ", filepath='" + filepath + '\'' +
                ", thumbnailpath='" + thumbnailpath + '\'' +
                '}';
    }
}