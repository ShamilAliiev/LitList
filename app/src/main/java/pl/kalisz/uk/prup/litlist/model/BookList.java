package pl.kalisz.uk.prup.litlist.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BookList implements Serializable {
    private String id;
    private String name;
    private String description;
    private List<String> bookIds;
    private boolean isDefault;
    private ListType type;

    public enum ListType {
        WANT_TO_READ,
        ALREADY_READ,
        CURRENTLY_READING,
        CUSTOM
    }

    public BookList() {
        this.bookIds = new ArrayList<>();
        this.isDefault = false;
        this.type = ListType.CUSTOM;
        this.id = generateId();
    }

    public BookList(String name) {
        this();
        this.name = name;
    }

    public BookList(String name, ListType type, boolean isDefault) {
        this(name);
        this.type = type;
        this.isDefault = isDefault;
    }

    private String generateId() {
        return "list_" + System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getBookIds() {
        return bookIds;
    }

    public void setBookIds(List<String> bookIds) {
        this.bookIds = bookIds;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public ListType getType() {
        return type;
    }

    public void setType(ListType type) {
        this.type = type;
    }

    public void addBook(String bookId) {
        if (!bookIds.contains(bookId)) {
            bookIds.add(bookId);
        }
    }

    public void removeBook(String bookId) {
        bookIds.remove(bookId);
    }

    public boolean containsBook(String bookId) {
        return bookIds.contains(bookId);
    }

    public int getBookCount() {
        return bookIds.size();
    }
}
