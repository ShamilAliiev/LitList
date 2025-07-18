package pl.kalisz.uk.prup.litlist.model;

import java.io.Serializable;
import java.util.Date;

public class Note implements Serializable {
    private String id;
    private int page;
    private String chapter;
    private String content;
    private Date createdAt;
    private Date updatedAt;

    public Note() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.id = generateId();
    }

    public Note(int page, String content) {
        this();
        this.page = page;
        this.content = content;
    }

    public Note(int page, String chapter, String content) {
        this(page, content);
        this.chapter = chapter;
    }

    private String generateId() {
        return "note_" + System.currentTimeMillis();
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.updatedAt = new Date();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
