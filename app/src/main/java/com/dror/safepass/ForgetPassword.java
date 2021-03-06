package com.dror.safepass;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class ForgetPassword extends AppCompatActivity {

    EditText editTextEmail;
    ProgressBar progressbar;
    TextView back_label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        TextView reset = findViewById(R.id.button_reset_password);
        editTextEmail = findViewById(R.id.text_email);
        progressbar = findViewById(R.id.progressbar);
        back_label = findViewById(R.id.text_back);

        // if ask for being back to the login screen
        back_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(ForgetPassword.this, SignInActivity.class));
            }
        });

        reset.setOnClickListener(new View.OnClickListener() { // check validation of email
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    editTextEmail.setError("Email is required");
                    editTextEmail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError("Please enter a valid email");
                    editTextEmail.requestFocus();
                    return;
                }
                progressbar.setVisibility(View.VISIBLE);

                FirebaseAuth.getInstance()
                        .sendPasswordResetEmail(email) // send email to the user
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressbar.setVisibility(View.GONE); // vanish the progressbar
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgetPassword.this, "Please check your email", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ForgetPassword.this, SignInActivity.class));
                                }
                                else
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        });
            }
        });
    }
}
