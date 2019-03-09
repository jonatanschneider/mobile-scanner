package de.thm.scanman.util;

import java.util.Comparator;
import java.util.Date;
import static java.lang.Math.toIntExact;

import de.thm.scanman.model.Document;

public class DocumentComparators {
    private static Comparator<Document> alphabetically = (o1, o2) -> {
        if (o1 == o2) return 0;
        if (o1 == null || o1.getName() == null) return 1;
        if (o2 == null || o2.getName() == null) return -1;
        return o1.getName().compareTo(o2.getName());
    };

    private static Comparator<Document> bySize = (o1, o2) -> {
        if (o1 == o2) return 0;
        if (o1 == null || o1.getName() == null) return 1;
        if (o2 == null || o2.getName() == null) return -1;
        return toIntExact(o1.getSize() - o2.getSize());
    };

    private static Comparator<Document> byCreateDate = (o1, o2) -> {
        if (o1 == o2) return 0;
        if (o1 == null || o1.getCreatedAt() == 0) return 1;
        if (o2 == null || o2.getCreatedAt() == 0) return -1;
        return new Date(o1.getCreatedAt()).compareTo(new Date(o2.getCreatedAt()));
    };

    private static Comparator<Document> byLastUpdate = (o1, o2) -> {
        if (o1 == o2) return 0;
        if (o1 == null || o1.getLastUpdateAt() == 0) return 1;
        if (o2 == null || o2.getLastUpdateAt() == 0) return -1;
        return new Date(o1.getLastUpdateAt()).compareTo(new Date(o2.getLastUpdateAt()));
    };

    private static Comparator<Document> byOwner = (o1, o2) -> {
        if (o1 == o2) return 0;
        if (o1 == null || o1.getOwnerId() == null) return 1;
        if (o2 == null || o2.getOwnerId() == null) return -1;
        return o1.getOwnerId().compareTo(o2.getOwnerId());
    };

    private static Comparator<Document> descendingAlphabetically = (o1, o2) -> {
        if (o1 == o2) return 0;
        if (o1 == null || o1.getName() == null) return -1;
        if (o2 == null || o2.getName() == null) return 1;
        return o2.getName().compareTo(o1.getName());
    };

    private static Comparator<Document> byDescendingSize = (o1, o2) -> {
        if (o1 == o2) return 0;
        if (o1 == null || o1.getName() == null) return -1;
        if (o2 == null || o2.getName() == null) return 1;
        return toIntExact(o2.getSize() - o1.getSize());    };

    private static Comparator<Document> byDescendingCreateDate = (o1, o2) -> {
        if (o1 == o2) return 0;
        if (o1 == null || o1.getCreatedAt() == 0) return -1;
        if (o2 == null || o2.getCreatedAt() == 0) return 1;
        return new Date(o2.getCreatedAt()).compareTo(new Date(o1.getCreatedAt()));
    };

    private static Comparator<Document> byDescendingLastUpdate = (o1, o2) -> {
        if (o1 == o2) return 0;
        if (o1 == null || o1.getLastUpdateAt() == 0) return -1;
        if (o2 == null || o2.getLastUpdateAt() == 0) return 1;
        return new Date(o2.getLastUpdateAt()).compareTo(new Date(o1.getLastUpdateAt()));
    };

    private static Comparator<Document> byDescendingOwner = (o1, o2) -> {
        if (o1 == o2) return 0;
        if (o1 == null || o1.getOwnerId() == null) return -1;
        if (o2 == null || o2.getOwnerId() == null) return 1;
        return o2.getOwnerId().compareTo(o1.getOwnerId());
    };

    public static Comparator<Document> getComparator(int pos, boolean descending) {
        switch (pos){
            case 0:
                if (descending) return DocumentComparators.descendingAlphabetically;
                else return DocumentComparators.alphabetically;
            case 1:
                if (descending) return DocumentComparators.byDescendingSize;
                else return DocumentComparators.bySize;
            case 2:
                if (descending) return DocumentComparators.byDescendingCreateDate;
                else return DocumentComparators.byCreateDate;
            case 3:
                if (descending) return DocumentComparators.byDescendingLastUpdate;
                else return DocumentComparators.byLastUpdate;
            case 4:
                if (descending) return DocumentComparators.byDescendingOwner;
                else return DocumentComparators.byOwner;
            default:
                if (descending) return DocumentComparators.descendingAlphabetically;
                else return DocumentComparators.alphabetically;
        }
    }
}
