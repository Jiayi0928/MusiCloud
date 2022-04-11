package edu.neu.madcourse.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private EditText keyword;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        keyword = findViewById(R.id.keyword_input);
        searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(view -> searchResult(keyword.getText().toString()));
    }

    private void searchResult(String s){
        Intent intent = new Intent(MainActivity.this, Search_Results_Activity.class);
        intent.putExtra("keyword I need",s);
        startActivity(intent);
    }
}