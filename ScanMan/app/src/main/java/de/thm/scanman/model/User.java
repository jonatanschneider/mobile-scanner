package de.thm.scanman.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {
    private String id;
    private String name;
    private String mail;
    private Date createdAt;
    private List<Document> createdDocuments;
    private List<Document> sharedDocuments;

    public User() {
        createdDocuments = new ArrayList<>();
        sharedDocuments = new ArrayList<>();
    }

    public User(String name, String mail, Date createdAt, List<Document> createdDocuments, List<Document> sharedDocuments) {
        this.name = name;
        this.mail = mail;
        this.createdAt = createdAt;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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
        StringBuilder sb = new StringBuilder();
        sb.append("User: ")
                .append(id).append(" - ")
                .append(name).append(" - ")
                .append(mail).append("\n");

        sb.append("Created Documents: \n");
        createdDocuments.forEach(d -> sb.append("\t")
                .append(d.toString()).append("\n"));

        sb.append("Documents shared with User: \n");
        sharedDocuments.forEach(d -> sb.append("\t")
                .append(d.toString()).append("\n"));

        return sb.toString();
    }
}
