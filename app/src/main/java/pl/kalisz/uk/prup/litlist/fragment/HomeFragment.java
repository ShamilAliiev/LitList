package pl.kalisz.uk.prup.litlist.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pl.kalisz.uk.prup.litlist.R;
import pl.kalisz.uk.prup.litlist.adapter.BookAdapter;
import pl.kalisz.uk.prup.litlist.data.DataManager;
import pl.kalisz.uk.prup.litlist.model.Book;
import pl.kalisz.uk.prup.litlist.model.BookList;

import java.util.List;

public class HomeFragment extends Fragment {

    private DataManager dataManager;
    private TextView statsCurrentlyReading, statsWantToRead, statsAlreadyRead;
    private RecyclerView recentBooksRecyclerView;
    private BookAdapter recentBooksAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        dataManager = DataManager.getInstance(requireContext());
        initViews(view);
        loadData();
        
        return view;
    }

    private void initViews(View view) {
        statsCurrentlyReading = view.findViewById(R.id.stats_currently_reading);
        statsWantToRead = view.findViewById(R.id.stats_want_to_read);
        statsAlreadyRead = view.findViewById(R.id.stats_already_read);
        
        recentBooksRecyclerView = view.findViewById(R.id.recent_books_recycler_view);
        recentBooksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void loadData() {
        loadStats();
        loadRecentBooks();
    }

    private void loadStats() {
        BookList currentlyReading = dataManager.getCurrentlyReadingList();
        BookList wantToRead = dataManager.getWantToReadList();
        BookList alreadyRead = dataManager.getAlreadyReadList();

        if (currentlyReading != null) {
            statsCurrentlyReading.setText(String.valueOf(currentlyReading.getBookCount()));
        }
        if (wantToRead != null) {
            statsWantToRead.setText(String.valueOf(wantToRead.getBookCount()));
        }
        if (alreadyRead != null) {
            statsAlreadyRead.setText(String.valueOf(alreadyRead.getBookCount()));
        }
    }

    private void loadRecentBooks() {
        List<Book> allBooks = dataManager.getAllBooks();
        // Show last 5 books added (for simplicity, we'll just show first 5)
        List<Book> recentBooks = allBooks.size() > 5 ? allBooks.subList(0, 5) : allBooks;
        
        recentBooksAdapter = new BookAdapter(recentBooks, new BookAdapter.OnBookClickListener() {
            @Override
            public void onBookClick(Book book) {
                // Handle book click - navigate to book details
                // For now, we'll leave this empty
            }

            @Override
            public void onBookLongClick(Book book) {
                // Handle long click - do nothing in home fragment
            }
        });
        recentBooksRecyclerView.setAdapter(recentBooksAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dataManager != null) {
            loadData();
        }
    }
}
