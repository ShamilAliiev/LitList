package pl.kalisz.uk.prup.litlist.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import pl.kalisz.uk.prup.litlist.R;
import pl.kalisz.uk.prup.litlist.model.Bookmark;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.BookmarkViewHolder> {

    private List<Bookmark> bookmarks;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    public BookmarksAdapter(List<Bookmark> bookmarks) {
        this.bookmarks = bookmarks;
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookmark, parent, false);
        return new BookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        Bookmark bookmark = bookmarks.get(position);
        holder.bind(bookmark);
    }

    @Override
    public int getItemCount() {
        return bookmarks.size();
    }

    public void updateBookmarks(List<Bookmark> newBookmarks) {
        this.bookmarks = newBookmarks;
        notifyDataSetChanged();
    }

    class BookmarkViewHolder extends RecyclerView.ViewHolder {
        private TextView bookmarkTitle, bookmarkInfo;

        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            bookmarkTitle = itemView.findViewById(R.id.bookmark_title);
            bookmarkInfo = itemView.findViewById(R.id.bookmark_info);
        }

        public void bind(Bookmark bookmark) {
            StringBuilder title = new StringBuilder("Strona ").append(bookmark.getPage());
            if (bookmark.getChapter() != null && !bookmark.getChapter().isEmpty()) {
                title.append(" • ").append(bookmark.getChapter());
            }
            bookmarkTitle.setText(title.toString());
            
            StringBuilder info = new StringBuilder();
            if (bookmark.getDescription() != null && !bookmark.getDescription().isEmpty()) {
                info.append(bookmark.getDescription()).append(" • ");
            }
            info.append(dateFormat.format(bookmark.getCreatedAt()));
            
            bookmarkInfo.setText(info.toString());
        }
    }
}
