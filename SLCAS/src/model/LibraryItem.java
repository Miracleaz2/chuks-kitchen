package model;

public abstract class LibraryItem implements Borrowable {
    protected String id;
    protected String title;
    protected String author;
    protected int year;
    protected String type;
    protected boolean available;
    protected int borrowCount;
    protected String currentBorrower; // userId of current borrower, null if available

    public LibraryItem(String id, String title, String author, int year, String type) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.type = type;
        this.available = true;
        this.borrowCount = 0;
        this.currentBorrower = null;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }
    public String getType() { return type; }
    public boolean isAvailable() { return available; }
    public int getBorrowCount() { return borrowCount; }
    public String getCurrentBorrower() { return currentBorrower; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setYear(int year) { this.year = year; }
    public void setBorrowCount(int borrowCount) { this.borrowCount = borrowCount; }

    /** Restores borrow state from persistence without incrementing borrowCount. */
    public void restoreBorrowState(String userId) {
        this.available = false;
        this.currentBorrower = userId;
    }

    // Borrowable implementation
    @Override
    public boolean borrow(String userId) {
        if (available) {
            available = false;
            borrowCount++;
            currentBorrower = userId;
            return true;
        }
        return false;
    }

    @Override
    public boolean returnItem(String userId) {
        if (!available && userId.equals(currentBorrower)) {
            available = true;
            currentBorrower = null;
            return true;
        }
        return false;
    }

    // Abstract method for polymorphism
    public abstract String getDetails();

    @Override
    public String toString() {
        return String.format("[%s] %s by %s (%d) - %s",
            type, title, author, year, available ? "Available" : "Borrowed");
    }

    // For serialization
    public String toCSV() {
        return String.format("%s,%s,%s,%s,%d,%b,%d,%s",
            type, id, escapeCSV(title), escapeCSV(author), year, available, borrowCount,
            currentBorrower != null ? currentBorrower : "");
    }

    protected String escapeCSV(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
