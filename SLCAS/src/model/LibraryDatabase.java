package model;

import java.util.*;

public class LibraryDatabase {
    private ArrayList<LibraryItem> items;
    private ArrayList<UserAccount> users;
    // Queue<String> format: "itemId:userId" for reservations
    private Queue<String> reservationQueue;
    // Stack for undo: stores deleted items
    private Stack<LibraryItem> undoStack;
    // Fixed-size array cache for most frequently accessed items (top 5)
    private static final int CACHE_SIZE = 5;
    private LibraryItem[] accessCache;

    public LibraryDatabase() {
        items = new ArrayList<>();
        users = new ArrayList<>();
        reservationQueue = new LinkedList<>();
        undoStack = new Stack<>();
        accessCache = new LibraryItem[CACHE_SIZE];
    }

    // Items management
    public void addItem(LibraryItem item) {
        items.add(item);
        updateCache();
    }

    public void removeItem(LibraryItem item) {
        if (items.remove(item)) {
            undoStack.push(item);
            updateCache();
        }
    }

    public boolean undoLastDelete() {
        if (!undoStack.isEmpty()) {
            LibraryItem item = undoStack.pop();
            items.add(item);
            updateCache();
            return true;
        }
        return false;
    }

    public ArrayList<LibraryItem> getItems() { return items; }
    public ArrayList<UserAccount> getUsers() { return users; }
    public Queue<String> getReservationQueue() { return reservationQueue; }
    public Stack<LibraryItem> getUndoStack() { return undoStack; }
    public LibraryItem[] getAccessCache() { return accessCache; }

    // Users management
    public void addUser(UserAccount user) { users.add(user); }
    public void removeUser(UserAccount user) { users.remove(user); }

    public UserAccount findUserById(String userId) {
        for (UserAccount u : users) {
            if (u.getUserId().equals(userId)) return u;
        }
        return null;
    }

    public LibraryItem findItemById(String itemId) {
        for (LibraryItem item : items) {
            if (item.getId().equals(itemId)) return item;
        }
        return null;
    }

    // Reservation queue
    public void addReservation(String itemId, String userId) {
        reservationQueue.offer(itemId + ":" + userId);
    }

    public String pollNextReservation() {
        return reservationQueue.poll();
    }

    public boolean hasReservation(String itemId) {
        for (String r : reservationQueue) {
            if (r.startsWith(itemId + ":")) return true;
        }
        return false;
    }

    // Update the most-frequently-accessed cache (top CACHE_SIZE by borrowCount)
    public void updateCache() {
        List<LibraryItem> sorted = new ArrayList<>(items);
        sorted.sort((a, b) -> b.getBorrowCount() - a.getBorrowCount());
        for (int i = 0; i < CACHE_SIZE; i++) {
            accessCache[i] = i < sorted.size() ? sorted.get(i) : null;
        }
    }

    // Recursive: count items by category
    public int countByCategory(String category) {
        return countByCategoryRecursive(items, category, 0);
    }

    private int countByCategoryRecursive(List<LibraryItem> list, String category, int idx) {
        if (idx >= list.size()) return 0;
        int count = list.get(idx).getType().equalsIgnoreCase(category) ? 1 : 0;
        return count + countByCategoryRecursive(list, category, idx + 1);
    }

    // Process any LibraryItem polymorphically
    public String processItem(LibraryItem item) {
        return item.getDetails();
    }
}
