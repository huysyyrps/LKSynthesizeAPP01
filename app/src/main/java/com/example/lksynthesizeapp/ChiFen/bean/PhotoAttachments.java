package com.example.lksynthesizeapp.ChiFen.bean;

import java.io.Serializable;

public class PhotoAttachments implements Serializable {

    private String name;
    private String fileName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
