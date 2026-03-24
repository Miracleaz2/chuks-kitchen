package controller;

import model.LibraryItem;
import java.util.ArrayList;
import java.util.List;

public class SearchEngine {

    // Linear search by title (case-insensitive substring)
    public List<LibraryItem> linearSearchByTitle(List<LibraryItem> items, String query) {
        List<LibraryItem> results = new ArrayList<>();
        String q = query.toLowerCase();
        for (LibraryItem item : items) {
            if (item.getTitle().toLowerCase().contains(q)) {
                results.add(item);
            }
        }
        return results;
    }

    // Linear search by author
    public List<LibraryItem> linearSearchByAuthor(List<LibraryItem> items, String query) {
        List<LibraryItem> results = new ArrayList<>();
        String q = query.toLowerCase();
        for (LibraryItem item : items) {
            if (item.getAuthor().toLowerCase().contains(q)) {
                results.add(item);
            }
        }
        return results;
    }

    // Linear search by type
    public List<LibraryItem> linearSearchByType(List<LibraryItem> items, String type) {
        List<LibraryItem> results = new ArrayList<>();
        for (LibraryItem item : items) {
            if (item.getType().equalsIgnoreCase(type)) {
                results.add(item);
            }
        }
        return results;
    }

    // Binary search by title (list must be sorted by title)
    // Returns index of first match, or -1
    public int binarySearchByTitle(List<LibraryItem> sortedItems, String title) {
        int low = 0, high = sortedItems.size() - 1;
        String t = title.toLowerCase();
        while (low <= high) {
            int mid = (low + high) / 2;
            String midTitle = sortedItems.get(mid).getTitle().toLowerCase();
            int cmp = midTitle.compareTo(t);
            if (cmp == 0) return mid;
            else if (cmp < 0) low = mid + 1;
            else high = mid - 1;
        }
        return -1;
    }

    // Recursive search by title (searches through list recursively)
    public LibraryItem recursiveSearchByTitle(List<LibraryItem> items, String title, int idx) {
        if (idx >= items.size()) return null;
        if (items.get(idx).getTitle().equalsIgnoreCase(title)) return items.get(idx);
        return recursiveSearchByTitle(items, title, idx + 1);
    }

    // Recursive search by author
    public List<LibraryItem> recursiveSearchByAuthor(List<LibraryItem> items, String author, int idx, List<LibraryItem> results) {
        if (idx >= items.size()) return results;
        if (items.get(idx).getAuthor().toLowerCase().contains(author.toLowerCase())) {
            results.add(items.get(idx));
        }
        return recursiveSearchByAuthor(items, author, idx + 1, results);
    }
}
