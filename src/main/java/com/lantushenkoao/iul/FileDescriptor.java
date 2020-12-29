package com.lantushenkoao.iul;

import java.nio.file.attribute.FileTime;
import java.util.Date;

public class FileDescriptor {
    private String name;
    private long size;
    private FileTime updatedAt;
    private String hash;

    private int itemNo;

    //used to create dummy file
    public FileDescriptor(){
        this.name = "";
        this.hash = "";
    }

    public FileDescriptor(String name, long size, FileTime updatedAt, String hash,  int itemNo) {
        this.name = name;
        this.size = size;
        this.updatedAt = updatedAt;
        this.hash = hash;
        this.itemNo = itemNo;
    }

    public String getHash() {
        return hash;
    }

    public String getName() {
        return name;
    }

    public String getItemNo() {
        return itemNo > 0 ? Integer.toString(itemNo) : "";
    }

    public String getSize() {
        return size > 0 ? Long.toString(size) : "";
    }

    public FileTime getUpdatedAt() {
        return updatedAt;
    }
}