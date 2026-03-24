package controller;

import model.*;
import utils.FileHandler;
import utils.IDGenerator;
import java.util.ArrayList;
import java.util.List;

public class LibraryManager {
    private LibraryDatabase db;
    private SearchEngine searchEngine;
    private BorrowController borrowController;
    private FileHandler fileHandler;
    private IDGenerator idGenerator;

    public LibraryManager() {
        this.db = new LibraryDatabase();
        this.searchEngine = new SearchEngine();
        this.borrowController = new BorrowController(db);
        this.fileHandler = new FileHandler();
        this.idGenerator = new IDGenerator();
    }

    public LibraryDatabase getDatabase() { return db; }
    public SearchEngine getSearchEngine() { return searchEngine; }
    public BorrowController getBorrowController() { return borrowController; }
    public IDGenerator getIdGenerator() { return idGenerator; }

    public void loadData(String dataDir) {
        fileHandler.loadItems(db, dataDir + "/library_items.csv");
        fileHandler.loadUsers(db, dataDir + "/users.csv");
        db.updateCache();
    }

    public void saveData(String dataDir) {
        fileHandler.saveItems(db.getItems(), dataDir + "/library_items.csv");
        fileHandler.saveUsers(db.getUsers(), dataDir + "/users.csv");
    }

    public String addBook(String title, String author, int year, String isbn, String genre, int pages) {
        String id = idGenerator.generateId("BK");
        Book book = new Book(id, title, author, year, isbn, genre, pages);
        db.addItem(book);
        return id;
    }

    public String addMagazine(String title, String author, int year, int issueNumber, String publisher, String month) {
        String id = idGenerator.generateId("MG");
        Magazine mag = new Magazine(id, title, author, year, issueNumber, publisher, month);
        db.addItem(mag);
        return id;
    }

    public String addJournal(String title, String author, int year, int volume, int issueNumber, String subject) {
        String id = idGenerator.generateId("JN");
        Journal journal = new Journal(id, title, author, year, volume, issueNumber, subject);
        db.addItem(journal);
        return id;
    }

    public String addUser(String name, String email, String role) {
        String id = idGenerator.generateId("USR");
        UserAccount user = new UserAccount(id, name, email, role);
        db.addUser(user);
        return id;
    }

    public void deleteItem(LibraryItem item) {
        db.removeItem(item);
    }

    public boolean undoLastDelete() {
        return db.undoLastDelete();
    }

    // ============ SORTING ALGORITHMS ============

    // Selection Sort by title
    public void selectionSortByTitle(List<LibraryItem> items) {
        int n = items.size();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (items.get(j).getTitle().compareToIgnoreCase(items.get(minIdx).getTitle()) < 0) {
                    minIdx = j;
                }
            }
            LibraryItem temp = items.get(minIdx);
            items.set(minIdx, items.get(i));
            items.set(i, temp);
        }
    }

    // Insertion Sort by author
    public void insertionSortByAuthor(List<LibraryItem> items) {
        int n = items.size();
        for (int i = 1; i < n; i++) {
            LibraryItem key = items.get(i);
            int j = i - 1;
            while (j >= 0 && items.get(j).getAuthor().compareToIgnoreCase(key.getAuthor()) > 0) {
                items.set(j + 1, items.get(j));
                j--;
            }
            items.set(j + 1, key);
        }
    }

    // Insertion Sort by year
    public void insertionSortByYear(List<LibraryItem> items) {
        int n = items.size();
        for (int i = 1; i < n; i++) {
            LibraryItem key = items.get(i);
            int j = i - 1;
            while (j >= 0 && items.get(j).getYear() > key.getYear()) {
                items.set(j + 1, items.get(j));
                j--;
            }
            items.set(j + 1, key);
        }
    }

    // Merge Sort by title
    public List<LibraryItem> mergeSortByTitle(List<LibraryItem> items) {
        if (items.size() <= 1) return items;
        int mid = items.size() / 2;
        List<LibraryItem> left = mergeSortByTitle(new ArrayList<>(items.subList(0, mid)));
        List<LibraryItem> right = mergeSortByTitle(new ArrayList<>(items.subList(mid, items.size())));
        return merge(left, right, "title");
    }

    // Merge Sort by author
    public List<LibraryItem> mergeSortByAuthor(List<LibraryItem> items) {
        if (items.size() <= 1) return items;
        int mid = items.size() / 2;
        List<LibraryItem> left = mergeSortByAuthor(new ArrayList<>(items.subList(0, mid)));
        List<LibraryItem> right = mergeSortByAuthor(new ArrayList<>(items.subList(mid, items.size())));
        return merge(left, right, "author");
    }

    // Merge Sort by year
    public List<LibraryItem> mergeSortByYear(List<LibraryItem> items) {
        if (items.size() <= 1) return items;
        int mid = items.size() / 2;
        List<LibraryItem> left = mergeSortByYear(new ArrayList<>(items.subList(0, mid)));
        List<LibraryItem> right = mergeSortByYear(new ArrayList<>(items.subList(mid, items.size())));
        return merge(left, right, "year");
    }

    private List<LibraryItem> merge(List<LibraryItem> left, List<LibraryItem> right, String field) {
        List<LibraryItem> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < left.size() && j < right.size()) {
            int cmp;
            switch (field) {
                case "author": cmp = left.get(i).getAuthor().compareToIgnoreCase(right.get(j).getAuthor()); break;
                case "year": cmp = Integer.compare(left.get(i).getYear(), right.get(j).getYear()); break;
                default: cmp = left.get(i).getTitle().compareToIgnoreCase(right.get(j).getTitle()); break;
            }
            if (cmp <= 0) result.add(left.get(i++));
            else result.add(right.get(j++));
        }
        while (i < left.size()) result.add(left.get(i++));
        while (j < right.size()) result.add(right.get(j++));
        return result;
    }

    // Quick Sort by title
    public void quickSortByTitle(List<LibraryItem> items, int low, int high) {
        if (low < high) {
            int pivot = partitionByTitle(items, low, high);
            quickSortByTitle(items, low, pivot - 1);
            quickSortByTitle(items, pivot + 1, high);
        }
    }

    private int partitionByTitle(List<LibraryItem> items, int low, int high) {
        String pivot = items.get(high).getTitle().toLowerCase();
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (items.get(j).getTitle().toLowerCase().compareTo(pivot) <= 0) {
                i++;
                LibraryItem temp = items.get(i);
                items.set(i, items.get(j));
                items.set(j, temp);
            }
        }
        LibraryItem temp = items.get(i + 1);
        items.set(i + 1, items.get(high));
        items.set(high, temp);
        return i + 1;
    }

    // Report: most borrowed items (top N)
    public List<LibraryItem> getMostBorrowedItems(int topN) {
        List<LibraryItem> sorted = new ArrayList<>(db.getItems());
        // Use selection sort for reporting
        int n = sorted.size();
        for (int i = 0; i < n - 1; i++) {
            int maxIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (sorted.get(j).getBorrowCount() > sorted.get(maxIdx).getBorrowCount()) {
                    maxIdx = j;
                }
            }
            LibraryItem tmp = sorted.get(maxIdx);
            sorted.set(maxIdx, sorted.get(i));
            sorted.set(i, tmp);
        }
        return sorted.subList(0, Math.min(topN, sorted.size()));
    }

    // Report: users with overdue items
    public List<UserAccount> getOverdueUsers() {
        List<UserAccount> overdue = new ArrayList<>();
        for (UserAccount u : db.getUsers()) {
            if (u.hasOverdue()) overdue.add(u);
        }
        return overdue;
    }

    // Report: category distribution
    public String getCategoryDistribution() {
        int books = db.countByCategory("Book");
        int magazines = db.countByCategory("Magazine");
        int journals = db.countByCategory("Journal");
        int total = books + magazines + journals;
        return String.format("Category Distribution:\n  Books: %d\n  Magazines: %d\n  Journals: %d\n  Total: %d",
            books, magazines, journals, total);
    }
}
