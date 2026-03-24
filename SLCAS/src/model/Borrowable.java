package model;

public interface Borrowable {
    boolean borrow(String userId);
    boolean returnItem(String userId);
    boolean isAvailable();
    int getBorrowCount();
}
