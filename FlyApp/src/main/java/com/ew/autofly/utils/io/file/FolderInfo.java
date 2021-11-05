package com.ew.autofly.utils.io.file;

import java.util.ArrayList;
import java.util.List;


public class FolderInfo {
    private String name;
    private String path;
    private List<FileInfo> images = new ArrayList<FileInfo>();

    public List<FileInfo> getImages() {
        return images;
    }

    public void setImages(List<FileInfo> images) {
        this.images = images;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
