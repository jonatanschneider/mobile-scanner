package de.thm.scanman.model;

import org.apache.commons.io.FileUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Document {
    private String id;
    private String name;
    private List<String> tags;
    private List<Image> images;
    private long createdAt;
    private long lastUpdateAt;
    private String ownerId;
    private List<String> userIds;

    public Document() {
        tags = new ArrayList<>();
        images = new ArrayList<>();
        userIds = new ArrayList<>();
    }

    public Document(String name, List<String> tags, List<Image> images, long createdAt, long lastUpdateAt, String ownerId, List<String> userIds) {
        this.name = name;
        this.tags = tags;
        this.images = images;
        this.createdAt = createdAt;
        this.lastUpdateAt = lastUpdateAt;
        this.ownerId = ownerId;
        this.userIds = userIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getSize() {
        return FileUtils.byteCountToDisplaySize(images.stream()
                .mapToLong(Image::getFileSize)
                .sum());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Document: ")
                .append(id).append(" - ")
                .append(name).append(" - ")
                .append("Owner: ").append(ownerId).append(" - ")
                .append("Created At: ").append(new Date(createdAt).toString()).append("\n");

        sb.append("Images: \n");
        images.forEach(i -> sb.append("\t")
                .append(i.toString()).append("\n"));

        sb.append("Shared With: \n");
        userIds.forEach(u -> sb.append("\t")
                .append(u.toString()).append("\n"));

        sb.append("Tags: \n");
        tags.forEach(t -> sb.append(t).append(","));

        return sb.toString();
    }

    public long getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(long lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public static class Image {
        private String id;
        private String file;
        private long createdAt;
        private long lastUpdateAt;
        private long fileSize;

        public Image() { }

        public Image(String file, long createdAt) {
            this.file = file;
            this.createdAt = createdAt;
            this.lastUpdateAt = createdAt;
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

        public long getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(long createdAt) {
            this.createdAt = createdAt;
        }

        public long getLastUpdateAt() {
            return lastUpdateAt;
        }

        public void setLastUpdateAt(long lastUpdateAt) {
            this.lastUpdateAt = lastUpdateAt;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }

        @Override
        public String toString() {
            return "Image{" +
                    "id='" + id + '\'' +
                    ", file='" + file + '\'' +
                    ", createdAt=" + createdAt +
                    ", lastUpdateAt=" + lastUpdateAt +
                    ", fileSize=" + fileSize +
                    '}';
        }
    }

}
