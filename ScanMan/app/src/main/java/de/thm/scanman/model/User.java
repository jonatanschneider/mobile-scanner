package de.thm.scanman.model;

import java.util.List;

public class User {
    private String id;
    private String name;
    private String mail;
    private List<Document> createdDocuments;
    private List<Document> sharedDocuments;

    public User() {}

    public User(String name, String mail, List<Document> createdDocuments, List<Document> sharedDocuments) {
        this.name = name;
        this.mail = mail;
        this.createdDocuments = createdDocuments;
        this.sharedDocuments = sharedDocuments;
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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public List<Document> getCreatedDocuments() {
        return createdDocuments;
    }

    public void setCreatedDocuments(List<Document> createdDocuments) {
        this.createdDocuments = createdDocuments;
    }

    public List<Document> getSharedDocuments() {
        return sharedDocuments;
    }

    public void setSharedDocuments(List<Document> sharedDocuments) {
        this.sharedDocuments = sharedDocuments;
    }

    @Override
    public String toString() {
        return name + " - " + mail;
    }
}
