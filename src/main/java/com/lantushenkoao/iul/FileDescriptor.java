package com.lantushenkoao.iul;

import java.nio.file.attribute.FileTime;
import java.util.Date;

public class FileDescriptor {
    private String name;
    private long size;
    private FileTime updatedAt;
    private String hash;


    public FileDescriptor(String name, long size, FileTime updatedAt, String hash) {
        this.name = name;
        this.size = size;
        this.updatedAt = updatedAt;
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public FileTime getUpdatedAt() {
        return updatedAt;
    }
}