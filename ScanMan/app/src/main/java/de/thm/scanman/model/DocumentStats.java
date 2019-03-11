package de.thm.scanman.model;

import org.apache.commons.io.FileUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provide methods to gather statistics about a document
 */
public class DocumentStats {
    private static DateFormat dateFormat = SimpleDateFormat.getDateInstance();
    private Document document;

    public DocumentStats(Document document) {
        this.document = document;
    }

    public Document getDocument() { return document; }

    public int numberOfUsers() {
        return (document.getUserIds() == null) ? 0 : document.getUserIds().size();
    }

    public int numberOfImages() {
        return document.getImages().size();
    }

    /**
     * @return Localized string of creation date
     */
    public String creationDate() {
        return dateFormat.format(new Date(document.getCreatedAt()));
    }

    /**
     * @return Localized string of last update date, if there wasn't an update yet will return "-"
     */
    public String lastUpdateDate() {
        long date = document.getLastUpdateAt();
        return (date != 0) ? dateFormat.format(new Date(date)) : "-";
    }

    /**
     * @return Human readable file size
     */
    public String documentSize() {
        return FileUtils.byteCountToDisplaySize(document.getSize());
    }
}
