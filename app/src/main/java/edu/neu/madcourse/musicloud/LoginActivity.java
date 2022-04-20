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

public class LoginActivity extends AppCompatActivity {
    private DatabaseReference dbReference;
    private DatabaseReference usersDbReference;
    private ValueEventListener valueEventListener;
    private RelativeLayout navBarLayout;
    private ImageView backButton;
    private TextInputEditText inputUsername;
    private TextInputEditText inputPassword;
    private Button loginButton;
    private TextView loginHelperLink; // switch to signup

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Bind views and set on click listeners
        navBarLayout = findViewById(R.id.navbarLogin);
        backButton = navBarLayout.findViewById(R.id.navPrev);
        inputUsername = findViewById(R.id.loginUsernameInput);
        inputPassword = findViewById(R.id.loginPasswordInput);
        loginButton = findViewById(R.id.loginButton);
        loginHelperLink = findViewById(R.id.loginHelperLink);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = inputUsername.getText().toString();
                String password = inputPassword.getText().toString();
                login(username, password);
            }
        });

        loginHelperLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });


        // Retrieve new users directed from Sign Up
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getParcelable("newUser") != null) {
            User user = (User) extras.getParcelable("newUser");
            inputUsername.setText(user.getUsername());
            inputPassword.setText(user.getPassword());
        }

        // Set up database
        dbReference = FirebaseDatabase.getInstance().getReference();
        usersDbReference = dbReference.child("users");
    }


    /**
     * Validates the input username and password with db user records and log in.
     * Shows error message if no matched users were found.
     * Note: currently only username is being validated
     *
     * @param username
     * @param password
     */
    private void login(String username, String password) {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(username)) {
                    // If user does not exist in DB
                    Toast.makeText(getApplicationContext(), "User does not exist!", Toast.LENGTH_LONG).show();
                } else {
                    User currentUser = snapshot.child(username).getValue(User.class);
                    Intent intent = new Intent(LoginActivity.this, Home.class);
                    intent.putExtra("currentUser", currentUser);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        usersDbReference.addListenerForSingleValueEvent(valueEventListener);

    }

    /**
     * Removes the listener so that login can work whenever the login activity is stopped
     * and resumed
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (valueEventListener != null) {
            usersDbReference.removeEventListener(valueEventListener);
        }
    }



}