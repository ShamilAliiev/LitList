package pl.kalisz.uk.prup.litlist.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import pl.kalisz.uk.prup.litlist.R;
import pl.kalisz.uk.prup.litlist.model.Book;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> books;
    private OnBookClickListener onBookClickListener;

    public interface OnBookClickListener {
        void onBookClick(Book book);
        void onBookLongClick(Book book);
    }

    public BookAdapter(List<Book> books, OnBookClickListener listener) {
        this.books = books;
        this.onBookClickListener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void updateBooks(List<Book> newBooks) {
        this.books = newBooks;
        notifyDataSetChanged();
    }

    class BookViewHolder extends RecyclerView.ViewHolder {
        private ImageView bookCover;
        private TextView bookTitle;
        private TextView bookAuthor;
        private TextView bookProgress;
        private ProgressBar progressBar;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCover = itemView.findViewById(R.id.book_cover);
            bookTitle = itemView.findViewById(R.id.book_title);
            bookAuthor = itemView.findViewById(R.id.book_author);
            bookProgress = itemView.findViewById(R.id.book_progress);
            progressBar = itemView.findViewById(R.id.progress_bar);

            itemView.setOnClickListener(v -> {
                if (onBookClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onBookClickListener.onBookClick(books.get(position));
                    }
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (onBookClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onBookClickListener.onBookLongClick(books.get(position));
                        return true;
                    }
                }
                return false;
            });
        }

        public void bind(Book book) {
            bookTitle.setText(book.getTitle());
            bookAuthor.setText(book.getAuthor());
            
            // Load book cover image
            if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(book.getImageUrl())
                        .placeholder(R.drawable.book_placeholder)
                        .error(R.drawable.book_placeholder)
                        .into(bookCover);
            } else {
                bookCover.setImageResource(R.drawable.book_placeholder);
            }

            // Set progress
            if (book.getTotalPages() > 0) {
                int progress = book.getProgressPercentage();
                progressBar.setProgress(progress);
                bookProgress.setText(book.getProgressText());
                progressBar.setVisibility(View.VISIBLE);
                bookProgress.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
                bookProgress.setVisibility(View.GONE);
            }
        }
    }
}
