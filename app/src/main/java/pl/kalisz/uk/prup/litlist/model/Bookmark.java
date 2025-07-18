package pl.kalisz.uk.prup.litlist.model;

import java.io.Serializable;
import java.util.Date;

public class Bookmark implements Serializable {
    private String id;
    private int page;
    private String chapter;
    private String description;
    private Date createdAt;

    public Bookmark() {
        this.createdAt = new Date();
        this.id = generateId();
    }

    public Bookmark(int page) {
        this();
        this.page = page;
    }

    public Bookmark(int page, String chapter) {
        this(page);
        this.chapter = chapter;
    }

    public Bookmark(int page, String chapter, String description) {
        this(page, chapter);
        this.description = description;
    }

    private String generateId() {
        return "bookmark_" + System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
