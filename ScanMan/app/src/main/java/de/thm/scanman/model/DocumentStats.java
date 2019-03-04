package de.thm.scanman.model;

import java.util.Date;

public class DocumentStats {
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

    @Override
    public String toString() {
        return "Erstellt am: " + new Date(document.getCreatedAt()).toString() +
                "\nZuletzt geändert am: " + new Date(document.getLastUpdateAt()).toString() +
                "\nAnzahl an Nutzern: " + numberOfUsers() +
                "\nAnzahl an Aufnahmen: " + numberOfImages();
    }
}
