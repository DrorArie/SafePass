package com.dror.safepass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;
import com.dror.safepass.MainActivity;

import com.dror.safepass.Model.ToDo;
import com.dror.safepass.adapter.ListItemAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
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

        int position = getIntent().getIntExtra("position", 0);

        boolean add = getIntent().getBooleanExtra("add", false);

        mAuth =FirebaseAuth.getInstance();

        if(!add) {
            title.setText(MainActivity.passwordList.get(position).getTitle());
            userName.setText(MainActivity.passwordList.get(position).getUserName());
            password.setText(MainActivity.passwordList.get(position).getPassword());
        }

        Toast.makeText(Pop.this, title.getText(), Toast.LENGTH_SHORT).show();
        MainActivity.db = FirebaseFirestore.getInstance();

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.isUpdate)
                    {
                    Toast.makeText(Pop.this, "added!", Toast.LENGTH_SHORT).show();
                    setData(title.getText().toString(),userName.getText().toString(),password.getText().toString());
                }
                else
                 {
                    Toast.makeText(Pop.this, "Updated!", Toast.LENGTH_SHORT).show();
                    updateData(title.getText().toString(),userName.getText().toString(), password.getText().toString());
                    MainActivity.isUpdate = !MainActivity.isUpdate;
                }
            }
        });

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
