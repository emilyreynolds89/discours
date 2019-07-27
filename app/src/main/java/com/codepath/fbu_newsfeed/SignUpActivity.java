package com.codepath.fbu_newsfeed;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.fbu_newsfeed.Models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class






SignUpActivity extends AppCompatActivity {

    @BindView(R.id.etName) EditText nameInput;
    @BindView(R.id.etUsername) EditText usernameInput;
    @BindView(R.id.etPassword) EditText passwordInput;
    @BindView(R.id.etConfirmPassword) EditText confirmPasswordInput;
    @BindView(R.id.etEmail) EditText emailInput;
    @BindView(R.id.btnSignUp) Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUser();
            }
        });

    }

    private void setUser() {
        String name = nameInput.getText().toString();
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        String email = emailInput.getText().toString();

        if (!password.equals(confirmPassword)) {
            Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }

        ParseUser user = new User();

        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put("fullName", name);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("SignUp", "New user sign up successful");
                    final Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    finish();
                } else {
                    Log.e("SignUp", "Error in new user sign up");
                    Toast.makeText(SignUpActivity.this, "Error in sign up: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
