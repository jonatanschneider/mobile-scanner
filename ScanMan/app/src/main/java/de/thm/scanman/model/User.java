package de.thm.scanman.model;

import java.util.List;

public class User {
    private String name;
    private String mail;
    private List<Document> documents;
    private List<Document> sharedDocuments;

    public User() {}

    public User(String name, String mail, List<Document> documents) {
        this.name = name;
        this.mail = mail;
        this.documents = documents;
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

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
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
