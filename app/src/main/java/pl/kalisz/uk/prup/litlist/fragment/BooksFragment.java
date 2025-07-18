package pl.kalisz.uk.prup.litlist.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import pl.kalisz.uk.prup.litlist.R;
import pl.kalisz.uk.prup.litlist.activity.AddBookActivity;
import pl.kalisz.uk.prup.litlist.activity.BookDetailActivity;
import pl.kalisz.uk.prup.litlist.adapter.BookAdapter;
import pl.kalisz.uk.prup.litlist.data.DataManager;
import pl.kalisz.uk.prup.litlist.model.Book;

import java.util.List;

public class BooksFragment extends Fragment {

    private DataManager dataManager;
    private RecyclerView booksRecyclerView;
    private BookAdapter bookAdapter;
    private TextView noBooksText;
    private FloatingActionButton fabAddBook;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);
        
        dataManager = DataManager.getInstance(requireContext());
        initViews(view);
        loadBooks();
        
        return view;
    }

    private void initViews(View view) {
        booksRecyclerView = view.findViewById(R.id.books_recycler_view);
        noBooksText = view.findViewById(R.id.no_books_text);
        fabAddBook = view.findViewById(R.id.fab_add_book);

        booksRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        fabAddBook.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddBookActivity.class);
            startActivity(intent);
        });
    }

    private void loadBooks() {
        List<Book> books = dataManager.getAllBooks();
        
        if (books.isEmpty()) {
            booksRecyclerView.setVisibility(View.GONE);
            noBooksText.setVisibility(View.VISIBLE);
        } else {
            booksRecyclerView.setVisibility(View.VISIBLE);
            noBooksText.setVisibility(View.GONE);
            
            bookAdapter = new BookAdapter(books, new BookAdapter.OnBookClickListener() {
                @Override
                public void onBookClick(Book book) {
                    Intent intent = new Intent(getContext(), BookDetailActivity.class);
                    intent.putExtra("book", book);
                    startActivity(intent);
                }

                @Override
                public void onBookLongClick(Book book) {
                    showDeleteConfirmationDialog(book);
                }
            });
            booksRecyclerView.setAdapter(bookAdapter);
        }
    }

    private void showDeleteConfirmationDialog(Book book) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Usuń książkę")
                .setMessage("Czy na pewno chcesz usunąć książkę \"" + book.getTitle() + "\"?")
                .setPositiveButton("Usuń", (dialog, which) -> {
                    dataManager.deleteBook(book.getId());
                    loadBooks(); // Refresh the list
                    // You could also show a snackbar with undo option here
                })
                .setNegativeButton("Anuluj", null)
                .show();
    }

    private void onBookClick(Book book) {
        Intent intent = new Intent(getContext(), BookDetailActivity.class);
        intent.putExtra("book", book);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dataManager != null) {
            loadBooks();
        }
    }
}
