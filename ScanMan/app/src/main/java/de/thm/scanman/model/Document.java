package de.thm.scanman.model;

import java.util.List;

public class Document {
    private String name;
    private List<String> tags;
    private List<Image> images;
    private String createdAt;
    private User owner;
    private List<User> users;

    public Document() {

    }

    public Document(String name, List<String> tags, List<Image> images, String createdAt, User owner, List<User> users) {
        this.name = name;
        this.tags = tags;
        this.images = images;
        this.createdAt = createdAt;
        this.owner = owner;
        this.users = users;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
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
}
