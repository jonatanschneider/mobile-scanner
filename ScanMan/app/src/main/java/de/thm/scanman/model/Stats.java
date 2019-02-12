package de.thm.scanman.model;

import java.util.List;
import java.util.Objects;

public class Stats {

    private User user;


    public Stats(User user) {
        this.user = user;
    }

    public long createdDocumentsFileSize() {
        return documentsFileSize(user.getCreatedDocuments());
    }

    public long sharedDocumentsFileSize() {
        return documentsFileSize(user.getSharedDocuments());
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

    public int countOfDocumentsSharedWithOthers() {
        return (int) user.getCreatedDocuments().stream()
                .filter(Objects::nonNull)
                .filter(doc -> doc.getUserIds().size() > 0)
                .count();
    }
}
