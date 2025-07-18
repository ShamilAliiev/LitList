package pl.kalisz.uk.prup.litlist.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import pl.kalisz.uk.prup.litlist.R;
import pl.kalisz.uk.prup.litlist.data.DataManager;
import pl.kalisz.uk.prup.litlist.model.Book;
import pl.kalisz.uk.prup.litlist.model.BookList;

import java.util.Calendar;
import java.util.List;

public class ProfileFragment extends Fragment {

    private DataManager dataManager;
    private TextView booksThisMonth, pagesRead, readingStreak;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        dataManager = DataManager.getInstance(requireContext());
        initViews(view);
        loadStatistics();
        
        return view;
    }

    private void initViews(View view) {
        booksThisMonth = view.findViewById(R.id.books_this_month);
        pagesRead = view.findViewById(R.id.pages_read);
        readingStreak = view.findViewById(R.id.reading_streak);
    }

    private void loadStatistics() {
        // Calculate books this month (for MVP, we'll just show total books read)
        BookList alreadyReadList = dataManager.getAlreadyReadList();
        int booksCount = alreadyReadList != null ? alreadyReadList.getBookCount() : 0;
        booksThisMonth.setText(String.valueOf(booksCount));

        // Calculate total pages read
        List<Book> allBooks = dataManager.getAllBooks();
        int totalPages = 0;
        for (Book book : allBooks) {
            if (book.getStatus() == Book.ReadingStatus.ALREADY_READ) {
                totalPages += book.getTotalPages();
            } else {
                totalPages += book.getCurrentPage();
            }
        }
        pagesRead.setText(String.valueOf(totalPages));

        // For MVP, we'll show a static reading streak
        readingStreak.setText("7");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dataManager != null) {
            loadStatistics();
        }
    }
}
