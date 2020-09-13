package com.example.mytodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 1; // value is arbitrary because we only have one more activity

    List<String> itemsList;

    Button addButton;
    EditText editTextItem;
    RecyclerView itemsView;

    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // References to UI components of the app
        addButton = findViewById(R.id.addButton);
        editTextItem = findViewById(R.id.editTextItem);
        itemsView = findViewById(R.id.itemsView);

        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                // Delete item from the model
                itemsList.remove(position);

                // Notify the adapter
                itemsAdapter.notifyItemRemoved(position);
                saveItems();
                Toast.makeText(getApplicationContext(), "Todo item deleted!", Toast.LENGTH_SHORT).show();
            }
        };

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                // Create new activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);

                // Pass data being edited
                i.putExtra(KEY_ITEM_TEXT, itemsList.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);

                // Display activity
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };

        itemsAdapter = new ItemsAdapter(itemsList, onLongClickListener, onClickListener);
        itemsView.setAdapter(itemsAdapter);
        itemsView.setLayoutManager(new LinearLayoutManager(this));

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = editTextItem.getText().toString();

                // Trim and do not add empty strings
                if(!todoItem.trim().isEmpty()) {
                    // Add item to the model
                    itemsList.add(todoItem.trim());

                    // Notify adapter that an item is inserted
                    itemsAdapter.notifyItemInserted(itemsList.size() - 1);
                    editTextItem.setText("");
                    saveItems();
                    Toast.makeText(getApplicationContext(), "New todo item added!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Handle the result of the edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            // Retrieve the updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);

            // Extract the original position of the edited item from the position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            // Update the model at the right position with new item text
            itemsList.set(position, itemText);

            // Notify the adapter
            itemsAdapter.notifyItemChanged(position);

            // Persist the changes
            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    // Load items by reading every line of the data file
    private  void loadItems() {
        try {
            itemsList = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            itemsList = new ArrayList<>();
        }
    }

    // Save items by writing them into the data file
    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), itemsList);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
        }
    }
}
