package de.thm.scanman.util;

import java.util.Comparator;
import java.util.Date;

import de.thm.scanman.model.Document;

public class DocumentComparators {
    public static Comparator<Document> alphabetically = (o1, o2) -> {
        if (o1 == o2) return 0;
        if (o1 == null || o1.getName() == null) return 1;
        if (o2 == null || o2.getName() == null) return -1;
        return o1.getName().compareTo(o2.getName());
    };

    public static Comparator<Document> bySize = (o1, o2) -> {
        if (o1 == o2) return 0;
        if (o1 == null || o1.getName() == null) return 1;
        if (o2 == null || o2.getName() == null) return -1;
        return o1.getName().length() - o2.getName().length();
    };

    public static Comparator<Document> byCreateDate = (o1, o2) -> {
        if (o1 == o2) return 0;
        if (o1 == null || o1.getCreatedAt() == 0) return 1;
        if (o2 == null || o2.getCreatedAt() == 0) return -1;
        return new Date(o1.getCreatedAt()).compareTo(new Date(o2.getCreatedAt()));
    };

    public static Comparator<Document> byLastUpdate = (o1, o2) -> {
        if (o1 == o2) return 0;
        if (o1 == null || o1.getLastUpdateAt() == 0) return 1;
        if (o2 == null || o2.getLastUpdateAt() == 0) return -1;
        return new Date(o1.getLastUpdateAt()).compareTo(new Date(o2.getLastUpdateAt()));
    };

    public static Comparator<Document> byOwner = (o1, o2) -> {
        if (o1 == o2) return 0;
        if (o1 == null || o1.getOwnerId() == null) return 1;
        if (o2 == null || o2.getOwnerId() == null) return -1;
        return o1.getOwnerId().compareTo(o2.getOwnerId());
    };

    public static Comparator<Document> descendingAlphabetically = (o1, o2) -> {
        if (o1 == o2) return 0;
        if (o1 == null || o1.getName() == null) return -1;
        if (o2 == null || o2.getName() == null) return 1;
        return o2.getName().compareTo(o1.getName());
    };

    public static Comparator<Document> byDescendingSize = (o1, o2) -> {
        if (o1 == o2) return 0;
        if (o1 == null || o1.getName() == null) return -1;
        if (o2 == null || o2.getName() == null) return 1;
        return o2.getName().length() - o1.getName().length();
    };

    public static Comparator<Document> byDescendingCreateDate = (o1, o2) -> {
        if (o1 == o2) return 0;
        if (o1 == null || o1.getCreatedAt() == 0) return -1;
        if (o2 == null || o2.getCreatedAt() == 0) return 1;
        return new Date(o2.getCreatedAt()).compareTo(new Date(o1.getCreatedAt()));
    };

    public static Comparator<Document> byDescendingLastUpdate = (o1, o2) -> {
        if (o1 == o2) return 0;
        if (o1 == null || o1.getLastUpdateAt() == 0) return -1;
        if (o2 == null || o2.getLastUpdateAt() == 0) return 1;
        return new Date(o2.getLastUpdateAt()).compareTo(new Date(o1.getLastUpdateAt()));
    };

    public static Comparator<Document> byDescendingOwner = (o1, o2) -> {
        if (o1 == o2) return 0;
        if (o1 == null || o1.getOwnerId() == null) return -1;
        if (o2 == null || o2.getOwnerId() == null) return 1;
        return o2.getOwnerId().compareTo(o1.getOwnerId());
    };
}
