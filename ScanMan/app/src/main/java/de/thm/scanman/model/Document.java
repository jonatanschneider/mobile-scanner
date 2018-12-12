package de.thm.scanman.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Document {
    private String id;
    private String name;
    private List<String> tags;
    private List<Image> images;
    private Date createdAt;
    private User owner;
    private List<User> users;

    public Document() {
        users = new ArrayList<>();
        tags = new ArrayList<>();
    }

    public Document(String name, List<String> tags, List<Image> images, Date createdAt, User owner, List<User> users) {
        this.name = name;
        this.tags = tags;
        this.images = images;
        this.createdAt = createdAt;
        this.owner = owner;
        this.users = users;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Document: ")
                .append(id).append(" - ")
                .append(name).append(" - ")
                .append("Owner: ").append(owner).append(" - ")
                .append("Created At: ").append(createdAt.toString()).append("\n");

        sb.append("Images: \n");
        images.forEach(i -> sb.append("\t")
                .append(i.toString()).append("\n"));

        sb.append("Shared With: \n");
        users.forEach(u -> sb.append("\t")
                .append(u.toString()).append("\n"));

        sb.append("Tags: \n");
        tags.forEach(t -> sb.append(t).append(","));

        return sb.toString();
    }

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

}
