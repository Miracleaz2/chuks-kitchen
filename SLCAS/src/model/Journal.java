package model;

public class Journal extends LibraryItem {
    private int volume;
    private int issueNumber;
    private String subject;

    public Journal(String id, String title, String author, int year, int volume, int issueNumber, String subject) {
        super(id, title, author, year, "Journal");
        this.volume = volume;
        this.issueNumber = issueNumber;
        this.subject = subject;
    }

    public int getVolume() { return volume; }
    public int getIssueNumber() { return issueNumber; }
    public String getSubject() { return subject; }
    public void setVolume(int volume) { this.volume = volume; }
    public void setIssueNumber(int issueNumber) { this.issueNumber = issueNumber; }
    public void setSubject(String subject) { this.subject = subject; }

    @Override
    public String getDetails() {
        return String.format("Journal: '%s' (%d)\nAuthor: %s | Volume: %d | Issue: %d | Subject: %s\nStatus: %s",
            title, year, author, volume, issueNumber, subject, available ? "Available" : "Borrowed by " + currentBorrower);
    }

    @Override
    public String toCSV() {
        return super.toCSV() + "," + volume + "," + issueNumber + "," + escapeCSV(subject);
    }
}
