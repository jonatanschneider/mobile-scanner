package de.thm.scanman.model;

import org.apache.commons.io.FileUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Provide methods to gather statistics about an user and it's documents
 */
public class UserStats {

    private User user;

    public UserStats(User user) {
        this.user = user;
    }

    /**
     * @return Human readable file size of all documents created by the user
     */
    public String createdDocumentsFileSize() {
        return FileUtils.byteCountToDisplaySize(documentsFileSize(user.getCreatedDocuments()));
    }

    /**
     * @return Human readable file size of all documents shared with this user
     */
    public String sharedDocumentsFileSize() {
        return FileUtils.byteCountToDisplaySize(documentsFileSize(user.getSharedDocuments()));
    }

    /**
     * @return Human readable file size for all documents this user has access to
     */
    public String allDocumentsFileSize() {
        return FileUtils.byteCountToDisplaySize(documentsFileSize(user.getCreatedDocuments()) + documentsFileSize(user.getSharedDocuments()));
    }

    /**
     * @return Human readable file size of all documents the user shared with other users
     */
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
}
