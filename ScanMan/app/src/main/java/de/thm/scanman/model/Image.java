package de.thm.scanman.model;

public class Image {
    private String id;
    private String file;
    private String createdAt;

    public Image() { }

    public Image(String file, String createdAt) {
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "File: " + id + " - " + file + " - " + "Created At: " + createdAt;
    }
}