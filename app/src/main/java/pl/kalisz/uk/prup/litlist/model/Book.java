package pl.kalisz.uk.prup.litlist.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Book implements Serializable {
    private String id;
    private String title;
    private String author;
    private String isbn;
    private String imageUrl;
    private String description;
    private int totalPages;
    private int currentPage;
    private List<Note> notes;
    private List<Bookmark> bookmarks;
    private ReadingStatus status;

    public enum ReadingStatus {
        WANT_TO_READ,
        CURRENTLY_READING,
        ALREADY_READ
    }

    public Book() {
        this.notes = new ArrayList<>();
        this.bookmarks = new ArrayList<>();
        this.status = ReadingStatus.WANT_TO_READ;
        this.currentPage = 0;
    }

    public Book(String title, String author) {
        this();
        this.title = title;
        this.author = author;
        this.id = generateId();
    }

    public Book(String title, String author, String isbn) {
        this(title, author);
        this.isbn = isbn;
    }

    private String generateId() {
        return "book_" + System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public List<Bookmark> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(List<Bookmark> bookmarks) {
        this.bookmarks = bookmarks;
    }

    public ReadingStatus getStatus() {
        return status;
    }

    public void setStatus(ReadingStatus status) {
        this.status = status;
    }

    public void addNote(Note note) {
        this.notes.add(note);
    }

    public void addBookmark(Bookmark bookmark) {
        this.bookmarks.add(bookmark);
    }

    public int getProgressPercentage() {
        if (totalPages <= 0) return 0;
        return (int) ((currentPage * 100.0) / totalPages);
    }

    public String getProgressText() {
        if (totalPages <= 0) return "Nie ustawiono liczby stron";
        return currentPage + " / " + totalPages + " stron (" + getProgressPercentage() + "%)";
    }
}
