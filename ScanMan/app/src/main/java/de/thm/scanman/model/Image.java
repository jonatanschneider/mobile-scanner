package de.thm.scanman.model;

import java.util.Date;

public class Image {
    private String id;
    private String file;
    private Date createdAt;

    public Image() { }

    public Image(String file, Date createdAt) {
        this.file = file;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "File: " + id + " - " + file + " - " + "Created At: " + createdAt.toString();
    }
}
