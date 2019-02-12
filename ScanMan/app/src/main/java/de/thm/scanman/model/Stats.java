package de.thm.scanman.model;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public long documentsSharedWithOthersFileSize() {
        List<Document> documents = user.getCreatedDocuments().stream()
                .filter(doc -> doc.getUserIds() != null)
                .filter(doc -> doc.getUserIds().size() > 0)
                .collect(Collectors.toList());
        return documentsFileSize(documents);
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
                .filter(doc -> doc.getUserIds() != null)
                .filter(doc -> doc.getUserIds().size() > 0)
                .count();
    }
}
