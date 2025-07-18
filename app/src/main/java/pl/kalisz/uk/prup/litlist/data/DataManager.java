package pl.kalisz.uk.prup.litlist.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pl.kalisz.uk.prup.litlist.model.Book;
import pl.kalisz.uk.prup.litlist.model.BookList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {
    private static final String PREF_NAME = "LitListData";
    private static final String KEY_BOOKS = "books";
    private static final String KEY_LISTS = "lists";
    
    private static DataManager instance;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    
    private Map<String, Book> books;
    private Map<String, BookList> bookLists;

    private DataManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        books = new HashMap<>();
        bookLists = new HashMap<>();
        loadData();
        initializeDefaultLists();
    }

    public static synchronized DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context.getApplicationContext());
        }
        return instance;
    }

    private void loadData() {
        // Load books
        String booksJson = sharedPreferences.getString(KEY_BOOKS, "");
        if (!booksJson.isEmpty()) {
            Type type = new TypeToken<Map<String, Book>>(){}.getType();
            Map<String, Book> loadedBooks = gson.fromJson(booksJson, type);
            if (loadedBooks != null) {
                books = loadedBooks;
            }
        }

        // Load lists
        String listsJson = sharedPreferences.getString(KEY_LISTS, "");
        if (!listsJson.isEmpty()) {
            Type type = new TypeToken<Map<String, BookList>>(){}.getType();
            Map<String, BookList> loadedLists = gson.fromJson(listsJson, type);
            if (loadedLists != null) {
                bookLists = loadedLists;
            }
        }
    }

    private void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        String booksJson = gson.toJson(books);
        editor.putString(KEY_BOOKS, booksJson);
        
        String listsJson = gson.toJson(bookLists);
        editor.putString(KEY_LISTS, listsJson);
        
        editor.apply();
    }

    private void initializeDefaultLists() {
        // Check if any default lists are missing
        boolean hasWantToRead = false;
        boolean hasAlreadyRead = false;
        boolean hasCurrentlyReading = false;
        
        for (BookList list : bookLists.values()) {
            if (list.getType() == BookList.ListType.WANT_TO_READ) {
                hasWantToRead = true;
            } else if (list.getType() == BookList.ListType.ALREADY_READ) {
                hasAlreadyRead = true;
            } else if (list.getType() == BookList.ListType.CURRENTLY_READING) {
                hasCurrentlyReading = true;
            }
        }
        
        // Create missing default lists
        BookList wantToRead = null;
        BookList alreadyRead = null;
        BookList currentlyReading = null;
        
        if (!hasWantToRead) {
            wantToRead = new BookList("Do przeczytania", BookList.ListType.WANT_TO_READ, true);
            bookLists.put(wantToRead.getId(), wantToRead);
        }
        
        if (!hasAlreadyRead) {
            alreadyRead = new BookList("Przeczytane", BookList.ListType.ALREADY_READ, true);
            bookLists.put(alreadyRead.getId(), alreadyRead);
        }
        
        if (!hasCurrentlyReading) {
            currentlyReading = new BookList("Obecnie czytam", BookList.ListType.CURRENTLY_READING, true);
            bookLists.put(currentlyReading.getId(), currentlyReading);
        }
        
        // Add sample books only if this is the first time initialization
        if (books.isEmpty() && wantToRead != null && alreadyRead != null && currentlyReading != null) {
            addSampleBooks(wantToRead, alreadyRead, currentlyReading);
        }
        
        // Save if any changes were made
        if (!hasWantToRead || !hasAlreadyRead || !hasCurrentlyReading) {
            saveData();
        }
    }

    private void addSampleBooks(BookList wantToRead, BookList alreadyRead, BookList currentlyReading) {
        // Sample book 1 - Currently Reading
        Book book1 = new Book("Wiedźmin: Ostatnie życzenie", "Andrzej Sapkowski");
        book1.setDescription("Zbiór opowiadań o wiedźminie Geralcie z Rivii. Pierwsza książka z serii o Wiedźminie.");
        book1.setTotalPages(332);
        book1.setCurrentPage(150);
        book1.setStatus(Book.ReadingStatus.CURRENTLY_READING);
        books.put(book1.getId(), book1);
        currentlyReading.addBook(book1.getId());

        // Sample book 2 - Want to Read
        Book book2 = new Book("Solaris", "Stanisław Lem");
        book2.setDescription("Klasyka polskiej fantastyki naukowej. Opowieść o kontakcie z obcą inteligencją.");
        book2.setTotalPages(296);
        book2.setStatus(Book.ReadingStatus.WANT_TO_READ);
        books.put(book2.getId(), book2);
        wantToRead.addBook(book2.getId());

        // Sample book 3 - Already Read
        Book book3 = new Book("Lalka", "Bolesław Prus");
        book3.setDescription("Klasyka polskiej literatury. Historia Stanisława Wokulskiego i jego miłości do Izabeli Łęckiej.");
        book3.setTotalPages(736);
        book3.setCurrentPage(736);
        book3.setStatus(Book.ReadingStatus.ALREADY_READ);
        books.put(book3.getId(), book3);
        alreadyRead.addBook(book3.getId());

        // Sample book 4 - Want to Read
        Book book4 = new Book("Cyberpunk 2077: Bez przyszłości", "Marek S. Huberath");
        book4.setDescription("Opowiadania z uniwersum Cyberpunk 2077. Futurystyczne historie o technologii i człowieczeństwie.");
        book4.setTotalPages(248);
        book4.setStatus(Book.ReadingStatus.WANT_TO_READ);
        books.put(book4.getId(), book4);
        wantToRead.addBook(book4.getId());
    }

    // Book methods
    public void addBook(Book book) {
        if (book.getId() == null) {
            book.setId("book_" + System.currentTimeMillis());
        }
        books.put(book.getId(), book);
        saveData();
    }

    public void updateBook(Book book) {
        books.put(book.getId(), book);
        saveData();
    }

    public void deleteBook(String bookId) {
        books.remove(bookId);
        // Remove from all lists
        for (BookList list : bookLists.values()) {
            list.removeBook(bookId);
        }
        saveData();
    }

    public Book getBook(String bookId) {
        return books.get(bookId);
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }

    public List<Book> getBooksInList(String listId) {
        BookList list = bookLists.get(listId);
        if (list == null) return new ArrayList<>();
        
        List<Book> result = new ArrayList<>();
        for (String bookId : list.getBookIds()) {
            Book book = books.get(bookId);
            if (book != null) {
                result.add(book);
            }
        }
        return result;
    }

    // BookList methods
    public void addBookList(BookList bookList) {
        if (bookList.getId() == null) {
            bookList.setId("list_" + System.currentTimeMillis());
        }
        bookLists.put(bookList.getId(), bookList);
        saveData();
    }

    public void updateBookList(BookList bookList) {
        bookLists.put(bookList.getId(), bookList);
        saveData();
    }

    public void deleteBookList(String listId) {
        BookList list = bookLists.get(listId);
        if (list != null && !list.isDefault()) {
            bookLists.remove(listId);
            saveData();
        }
    }

    public BookList getBookList(String listId) {
        return bookLists.get(listId);
    }

    public List<BookList> getAllBookLists() {
        return new ArrayList<>(bookLists.values());
    }

    public List<BookList> getDefaultLists() {
        List<BookList> defaultLists = new ArrayList<>();
        for (BookList list : bookLists.values()) {
            if (list.isDefault()) {
                defaultLists.add(list);
            }
        }
        return defaultLists;
    }

    public List<BookList> getCustomLists() {
        List<BookList> customLists = new ArrayList<>();
        for (BookList list : bookLists.values()) {
            if (!list.isDefault()) {
                customLists.add(list);
            }
        }
        return customLists;
    }

    public void addBookToList(String bookId, String listId) {
        BookList list = bookLists.get(listId);
        if (list != null) {
            list.addBook(bookId);
            saveData();
        }
    }

    public void removeBookFromList(String bookId, String listId) {
        BookList list = bookLists.get(listId);
        if (list != null) {
            list.removeBook(bookId);
            saveData();
        }
    }

    public BookList getWantToReadList() {
        for (BookList list : bookLists.values()) {
            if (list.getType() == BookList.ListType.WANT_TO_READ) {
                return list;
            }
        }
        return null;
    }

    public BookList getAlreadyReadList() {
        for (BookList list : bookLists.values()) {
            if (list.getType() == BookList.ListType.ALREADY_READ) {
                return list;
            }
        }
        return null;
    }

    public BookList getCurrentlyReadingList() {
        for (BookList list : bookLists.values()) {
            if (list.getType() == BookList.ListType.CURRENTLY_READING) {
                return list;
            }
        }
        return null;
    }
}
