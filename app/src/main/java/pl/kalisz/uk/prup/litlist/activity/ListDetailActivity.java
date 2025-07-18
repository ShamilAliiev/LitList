package pl.kalisz.uk.prup.litlist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import pl.kalisz.uk.prup.litlist.R;
import pl.kalisz.uk.prup.litlist.adapter.BookAdapter;
import pl.kalisz.uk.prup.litlist.data.DataManager;
import pl.kalisz.uk.prup.litlist.model.Book;
import pl.kalisz.uk.prup.litlist.model.BookList;

public class ListDetailActivity extends AppCompatActivity {
    
    public static final String EXTRA_LIST_ID = "extra_list_id";
    
    private DataManager dataManager;
    private BookList bookList;
    private List<Book> booksInList;
    
    private RecyclerView booksRecyclerView;
    private TextView noBooksTextView;
    private FloatingActionButton fabAddBook;
    
    private BookAdapter bookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);

        // Set status bar color to dark color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(0xFF000000); // Pure black
            getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // Ensure status bar icons are white on dark background
            getWindow().getDecorView().setSystemUiVisibility(0);
        }

        dataManager = DataManager.getInstance(this);
        
        // Get list ID from intent
        String listId = getIntent().getStringExtra(EXTRA_LIST_ID);
        if (listId == null) {
            finish();
            return;
        }
        
        bookList = dataManager.getBookList(listId);
        if (bookList == null) {
            finish();
            return;
        }

        initViews();
        setupToolbar();
        loadBooksInList();
        setupClickListeners();
    }

    private void initViews() {
        booksRecyclerView = findViewById(R.id.books_recycler_view);
        noBooksTextView = findViewById(R.id.no_books_text);
        fabAddBook = findViewById(R.id.fab_add_book);
        
        // Setup RecyclerView
        booksRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(bookList.getName());
        }
    }

    private void loadBooksInList() {
        // Load books
        booksInList = dataManager.getBooksInList(bookList.getId());
        
        if (booksInList.isEmpty()) {
            booksRecyclerView.setVisibility(View.GONE);
            noBooksTextView.setVisibility(View.VISIBLE);
        } else {
            booksRecyclerView.setVisibility(View.VISIBLE);
            noBooksTextView.setVisibility(View.GONE);
            
            bookAdapter = new BookAdapter(booksInList, new BookAdapter.OnBookClickListener() {
                @Override
                public void onBookClick(Book book) {
                    ListDetailActivity.this.onBookClick(book);
                }

                @Override
                public void onBookLongClick(Book book) {
                    showBookOptionsDialog(book);
                }
            });
            booksRecyclerView.setAdapter(bookAdapter);
        }
    }

    private void setupClickListeners() {
        fabAddBook.setOnClickListener(v -> showAddBookDialog());
    }

    private void onBookClick(Book book) {
        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra("book", book);
        startActivity(intent);
    }

    private void showAddBookDialog() {
        List<Book> allBooks = dataManager.getAllBooks();
        List<Book> availableBooks = new ArrayList<>();
        
        // Filter out books already in this list
        for (Book book : allBooks) {
            if (!bookList.getBookIds().contains(book.getId())) {
                availableBooks.add(book);
            }
        }
        
        if (availableBooks.isEmpty()) {
            Toast.makeText(this, "Wszystkie książki są już na tej liście", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String[] bookTitles = new String[availableBooks.size()];
        for (int i = 0; i < availableBooks.size(); i++) {
            Book book = availableBooks.get(i);
            bookTitles[i] = book.getTitle() + " - " + book.getAuthor();
        }
        
        boolean[] checkedItems = new boolean[availableBooks.size()];
        
        new AlertDialog.Builder(this)
                .setTitle("Wybierz książki do dodania")
                .setMultiChoiceItems(bookTitles, checkedItems, (dialog, which, isChecked) -> {
                    checkedItems[which] = isChecked;
                })
                .setPositiveButton("Dodaj", (dialog, which) -> {
                    List<Book> selectedBooks = new ArrayList<>();
                    for (int i = 0; i < checkedItems.length; i++) {
                        if (checkedItems[i]) {
                            selectedBooks.add(availableBooks.get(i));
                        }
                    }
                    addBooksToList(selectedBooks);
                })
                .setNegativeButton("Anuluj", null)
                .show();
    }
    
    private void addBooksToList(List<Book> books) {
        for (Book book : books) {
            dataManager.addBookToList(book.getId(), bookList.getId());
        }
        
        if (!books.isEmpty()) {
            Toast.makeText(this, "Dodano " + books.size() + " książek do listy", Toast.LENGTH_SHORT).show();
            loadBooksInList(); // Refresh the view
        }
    }
    
    private void showBookOptionsDialog(Book book) {
        String[] options = {"Usuń z listy", "Szczegóły książki"};
        
        new AlertDialog.Builder(this)
                .setTitle(book.getTitle())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Remove from list
                            removeBookFromList(book);
                            break;
                        case 1: // Book details
                            onBookClick(book);
                            break;
                    }
                })
                .show();
    }
    
    private void removeBookFromList(Book book) {
        new AlertDialog.Builder(this)
                .setTitle("Usuń z listy")
                .setMessage("Czy na pewno chcesz usunąć \"" + book.getTitle() + "\" z tej listy?")
                .setPositiveButton("Usuń", (dialog, which) -> {
                    dataManager.removeBookFromList(book.getId(), bookList.getId());
                    Toast.makeText(this, "Książka została usunięta z listy", Toast.LENGTH_SHORT).show();
                    loadBooksInList(); // Refresh the view
                })
                .setNegativeButton("Anuluj", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadBooksInList(); // Refresh when returning from book detail
    }
}
