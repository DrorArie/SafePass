package com.dror.safepass;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextUsername, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonSignUp:

                break;

            case R.id.

        }
    }
}
