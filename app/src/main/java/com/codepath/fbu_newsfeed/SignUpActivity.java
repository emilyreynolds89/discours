package com.codepath.fbu_newsfeed;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseUser;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameInput;
    private EditText usernameInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private EditText emailInput;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameInput = findViewById(R.id.etName);
        usernameInput = findViewById(R.id.etUsername);
        passwordInput = findViewById(R.id.etPassword);
        confirmPasswordInput = findViewById(R.id.etConfirmPassword);
        emailInput = findViewById(R.id.etEmail);
        signupButton = findViewById(R.id.btnSignUp);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUser();
            }
        });

    }

    protected void setUser() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        String email = emailInput.getText().toString();

        if (!password.equals(confirmPassword)) {
            Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }

        ParseUser user = new ParseUser();

        user.setUsername(username);


    }

}
