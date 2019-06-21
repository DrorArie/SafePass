package com.dror.safepass;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

public class Pop extends Activity{

    FirebaseAuth mAuth;

    FloatingActionButton fab;

    public MaterialEditText title, userName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.popwindow);
        super.onCreate(savedInstanceState);

        title = (MaterialEditText) findViewById(R.id.platformName);
        userName = (MaterialEditText) findViewById(R.id.userName);
        password = (MaterialEditText) findViewById(R.id.password);

        int position = getIntent().getIntExtra("position", 0);  // gets the position of item from the MainActivity

        boolean add = getIntent().getBooleanExtra("add", false);  // gets if the user wanna add or edit item

        final boolean view = getIntent().getBooleanExtra("view", false); // gets if the user just want to view the item's details

        mAuth =FirebaseAuth.getInstance();

        fab = (FloatingActionButton)findViewById(R.id.fab);

        if(view){  // if the user wanna watch the item details, sets all the editTexts to disabled
            // and switch the save button with back button
            title.setEnabled(false);
            userName.setEnabled(false);
            password.setEnabled(false);
            fab.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        }

        if(!add) {  // if edit' load the item's details
            title.setText(MainActivity.passwordList.get(position).getTitle());
            userName.setText(MainActivity.passwordList.get(position).getUserName());
            password.setText(MainActivity.passwordList.get(position).getPassword());
        }

        MainActivity.db = FirebaseFirestore.getInstance();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(view)
                    finish();
                else // check for validation
                    if(title.getText().toString().equals("")){
                        Toast.makeText(Pop.this, "Title is required", Toast.LENGTH_SHORT).show();
                    }
                    else
                        if(userName.getText().toString().equals(""))
                            Toast.makeText(Pop.this, "User name is required", Toast.LENGTH_SHORT).show();
                        else
                            if(password.getText().toString().equals(""))
                                Toast.makeText(Pop.this, "Password is required", Toast.LENGTH_SHORT).show();
                            else
                                if(!MainActivity.isUpdate && !title.getText().toString().equals("") && !userName.getText().toString().equals("")
                                        && !password.getText().toString().equals(""))
                                {
                                    Toast.makeText(Pop.this, "added!", Toast.LENGTH_SHORT).show();
                                    setData(title.getText().toString(),userName.getText().toString(),password.getText().toString());
                                }
                                else
                                    if (!title.getText().toString().equals("") && !userName.getText().toString().equals("")
                                            && !password.getText().toString().equals(""))
                                    {
                                        Toast.makeText(Pop.this, "Updated!", Toast.LENGTH_SHORT).show();
                                        updateData(title.getText().toString(),userName.getText().toString(), password.getText().toString());
                                        MainActivity.isUpdate = !MainActivity.isUpdate;
                                    }
                }
            });

        // sets the size of the window

        DisplayMetrics dm = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width *.8) , (int)(height*.6));

    }

    private void updateData(String title, String userName, String password) {
        // open the correct collection and item
        MainActivity.db.collection("passwordsList").document(MainActivity.idUpdate)
                // update his details
                .update("title",  Base64.encodeToString(title.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT),
                        "userName", Base64.encodeToString(userName.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT),
                        "password", Base64.encodeToString(password.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Pop.this, "Updated!", Toast.LENGTH_SHORT).show();
                        finish(); // close the pop screen
                    }
                });

        MainActivity.db.collection("passwordsList").document(MainActivity.idUpdate)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {  // check for exceptions in the update
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    }
                });
    }

    private void setData(String title, String userName, String password) {
        String id = UUID.randomUUID().toString(); // create random id for the item
        Map<String, Object> todo = new HashMap<>();
        // Enters the information into the item's fields
        todo.put("User_id", mAuth.getCurrentUser().getUid());
        todo.put("id", id);
        byte[] data = title.getBytes(StandardCharsets.UTF_8);
        String encode = Base64.encodeToString(data, Base64.DEFAULT);
        todo.put("title", encode);
        data = userName.getBytes(StandardCharsets.UTF_8);
        encode = Base64.encodeToString(data, Base64.DEFAULT);
        todo.put("userName",encode);
        data = password.getBytes(StandardCharsets.UTF_8);
        encode = Base64.encodeToString(data, Base64.DEFAULT);
        todo.put("password", encode);

        MainActivity.db.collection("passwordsList").document(id)
                // add the item to the database
                .set(todo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish(); // close the pop screen
            }
        });
    }

}
