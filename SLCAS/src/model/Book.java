package model;

public class Book extends LibraryItem {
    private String isbn;
    private String genre;
    private int pages;

    public Book(String id, String title, String author, int year, String isbn, String genre, int pages) {
        super(id, title, author, year, "Book");
        this.isbn = isbn;
        this.genre = genre;
        this.pages = pages;
    }

    public String getIsbn() { return isbn; }
    public String getGenre() { return genre; }
    public int getPages() { return pages; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setPages(int pages) { this.pages = pages; }

    @Override
    public String getDetails() {
        return String.format("Book: '%s' by %s (%d)\nISBN: %s | Genre: %s | Pages: %d\nStatus: %s",
            title, author, year, isbn, genre, pages, available ? "Available" : "Borrowed by " + currentBorrower);
    }

    @Override
    public String toCSV() {
        return super.toCSV() + "," + escapeCSV(isbn) + "," + escapeCSV(genre) + "," + pages;
    }
}
