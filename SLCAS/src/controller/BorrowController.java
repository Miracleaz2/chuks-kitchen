package controller;

import model.*;
import java.util.concurrent.TimeUnit;

public class BorrowController {
    private LibraryDatabase db;
    private static final long BORROW_DURATION_MS = TimeUnit.DAYS.toMillis(14); // 2 weeks

    public BorrowController(LibraryDatabase db) {
        this.db = db;
    }

    public String borrowItem(String itemId, String userId) {
        LibraryItem item = db.findItemById(itemId);
        UserAccount user = db.findUserById(userId);
        if (item == null) return "Item not found: " + itemId;
        if (user == null) return "User not found: " + userId;
        if (!item.isAvailable()) {
            // Add to reservation queue
            if (!db.hasReservation(itemId)) {
                db.addReservation(itemId, userId);
                return "Item is not available. Added to reservation queue.";
            }
            return "Item is not available and you are already in the reservation queue.";
        }
        item.borrow(userId);
        long dueDate = System.currentTimeMillis() + BORROW_DURATION_MS;
        user.addBorrow(itemId, dueDate);
        db.updateCache();
        return "Successfully borrowed '" + item.getTitle() + "'. Due date: " + new java.util.Date(dueDate);
    }

    public String returnItem(String itemId, String userId) {
        LibraryItem item = db.findItemById(itemId);
        UserAccount user = db.findUserById(userId);
        if (item == null) return "Item not found: " + itemId;
        if (user == null) return "User not found: " + userId;
        if (!item.returnItem(userId)) {
            return "Return failed: item is either available or you didn't borrow it.";
        }
        user.returnItem(itemId);
        db.updateCache();
        // Check reservation queue
        String result = "Successfully returned '" + item.getTitle() + "'.";
        if (db.hasReservation(itemId)) {
            String reservation = null;
            for (String r : db.getReservationQueue()) {
                if (r.startsWith(itemId + ":")) {
                    reservation = r;
                    break;
                }
            }
            if (reservation != null) {
                db.getReservationQueue().remove(reservation);
                String nextUserId = reservation.split(":")[1];
                result += "\nNext reservation notified for user: " + nextUserId;
            }
        }
        return result;
    }

    public String getOverdueInfo(String userId) {
        UserAccount user = db.findUserById(userId);
        if (user == null) return "User not found.";
        if (!user.hasOverdue()) return "No overdue items.";
        double fine = user.computeOverdueFine();
        return String.format("User %s has %d overdue item(s). Total fine: $%.2f",
            user.getName(), user.getOverdueCount(), fine);
    }
}
