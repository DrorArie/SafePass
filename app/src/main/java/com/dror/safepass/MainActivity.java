package com.dror.safepass;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.dror.safepass.Common.Common;
import com.dror.safepass.Model.ToDo;
import com.dror.safepass.adapter.ListItemAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    public static List<ToDo> passwordList = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static FirebaseFirestore db;

    @SuppressLint("StaticFieldLeak")
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

        db = FirebaseFirestore.getInstance();

        dialog = new SpotsDialog(this);

        listItem = findViewById(R.id.listPasswords);
        listItem.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listItem.setLayoutManager(layoutManager);

        mAuth =FirebaseAuth.getInstance();

        loadData();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);


        ImageButton add = findViewById(R.id.add);
        ImageButton edit = findViewById(R.id.edit);
        ImageButton delete = findViewById(R.id.delete);
        ImageButton copy = findViewById(R.id.copy);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Pop.class);
                intent.putExtra("add", true);
                startActivityForResult(intent, 1);}
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.curretToDO != null)
                {
                    isUpdate = true;
                    idUpdate = Common.curretToDO.getId();
                    Intent intent = new Intent(MainActivity.this, Pop.class);
                    intent.putExtra("position", Common.position);
                    startActivityForResult(intent, 1);}
                else
                    Toast.makeText(MainActivity.this, "Please choose item", Toast.LENGTH_SHORT ).show();
                Common.curretToDO = null;
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.curretToDO != null)
                {
                    final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip =  ClipData.newPlainText(Common.curretToDO.getUserName(), Common.curretToDO.getUserName());
                    clipboard.setPrimaryClip(clip);
                    clip =  ClipData.newPlainText(Common.curretToDO.getPassword(), Common.curretToDO.getPassword());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(MainActivity.this, "Copied!", Toast.LENGTH_SHORT).show();
                    int DISPLAY_LENGTH = 5000;
                    new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        /*  Clear the clipboard. */
                        ClipData data = ClipData.newPlainText("", "");
                        clipboard.setPrimaryClip(data);
                    }
                }, DISPLAY_LENGTH);
                }
                else
                    Toast.makeText(MainActivity.this, "Please choose item", Toast.LENGTH_SHORT).show();
                Common.curretToDO = null;
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.curretToDO != null)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure you want to delete this?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            deleteItem(Common.position);}});

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Do nothing
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else
                    Toast.makeText(MainActivity.this, "Please choose item", Toast.LENGTH_SHORT).show();
                Common.curretToDO = null;
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadData();}

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        if(item.getTitle().equals("DELETE")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("Confirm");
            builder.setMessage("Are you sure you want to delete this?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    Common.curretToDO = null;
                    deleteItem(item.getOrder());
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
        if(item.getTitle().equals("COPY"))
        {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip =  ClipData.newPlainText(passwordList.get(item.getOrder()).getUserName(), passwordList.get(item.getOrder()).getUserName());
            clipboard.setPrimaryClip(clip);
            clip =  ClipData.newPlainText(passwordList.get(item.getOrder()).getPassword(), passwordList.get(item.getOrder()).getPassword());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(MainActivity.this, "Copied!", Toast.LENGTH_SHORT).show();
        }
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


    public void loadData() {
        dialog.show();
        if(passwordList.size() > 0)
            passwordList.clear();
        db.collection("passwordsList").whereEqualTo("User_id", mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //byte[] data = Base64.decode(, Base64.DEFAULT);
                        //String text = new String(data, StandardCharsets.UTF_8);
                        for (DocumentSnapshot doc:task.getResult())
                        {
                            ToDo toDo = new ToDo(doc.getString("id"),
                                    new String(Base64.decode(doc.getString("title"), Base64.DEFAULT), StandardCharsets.UTF_8),
                                    new String(Base64.decode(doc.getString("userName"), Base64.DEFAULT), StandardCharsets.UTF_8),
                                    new String(Base64.decode(doc.getString("password"), Base64.DEFAULT), StandardCharsets.UTF_8));
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
                startActivity(new Intent(this, SignInActivity.class));

                break;
        }

        return true;
    }
}
