package pl.kalisz.uk.prup.litlist.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import pl.kalisz.uk.prup.litlist.R;
import pl.kalisz.uk.prup.litlist.adapter.BookListAdapter;
import pl.kalisz.uk.prup.litlist.data.DataManager;
import pl.kalisz.uk.prup.litlist.model.BookList;

import java.util.ArrayList;
import java.util.List;

public class ListsFragment extends Fragment {

    private DataManager dataManager;
    private RecyclerView defaultListsRecyclerView, customListsRecyclerView;
    private BookListAdapter defaultListsAdapter, customListsAdapter;
    private TextView noCustomListsText;
    private FloatingActionButton fabCreateList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lists, container, false);
        
        dataManager = DataManager.getInstance(requireContext());
        initViews(view);
        loadLists();
        
        return view;
    }

    private void initViews(View view) {
        defaultListsRecyclerView = view.findViewById(R.id.default_lists_recycler_view);
        customListsRecyclerView = view.findViewById(R.id.custom_lists_recycler_view);
        noCustomListsText = view.findViewById(R.id.no_custom_lists_text);
        fabCreateList = view.findViewById(R.id.fab_create_list);

        defaultListsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        customListsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fabCreateList.setOnClickListener(v -> {
            showCreateListDialog();
        });
    }

    private void loadLists() {
        // Load default lists
        List<BookList> defaultLists = dataManager.getDefaultLists();
        defaultListsAdapter = new BookListAdapter(defaultLists, this::onListClick);
        defaultListsRecyclerView.setAdapter(defaultListsAdapter);

        // Load custom lists
        List<BookList> customLists = dataManager.getCustomLists();
        if (customLists.isEmpty()) {
            customListsRecyclerView.setVisibility(View.GONE);
            noCustomListsText.setVisibility(View.VISIBLE);
        } else {
            customListsRecyclerView.setVisibility(View.VISIBLE);
            noCustomListsText.setVisibility(View.GONE);
            customListsAdapter = new BookListAdapter(customLists, this::onListClick);
            customListsRecyclerView.setAdapter(customListsAdapter);
        }
    }

    private void onListClick(BookList bookList) {
        // TODO: Navigate to list detail view
    }

    private void showCreateListDialog() {
        EditText editText = new EditText(getContext());
        editText.setHint("Enter list name");
        editText.setPadding(50, 40, 50, 40);

        new AlertDialog.Builder(getContext())
                .setTitle("Dodaj nową listę")
                .setView(editText)
                .setPositiveButton("Dodaj", (dialog, which) -> {
                    String listName = editText.getText().toString().trim();
                    if (!listName.isEmpty()) {
                        createCustomList(listName);
                    } else {
                        Toast.makeText(getContext(), "Nazwa listy", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Wstecz", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void createCustomList(String listName) {
        BookList newList = new BookList(listName);
        dataManager.addBookList(newList);
        loadLists(); // Refresh the lists
        Toast.makeText(getContext(), "List \"" + listName + "\" created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dataManager != null) {
            loadLists();
        }
    }
}
