package pl.kalisz.uk.prup.litlist.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import pl.kalisz.uk.prup.litlist.R;
import pl.kalisz.uk.prup.litlist.adapter.NotesAdapter;
import pl.kalisz.uk.prup.litlist.adapter.BookmarksAdapter;
import pl.kalisz.uk.prup.litlist.data.DataManager;
import pl.kalisz.uk.prup.litlist.model.Book;
import pl.kalisz.uk.prup.litlist.model.Note;
import pl.kalisz.uk.prup.litlist.model.Bookmark;

public class BookDetailActivity extends AppCompatActivity {

    private Book book;
    private DataManager dataManager;
    
    private ImageView bookCover;
    private TextView bookTitle, bookAuthor, bookPages, bookDescription, progressText;
    private ProgressBar progressBar;
    private Button updateProgressButton, addToListButton;
    private FloatingActionButton fabAddNote, fabAddBookmark;
    private RecyclerView notesRecyclerView, bookmarksRecyclerView;
    private NotesAdapter notesAdapter;
    private BookmarksAdapter bookmarksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // Get book from intent
        book = (Book) getIntent().getSerializableExtra("book");
        if (book == null) {
            finish();
            return;
        }

        // Setup ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(book.getTitle());
        }

        dataManager = DataManager.getInstance(this);
        initViews();
        loadBookDetails();
        setupListeners();
    }

    private void initViews() {
        bookCover = findViewById(R.id.book_cover);
        bookTitle = findViewById(R.id.book_title);
        bookAuthor = findViewById(R.id.book_author);
        bookPages = findViewById(R.id.book_pages);
        bookDescription = findViewById(R.id.book_description);
        progressText = findViewById(R.id.progress_text);
        progressBar = findViewById(R.id.progress_bar);
        updateProgressButton = findViewById(R.id.update_progress_button);
        addToListButton = findViewById(R.id.add_to_list_button);
        fabAddNote = findViewById(R.id.fab_add_note);
        fabAddBookmark = findViewById(R.id.fab_add_bookmark);
        notesRecyclerView = findViewById(R.id.notes_recycler_view);
        bookmarksRecyclerView = findViewById(R.id.bookmarks_recycler_view);

        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookmarksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadBookDetails() {
        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        
        if (book.getTotalPages() > 0) {
            bookPages.setText(book.getTotalPages() + " stron");
            progressText.setText(book.getProgressText());
            progressBar.setProgress(book.getProgressPercentage());
            progressBar.setVisibility(View.VISIBLE);
            progressText.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            progressText.setVisibility(View.GONE);
            bookPages.setText("Nie ustawiono liczby stron");
        }

        if (book.getDescription() != null && !book.getDescription().isEmpty()) {
            bookDescription.setText(book.getDescription());
            bookDescription.setVisibility(View.VISIBLE);
        } else {
            bookDescription.setVisibility(View.GONE);
        }

        // Load book cover
        if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(book.getImageUrl())
                    .placeholder(R.drawable.book_placeholder)
                    .error(R.drawable.book_placeholder)
                    .into(bookCover);
        } else {
            bookCover.setImageResource(R.drawable.book_placeholder);
        }

        // Load notes and bookmarks
        notesAdapter = new NotesAdapter(book.getNotes());
        notesRecyclerView.setAdapter(notesAdapter);

        bookmarksAdapter = new BookmarksAdapter(book.getBookmarks());
        bookmarksRecyclerView.setAdapter(bookmarksAdapter);
    }

    private void setupListeners() {
        updateProgressButton.setOnClickListener(v -> showUpdateProgressDialog());
        addToListButton.setOnClickListener(v -> showAddToListDialog());
        fabAddNote.setOnClickListener(v -> showAddNoteDialog());
        fabAddBookmark.setOnClickListener(v -> showAddBookmarkDialog());
    }

    private void showUpdateProgressDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_progress, null);
        EditText currentPageEdit = dialogView.findViewById(R.id.current_page_edit);
        
        currentPageEdit.setText(String.valueOf(book.getCurrentPage()));

        new AlertDialog.Builder(this)
                .setTitle("Aktualizuj postęp")
                .setView(dialogView)
                .setPositiveButton("Zapisz", (dialog, which) -> {
                    String pageStr = currentPageEdit.getText().toString().trim();
                    if (!pageStr.isEmpty()) {
                        try {
                            int currentPage = Integer.parseInt(pageStr);
                            book.setCurrentPage(currentPage);
                            dataManager.updateBook(book);
                            loadBookDetails();
                            Toast.makeText(this, "Postęp zaktualizowany", Toast.LENGTH_SHORT).show();
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Nieprawidłowy numer strony", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Anuluj", null)
                .show();
    }

    private void showAddToListDialog() {
        // For MVP, we'll just show a simple toast
        Toast.makeText(this, "Funkcja dodawania do list będzie dostępna wkrótce", Toast.LENGTH_SHORT).show();
    }

    private void showAddNoteDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_note, null);
        EditText pageEdit = dialogView.findViewById(R.id.page_edit);
        EditText chapterEdit = dialogView.findViewById(R.id.chapter_edit);
        EditText contentEdit = dialogView.findViewById(R.id.content_edit);

        new AlertDialog.Builder(this)
                .setTitle("Dodaj notatkę")
                .setView(dialogView)
                .setPositiveButton("Zapisz", (dialog, which) -> {
                    String pageStr = pageEdit.getText().toString().trim();
                    String chapter = chapterEdit.getText().toString().trim();
                    String content = contentEdit.getText().toString().trim();
                    
                    if (!content.isEmpty()) {
                        Note note = new Note();
                        note.setContent(content);
                        
                        if (!pageStr.isEmpty()) {
                            try {
                                note.setPage(Integer.parseInt(pageStr));
                            } catch (NumberFormatException e) {
                                // Invalid page number, ignore
                            }
                        }
                        
                        if (!chapter.isEmpty()) {
                            note.setChapter(chapter);
                        }
                        
                        book.addNote(note);
                        dataManager.updateBook(book);
                        notesAdapter.updateNotes(book.getNotes());
                        Toast.makeText(this, R.string.note_added_successfully, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Anuluj", null)
                .show();
    }

    private void showAddBookmarkDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_bookmark, null);
        EditText pageEdit = dialogView.findViewById(R.id.page_edit);
        EditText chapterEdit = dialogView.findViewById(R.id.chapter_edit);
        EditText descriptionEdit = dialogView.findViewById(R.id.description_edit);

        new AlertDialog.Builder(this)
                .setTitle("Dodaj zakładkę")
                .setView(dialogView)
                .setPositiveButton("Zapisz", (dialog, which) -> {
                    String pageStr = pageEdit.getText().toString().trim();
                    String chapter = chapterEdit.getText().toString().trim();
                    String description = descriptionEdit.getText().toString().trim();
                    
                    Bookmark bookmark = new Bookmark();
                    
                    if (!pageStr.isEmpty()) {
                        try {
                            bookmark.setPage(Integer.parseInt(pageStr));
                        } catch (NumberFormatException e) {
                            bookmark.setPage(book.getCurrentPage());
                        }
                    } else {
                        bookmark.setPage(book.getCurrentPage());
                    }
                    
                    if (!chapter.isEmpty()) {
                        bookmark.setChapter(chapter);
                    }
                    
                    if (!description.isEmpty()) {
                        bookmark.setDescription(description);
                    }
                    
                    book.addBookmark(bookmark);
                    dataManager.updateBook(book);
                    bookmarksAdapter.updateBookmarks(book.getBookmarks());
                    Toast.makeText(this, R.string.bookmark_added_successfully, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Anuluj", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_detail_menu, menu);
        return true;
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Usuń książkę")
                .setMessage("Czy na pewno chcesz usunąć książkę \"" + book.getTitle() + "\"? Ta akcja jest nieodwracalna.")
                .setPositiveButton("Usuń", (dialog, which) -> {
                    dataManager.deleteBook(book.getId());
                    Toast.makeText(this, R.string.book_deleted_successfully, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Anuluj", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_delete_book) {
            showDeleteConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
