package de.thm.scanman.model;

public class Image {
    private String file;
    private String timestamp;

    public Image() { }

    public Image(String file, String timestamp) {
        this.file = file;
        this.timestamp = timestamp;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return file + ", Created At: " + timestamp;
    }
}