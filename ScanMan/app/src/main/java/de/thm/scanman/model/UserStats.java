package de.thm.scanman.model;

import org.apache.commons.io.FileUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserStats {

    private User user;

    public UserStats(User user) {
        this.user = user;
    }

    public String createdDocumentsFileSize() {
        return FileUtils.byteCountToDisplaySize(documentsFileSize(user.getCreatedDocuments()));
    }

    public String sharedDocumentsFileSize() {
        return FileUtils.byteCountToDisplaySize(documentsFileSize(user.getSharedDocuments()));
    }

    public String allDocumentsFileSize() {
        return FileUtils.byteCountToDisplaySize(documentsFileSize(user.getCreatedDocuments()) + documentsFileSize(user.getSharedDocuments()));
    }

    public String documentsSharedWithOthersFileSize() {
        List<Document> documents = user.getCreatedDocuments().stream()
                .filter(doc -> doc.getUserIds() != null)
                .filter(doc -> doc.getUserIds().size() > 0)
                .collect(Collectors.toList());
        return FileUtils.byteCountToDisplaySize(documentsFileSize(documents));
    }

    private long documentsFileSize(List<Document> list) {
        return list.stream()
                .mapToLong(Document::getSize)
                .sum();
    }

    public int countOfCreatedDocuments() {
        return user.getCreatedDocuments().size();
    }

    public int countOfSharedDocuments() {
        return user.getSharedDocuments().size();
    }

    public int countOfAllDocuments() {
        return countOfCreatedDocuments() + countOfSharedDocuments();
    }

    public int countOfDocumentsSharedWithOthers() {
        return (int) user.getCreatedDocuments().stream()
                .filter(doc -> doc.getUserIds() != null)
                .filter(doc -> doc.getUserIds().size() > 0)
                .count();
    }

    @Override
    public String toString() {
        return "Anzahl erstellter Dokumente: " + countOfCreatedDocuments() + " insgesamt " + createdDocumentsFileSize() + " Bytes" +
                "\nAnzahl geteilter Dokumente: " + countOfDocumentsSharedWithOthers() + " insgesamt " + documentsSharedWithOthersFileSize() + " Bytes" +
                "\nAnzahl mit mir geteilter Dokumente: " + countOfSharedDocuments() + " insgesamt " + sharedDocumentsFileSize() + " Bytes" +
                "\nGesamtzahl an Dokumenten: " + (user.getCreatedDocuments().size() + user.getSharedDocuments().size()) + " insgesamt " + (createdDocumentsFileSize() + sharedDocumentsFileSize()) + " Bytes";

    }
}
