package model;

public class Magazine extends LibraryItem {
    private int issueNumber;
    private String publisher;
    private String month;

    public Magazine(String id, String title, String author, int year, int issueNumber, String publisher, String month) {
        super(id, title, author, year, "Magazine");
        this.issueNumber = issueNumber;
        this.publisher = publisher;
        this.month = month;
    }

    public int getIssueNumber() { return issueNumber; }
    public String getPublisher() { return publisher; }
    public String getMonth() { return month; }
    public void setIssueNumber(int issueNumber) { this.issueNumber = issueNumber; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setMonth(String month) { this.month = month; }

    @Override
    public String getDetails() {
        return String.format("Magazine: '%s' (%d)\nPublisher: %s | Issue: %d | Month: %s\nStatus: %s",
            title, year, publisher, issueNumber, month, available ? "Available" : "Borrowed by " + currentBorrower);
    }

    @Override
    public String toCSV() {
        return super.toCSV() + "," + issueNumber + "," + escapeCSV(publisher) + "," + escapeCSV(month);
    }
}
