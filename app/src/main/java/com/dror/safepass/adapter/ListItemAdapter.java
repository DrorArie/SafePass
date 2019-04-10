package com.dror.safepass.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dror.safepass.MainActivity;
import com.dror.safepass.Pop;
import com.dror.safepass.Model.ToDo;
import com.dror.safepass.R;
import com.dror.safepass.Splash;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.Console;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    ItemClickListener itemClickListener;
    TextView item_title, item_userName, item_password;


    public ListItemViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);


        item_title = itemView.findViewById(R.id.item_title);
        item_userName = itemView.findViewById(R.id.item_user_name);
        item_password = itemView.findViewById(R.id.item_password);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select the action");
        menu.add(0,0, getAdapterPosition(), "DELETE");

    }
}

public class ListItemAdapter extends RecyclerView.Adapter<ListItemViewHolder>{

    MainActivity mainActivity;
    List<ToDo> passwordList;

    public ListItemAdapter(MainActivity mainActivity, List<ToDo> passwordList) {
        this.mainActivity = mainActivity;
        this.passwordList = passwordList;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mainActivity.getBaseContext());
        View view = inflater.inflate(R.layout.list_item,viewGroup, false);
        return new ListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {

        holder.item_title.setText(passwordList.get(position).getTitle());
        holder.item_userName.setText(passwordList.get(position).getUserName());
        holder.item_password.setText(passwordList.get(position).getPassword());

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLondClick) {

                MainActivity.isUpdate = true;
                MainActivity.idUpdate = passwordList.get(position).getId();
                Intent intent = new Intent(mainActivity, Pop.class);
                intent.putExtra("position", position);
                mainActivity.startActivityForResult(intent, 1);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mainActivity.onActivityResult(requestCode, resultCode, data);
        mainActivity.loadData();}


    @Override
    public int getItemCount() {
        return passwordList.size();
    }
}
