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
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity  implements View.OnClickListener {

    FirebaseAuth mAuth;
    EditText editTextEmail , editTextPassword;
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        progressbar = findViewById(R.id.progressbar);

        findViewById(R.id.text_view_register).setOnClickListener(this);
        findViewById(R.id.button_sign_in).setOnClickListener(this);
        findViewById(R.id.text_view_forget_password).setOnClickListener(this);
    }

    private void userLogin() {


        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

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



        progressbar.setVisibility(View.VISIBLE); // the progressbar now can be seen

        // connected to exist user with email and password
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressbar.setVisibility(View.GONE); // vanish the progressbar
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser(); // set me to my user and connect me to my account

                    if(user.isEmailVerified()){ // if email verified
                        finish();
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent); // open my account

                    }else{  // email not verified, have to verify email first
                        Toast.makeText(SignInActivity.this, "Email not verified", Toast.LENGTH_SHORT).show();
                    }
                }else{  // unknown error occur
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null)
        {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }*/
/* dick */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.text_view_register:
                finish();
                startActivity(new Intent(this, SignUpActivity.class ));
                break;

            case R.id.text_view_forget_password:
                finish();
                startActivity(new Intent(this, ForgetPassword.class ));
                break;

            case R.id.button_sign_in:
                userLogin();
                break;


        }

    }
}
