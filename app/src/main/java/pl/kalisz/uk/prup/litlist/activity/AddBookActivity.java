package pl.kalisz.uk.prup.litlist.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import pl.kalisz.uk.prup.litlist.R;
import pl.kalisz.uk.prup.litlist.data.DataManager;
import pl.kalisz.uk.prup.litlist.model.Book;
import pl.kalisz.uk.prup.litlist.model.BookList;

public class AddBookActivity extends AppCompatActivity {

    private EditText titleEditText, authorEditText, isbnEditText, pagesEditText, descriptionEditText;
    private Button saveButton;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        // Setup ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.add_book_title);
        }

        dataManager = DataManager.getInstance(this);
        initViews();
        setupListeners();
    }

    private void initViews() {
        titleEditText = findViewById(R.id.title_edit_text);
        authorEditText = findViewById(R.id.author_edit_text);
        isbnEditText = findViewById(R.id.isbn_edit_text);
        pagesEditText = findViewById(R.id.pages_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        saveButton = findViewById(R.id.save_button);
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> saveBook());
    }

    private void saveBook() {
        String title = titleEditText.getText().toString().trim();
        String author = authorEditText.getText().toString().trim();
        String isbn = isbnEditText.getText().toString().trim();
        String pagesStr = pagesEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        // Validation
        if (title.isEmpty()) {
            titleEditText.setError(getString(R.string.title_required));
            titleEditText.requestFocus();
            return;
        }

        if (author.isEmpty()) {
            authorEditText.setError(getString(R.string.author_required));
            authorEditText.requestFocus();
            return;
        }

        // Create book
        Book book = new Book(title, author);
        
        if (!isbn.isEmpty()) {
            book.setIsbn(isbn);
        }
        
        if (!pagesStr.isEmpty()) {
            try {
                int pages = Integer.parseInt(pagesStr);
                book.setTotalPages(pages);
            } catch (NumberFormatException e) {
                // Invalid number, ignore
            }
        }
        
        if (!description.isEmpty()) {
            book.setDescription(description);
        }

        // Save book
        dataManager.addBook(book);
        
        // Add to "Want to Read" list by default
        BookList wantToReadList = dataManager.getWantToReadList();
        if (wantToReadList != null) {
            dataManager.addBookToList(book.getId(), wantToReadList.getId());
        } else {
            // Fallback: create a new "Want to Read" list if it doesn't exist
            BookList newWantToReadList = new BookList("Do przeczytania", BookList.ListType.WANT_TO_READ, true);
            dataManager.addBookList(newWantToReadList);
            dataManager.addBookToList(book.getId(), newWantToReadList.getId());
        }

        Toast.makeText(this, R.string.book_added_successfully, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
