package com.dror.safepass;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.dror.safepass.Model.ToDo;
import com.dror.safepass.adapter.ListItemAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nullable;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    public static List<ToDo> passwordList = new ArrayList<>();
    public static FirebaseFirestore db;

    public static RecyclerView listItem;
    static RecyclerView.LayoutManager layoutManager;

    static ListItemAdapter adapter;

    static AlertDialog dialog;

    FirebaseAuth mAuth;

    public static boolean isUpdate =false;
    public static String idUpdate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*final LayoutInflater factory = getLayoutInflater();

        final View view = factory.inflate(R.layout.popwindow, null);

        Pop.title = (MaterialEditText) view.findViewById(R.id.platformName);
        Pop.userName = (MaterialEditText) view.findViewById(R.id.userName);
        Pop.password = (MaterialEditText) view.findViewById(R.id.password);*/

        db = FirebaseFirestore.getInstance();

        dialog = new SpotsDialog(this);

        listItem = (RecyclerView)findViewById(R.id.listPasswords);
        listItem.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listItem.setLayoutManager(layoutManager);

        mAuth =FirebaseAuth.getInstance();

        loadData();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);


        ImageButton add = (ImageButton) findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Pop.class);
                intent.putExtra("add", true);
                startActivityForResult(intent, 1);}

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadData();}

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("DELETE"))
            deleteItem(item.getOrder());
        return super.onContextItemSelected(item);
    }

    private void deleteItem(int index) {
        db.collection("passwordsList")
                .document(passwordList.get(index).getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadData();
                    }
                });
    }

    public ArrayList<ToDo> GetPasswordList(){
        return (ArrayList<ToDo>) this.passwordList;
    }

    public FirebaseFirestore GetDB(){
        return this.db;
    }


    public void loadData() {
        dialog.show();
        if(passwordList.size() > 0)
            passwordList.clear();
        db.collection("passwordsList").whereEqualTo("User_id", mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot doc:task.getResult())
                        {
                            ToDo toDo = new ToDo(doc.getString("id"),
                                    "Title: " + doc.getString("title"),
                                    doc.getString("userName"),
                                    doc.getString("password"));
                            passwordList.add(toDo);
                        }
                        adapter = new ListItemAdapter(MainActivity.this,  passwordList);
                        listItem.setAdapter(adapter);
                        dialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();
                    }
                });

    }

    @Override
    protected void  onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuLogout:

                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, FingerPrint.class));

                break;
        }

        return true;
    }
}