package pl.kalisz.uk.prup.litlist.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import pl.kalisz.uk.prup.litlist.R;
import pl.kalisz.uk.prup.litlist.model.Note;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notes;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    public NotesAdapter(List<Note> notes) {
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.bind(note);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void updateNotes(List<Note> newNotes) {
        this.notes = newNotes;
        notifyDataSetChanged();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView noteContent, noteInfo;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteContent = itemView.findViewById(R.id.note_content);
            noteInfo = itemView.findViewById(R.id.note_info);
        }

        public void bind(Note note) {
            noteContent.setText(note.getContent());
            
            StringBuilder info = new StringBuilder();
            if (note.getPage() > 0) {
                info.append("Strona ").append(note.getPage());
            }
            if (note.getChapter() != null && !note.getChapter().isEmpty()) {
                if (info.length() > 0) info.append(" • ");
                info.append(note.getChapter());
            }
            if (info.length() > 0) info.append(" • ");
            info.append(dateFormat.format(note.getCreatedAt()));
            
            noteInfo.setText(info.toString());
        }
    }
}
