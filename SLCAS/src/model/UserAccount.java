package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UserAccount {
    private String userId;
    private String name;
    private String email;
    private String role; // "student", "faculty", "admin"
    private List<String> borrowingHistory; // item IDs
    private List<String> currentlyBorrowed; // item IDs
    private List<Long> dueDates; // timestamps for each currently borrowed item

    public UserAccount(String userId, String name, String email, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.borrowingHistory = new ArrayList<>();
        this.currentlyBorrowed = new ArrayList<>();
        this.dueDates = new ArrayList<>();
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public List<String> getBorrowingHistory() { return borrowingHistory; }
    public List<String> getCurrentlyBorrowed() { return currentlyBorrowed; }
    public List<Long> getDueDates() { return dueDates; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }

    public void addBorrow(String itemId, long dueDate) {
        currentlyBorrowed.add(itemId);
        dueDates.add(dueDate);
        borrowingHistory.add(itemId);
    }

    /** Restores a currently-borrowed item from persistence without adding to borrowingHistory. */
    public void restoreCurrentBorrow(String itemId, long dueDate) {
        currentlyBorrowed.add(itemId);
        dueDates.add(dueDate);
    }

    public boolean returnItem(String itemId) {
        int idx = currentlyBorrowed.indexOf(itemId);
        if (idx >= 0) {
            currentlyBorrowed.remove(idx);
            dueDates.remove(idx);
            return true;
        }
        return false;
    }

    public boolean hasOverdue() {
        long now = System.currentTimeMillis();
        for (long due : dueDates) {
            if (now > due) return true;
        }
        return false;
    }

    public int getOverdueCount() {
        long now = System.currentTimeMillis();
        int count = 0;
        for (long due : dueDates) {
            if (now > due) count++;
        }
        return count;
    }

    // Recursive overdue fine calculation: $0.50 per day per overdue item
    public double computeOverdueFine() {
        return computeOverdueFineRecursive(0, 0);
    }

    private double computeOverdueFineRecursive(int idx, double totalFine) {
        if (idx >= dueDates.size()) return totalFine;
        long now = System.currentTimeMillis();
        long due = dueDates.get(idx);
        if (now > due) {
            long daysOverdue = TimeUnit.MILLISECONDS.toDays(now - due);
            totalFine += daysOverdue * 0.50;
        }
        return computeOverdueFineRecursive(idx + 1, totalFine);
    }

    public String toCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append(userId).append(",")
          .append(escapeCSV(name)).append(",")
          .append(escapeCSV(email)).append(",")
          .append(role).append(",")
          .append(String.join(";", borrowingHistory)).append(",")
          .append(String.join(";", currentlyBorrowed)).append(",");
        // Due dates as semicolon-separated timestamps
        List<String> dueDateStrs = new ArrayList<>();
        for (long d : dueDates) dueDateStrs.add(String.valueOf(d));
        sb.append(String.join(";", dueDateStrs));
        return sb.toString();
    }

    private String escapeCSV(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %s | Borrowed: %d | Overdue: %d",
            userId, name, role, email, currentlyBorrowed.size(), getOverdueCount());
    }
}
