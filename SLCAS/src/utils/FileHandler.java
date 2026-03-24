package utils;

import model.*;
import java.io.*;
import java.util.*;

public class FileHandler {

    public void saveItems(List<LibraryItem> items, String filePath) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                for (LibraryItem item : items) {
                    pw.println(item.toCSV());
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving items: " + e.getMessage());
        }
    }

    public void loadItems(LibraryDatabase db, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    LibraryItem item = parseItem(line);
                    if (item != null) db.addItem(item);
                } catch (Exception e) {
                    System.err.println("Error parsing item: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading items: " + e.getMessage());
        }
    }

    private LibraryItem parseItem(String line) {
        String[] parts = parseCSVLine(line);
        if (parts.length < 8) return null;
        String type = parts[0];
        String id = parts[1];
        String title = parts[2];
        String author = parts[3];
        int year = Integer.parseInt(parts[4]);
        boolean available = Boolean.parseBoolean(parts[5]);
        int borrowCount = Integer.parseInt(parts[6]);
        String currentBorrower = parts[7].isEmpty() ? null : parts[7];

        LibraryItem item = null;
        switch (type) {
            case "Book":
                if (parts.length >= 11) {
                    String isbn = parts[8];
                    String genre = parts[9];
                    int pages = Integer.parseInt(parts[10]);
                    item = new Book(id, title, author, year, isbn, genre, pages);
                }
                break;
            case "Magazine":
                if (parts.length >= 11) {
                    int issueNumber = Integer.parseInt(parts[8]);
                    String publisher = parts[9];
                    String month = parts[10];
                    item = new Magazine(id, title, author, year, issueNumber, publisher, month);
                }
                break;
            case "Journal":
                if (parts.length >= 11) {
                    int volume = Integer.parseInt(parts[8]);
                    int issueNumber = Integer.parseInt(parts[9]);
                    String subject = parts[10];
                    item = new Journal(id, title, author, year, volume, issueNumber, subject);
                }
                break;
        }
        if (item != null) {
            item.setBorrowCount(borrowCount);
            if (!available && currentBorrower != null) {
                item.restoreBorrowState(currentBorrower);
            }
        }
        return item;
    }

    public void saveUsers(List<UserAccount> users, String filePath) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                for (UserAccount user : users) {
                    pw.println(user.toCSV());
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    public void loadUsers(LibraryDatabase db, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    UserAccount user = parseUser(line);
                    if (user != null) db.addUser(user);
                } catch (Exception e) {
                    System.err.println("Error parsing user: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    private UserAccount parseUser(String line) {
        String[] parts = parseCSVLine(line);
        if (parts.length < 4) return null;
        String userId = parts[0];
        String name = parts[1];
        String email = parts[2];
        String role = parts[3];
        UserAccount user = new UserAccount(userId, name, email, role);
        if (parts.length > 4 && !parts[4].isEmpty()) {
            for (String itemId : parts[4].split(";")) {
                if (!itemId.isEmpty()) user.getBorrowingHistory().add(itemId);
            }
        }
        if (parts.length > 5 && !parts[5].isEmpty()) {
            String[] borrowed = parts[5].split(";");
            String[] dueDateArr = parts.length > 6 ? parts[6].split(";") : new String[0];
            for (int i = 0; i < borrowed.length; i++) {
                if (!borrowed[i].isEmpty()) {
                    long due = (i < dueDateArr.length && !dueDateArr[i].isEmpty()) ?
                        Long.parseLong(dueDateArr[i]) : System.currentTimeMillis() + 86400000L * 14;
                    user.restoreCurrentBorrow(borrowed[i], due);
                }
            }
        }
        return user;
    }

    // Simple CSV parser supporting quoted fields
    private String[] parseCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }
}
