package pl.kalisz.uk.prup.litlist.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import pl.kalisz.uk.prup.litlist.R;
import pl.kalisz.uk.prup.litlist.model.BookList;

import java.util.List;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.BookListViewHolder> {

    private List<BookList> bookLists;
    private OnBookListClickListener onBookListClickListener;

    public interface OnBookListClickListener {
        void onBookListClick(BookList bookList);
    }

    public BookListAdapter(List<BookList> bookLists, OnBookListClickListener listener) {
        this.bookLists = bookLists;
        this.onBookListClickListener = listener;
    }

    @NonNull
    @Override
    public BookListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_list, parent, false);
        return new BookListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookListViewHolder holder, int position) {
        BookList bookList = bookLists.get(position);
        holder.bind(bookList);
    }

    @Override
    public int getItemCount() {
        return bookLists.size();
    }

    public void updateBookLists(List<BookList> newBookLists) {
        this.bookLists = newBookLists;
        notifyDataSetChanged();
    }

    class BookListViewHolder extends RecyclerView.ViewHolder {
        private TextView listName;
        private TextView listDescription;
        private TextView bookCount;

        public BookListViewHolder(@NonNull View itemView) {
            super(itemView);
            listName = itemView.findViewById(R.id.list_name);
            listDescription = itemView.findViewById(R.id.list_description);
            bookCount = itemView.findViewById(R.id.book_count);

            itemView.setOnClickListener(v -> {
                if (onBookListClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onBookListClickListener.onBookListClick(bookLists.get(position));
                    }
                }
            });
        }

        public void bind(BookList bookList) {
            listName.setText(bookList.getName());
            
            if (bookList.getDescription() != null && !bookList.getDescription().isEmpty()) {
                listDescription.setText(bookList.getDescription());
                listDescription.setVisibility(View.VISIBLE);
            } else {
                listDescription.setVisibility(View.GONE);
            }

            int count = bookList.getBookCount();
            String countText = count == 1 ? count + " książka" : count + " książek";
            bookCount.setText(countText);
        }
    }
}
