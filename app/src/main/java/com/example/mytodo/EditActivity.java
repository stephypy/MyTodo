package com.example.mytodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity {

    EditText newTodoText;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // References to UI components of the app
        newTodoText = findViewById(R.id.editTextSave);
        saveButton = findViewById(R.id.saveButton);

        getSupportActionBar().setTitle("Edit item");

        newTodoText.setText(getIntent().getStringExtra(MainActivity.KEY_ITEM_TEXT));

        // When user is done editing
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent
                Intent intent = new Intent();

                // Pass the data
                intent.putExtra(MainActivity.KEY_ITEM_TEXT, newTodoText.getText().toString());
                intent.putExtra(MainActivity.KEY_ITEM_POSITION, getIntent().getExtras().getInt(MainActivity.KEY_ITEM_POSITION));

                // Set the result of the intent
                setResult(RESULT_OK, intent);

                // Finish the activity
                finish();
            }
        });
    }
}
