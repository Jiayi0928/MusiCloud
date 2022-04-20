package edu.neu.madcourse.musicloud;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {
    private DatabaseReference dbReference;
    private RelativeLayout navBarLayout;
    private ImageView backButton;
    private TextView navBarTitle;
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private Button signupButton;
    private TextView signupHelperLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Bind views and set on click listeners
        navBarLayout = findViewById(R.id.navbarSignup);
        backButton = navBarLayout.findViewById(R.id.navPrev);
        navBarTitle = navBarLayout.findViewById(R.id.navTitle);
        usernameInput = findViewById(R.id.signupUsernameInput);
        passwordInput = findViewById(R.id.signupPasswordInput);
        signupButton = findViewById(R.id.signupButton);
        signupHelperLink = findViewById(R.id.signupHelperLink);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        signupHelperLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Set up navbar
        navBarTitle.setText("SIGN UP");

        // Set up database
        dbReference = FirebaseDatabase.getInstance().getReference();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                signUp(username, password);
            }
        });
    }

    private void signUp(String username, String password) {
        DatabaseReference usersDbReference = dbReference.child("users");

        // Check if username already exists
        usersDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(username)) {
                    String errMessage = "Username already exists. Please try again.";
                    Toast.makeText(getApplicationContext(), errMessage, Toast.LENGTH_LONG).show();
                } else {
                    // TODO: validate password, e.g. capitalization
                    User newUser = new User(username, password);
                    usersDbReference.child(username).setValue(newUser);
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    intent.putExtra("newUser", newUser);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}