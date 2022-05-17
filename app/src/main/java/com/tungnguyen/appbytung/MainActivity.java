package com.tungnguyen.appbytung;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText userNameInput;
    private EditText userEmailInput;
    private EditText userPasswordInput;

    private SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate a SharedPreferencesHelper class
        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        sharedPreferencesHelper = new SharedPreferencesHelper(sharedPreferences);

        // Login input fields
        userNameInput = findViewById(R.id.userNameInput);
        userEmailInput = findViewById(R.id.userEmailInput);
        userPasswordInput = findViewById(R.id.userPasswordInput);

        userNameInput.setText(sharedPreferencesHelper.getEntry("name"));
        userEmailInput.setText(sharedPreferencesHelper.getEntry("email"));
        userPasswordInput.setText(sharedPreferencesHelper.getEntry("password"));

        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(v -> signIn());

        // Get grid
        GridView mainGrid = findViewById(R.id.mainGrid);

        // Create buttons to add to grid
        ArrayList<Button> buttonList = new ArrayList<>();
        String[] buttonNames = {"Movies", "Cameras", "Google Map", "Traffic", "Music", "Food" };
        for(String buttonName : buttonNames) {
            Button newBtn = new Button(this);
            newBtn.setText(buttonName);
            buttonList.add(newBtn);
        }

        // Add buttons to grid by using custom adapter
        ArrayAdapter<Button> buttonArrayAdapter = new CustomButtonAdapter(this, buttonList);
        mainGrid.setAdapter(buttonArrayAdapter);
    }

    private void signIn() {
        String userName = userNameInput.getText().toString();
        String userEmail = userEmailInput.getText().toString();
        String userPassword = userPasswordInput.getText().toString();

        if (!formIsValid(userName, userEmail, userPassword)) {
            return;
        }

        // store shared preferences
        sharedPreferencesHelper.saveEntry("name", userName);
        sharedPreferencesHelper.saveEntry("email", userEmail);
        sharedPreferencesHelper.saveEntry("password", userPassword);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // update profile. name is the value entered in UI
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(userName)
                        .build();

                    if (user != null) {
                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    startActivity(new Intent(MainActivity.this, FirebaseActivity.class));
                                }
                            });
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong...\nOR your login credentials are incorrect!",
                            Toast.LENGTH_SHORT).show();
                }
            });
    }

    private boolean formIsValid(String userName, String userEmail, String userPassword) {
        boolean result = true;
        if (TextUtils.isEmpty(userName)) {
            userNameInput.setError("Required");
            result = false;
        } else {
            userNameInput.setError(null);
        }

        if (TextUtils.isEmpty(userEmail)) {
            userEmailInput.setError("Required");
            result = false;
        } else {
            userEmailInput.setError(null);
        }

        if (TextUtils.isEmpty(userPassword)) {
            userPasswordInput.setError("Required");
            result = false;
        } else {
            userPasswordInput.setError(null);
        }

        return result;
    }
}