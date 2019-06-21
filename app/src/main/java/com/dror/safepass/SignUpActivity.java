package com.dror.safepass;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressBar progressBar;
    EditText editTextEmail, editTextPassword, editTextConfirmedPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextEmail = findViewById(R.id.text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        editTextConfirmedPassword = findViewById(R.id.edit_text_confirm_password);
        progressBar = findViewById(R.id.progressbar);

        mAuth = FirebaseAuth.getInstance(); // getting the current user, in this case null

        // find if one of them has been clicked
        findViewById(R.id.button_sign_up).setOnClickListener(this);
        findViewById(R.id.text_view_login).setOnClickListener(this);

    }

    private void registerUser(){
        String email = editTextEmail.getText().toString().trim(); // remove the spaces
        String password = editTextPassword.getText().toString().trim(); // remove the spaces
        String confirmedPassword = editTextConfirmedPassword.getText().toString().trim(); // remove the spaces

        if(email.isEmpty()){
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("password is required");
            editTextPassword.requestFocus();
            return;
        }
        if(confirmedPassword.isEmpty()){
            editTextConfirmedPassword.setError("Please confirm your password");
            editTextConfirmedPassword.requestFocus();
            return;
        }
        if (!(confirmedPassword.equals(password))){
            editTextConfirmedPassword.setError("Invalid passwords");
            editTextConfirmedPassword.requestFocus();
            return;
        }
        if(password.length() < 8){
            editTextPassword.setError("Minimum length of password should be 8");
            editTextPassword.requestFocus();
            return;
        }
        if(!password.matches(".*[0-9].*")){
            editTextPassword.setError("Password must contain digits");
            editTextPassword.requestFocus();
            return;
        }
        if(!password.matches(".*[a-z].*")){
            editTextPassword.setError("Password must contain letters");
            editTextPassword.requestFocus();
            return;
        }
        if(!password.matches(".*[A-Z].*")){
            editTextPassword.setError("Password must contain uppercase letters");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // create new user with email and password
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);  // invisible the progressbar
                if(task.isSuccessful()){  // if succeeded create new user
                    FirebaseUser user = mAuth.getCurrentUser();
                    user.sendEmailVerification(); // send him verification email
                    Toast.makeText(SignUpActivity.this, "Verification email sent", Toast.LENGTH_SHORT ).show();
                    finish(); // close this screen
                    startActivity(new Intent(SignUpActivity.this, SignInActivity.class)); // start new activity

                }else{  // if not succeeded to create new user
                    if(task.getException() instanceof FirebaseAuthInvalidUserException){  // email already exists in the data base
                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                    }else{  // unknown error
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_sign_up: // if the user signed up
                registerUser();
                break;

            case R.id.text_view_login: // if the user has backed to the login screen
                finish();
                startActivity(new Intent(this, SignInActivity.class));
                break;

        }
    }
}
