package com.dror.safepass.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dror.safepass.Common.Common;
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
    ImageView item_image;
    TextView item_title;


    public ListItemViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);


        item_title = itemView.findViewById(R.id.item_title);
        item_image = itemView.findViewById(R.id.icon);
        Typeface font = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Chewy.ttf");
        item_title.setTypeface(font);
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
        menu.add(1,1, getAdapterPosition(), "COPY");

    }
}

public class ListItemAdapter extends RecyclerView.Adapter<ListItemViewHolder>{

    MainActivity mainActivity;
    List<ToDo> passwordList;

    int row_index = -1; // Default no row choose

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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {
        // Check for adding the right icon
        String title = passwordList.get(position).getTitle().substring(0, 1).toUpperCase() + passwordList.get(position).getTitle().substring(1);
        holder.item_title.setText("  " + passwordList.get(position).getTitle());
        if (title.indexOf('F') >= 0 && title.indexOf('c') >= 0 && title.indexOf('b') >= 0 && title.indexOf('k') >= 0)
            holder.item_image.setImageResource(R.drawable.facebook);
        else
            if (title.indexOf('G') >= 0 && title.indexOf('t') >= 0 && title.indexOf('h') >= 0 && title.indexOf('b') >= 0)
                holder.item_image.setImageResource(R.drawable.github);
            else
                if (title.indexOf('G') >= 0 && title.indexOf('o') >= 0 && title.indexOf('l') >= 0 && title.indexOf('+') >= 0)
                    holder.item_image.setImageResource(R.drawable.google_plus);
                else
                    if (title.indexOf('I') >= 0 && title.indexOf('n') >= 0 && title.indexOf('g') >= 0 && title.indexOf('m') >= 0)
                        holder.item_image.setImageResource(R.drawable.instagram);
                    else
                        if (title.indexOf('S') >= 0 && title.indexOf('p') >= 0 && title.indexOf('t') >= 0 && title.indexOf('y') >= 0)
                            holder.item_image.setImageResource(R.drawable.spotify);
                        else
                            if (title.indexOf('W') >= 0 && title.indexOf('f') >= 0 && title.indexOf('i') >= 0)
                                holder.item_image.setImageResource(R.drawable.wifi_signal);
                            else
                                holder.item_image.setImageResource(R.drawable.arrow);


        holder.setItemClickListener(new ItemClickListener() {
        @Override
        public void onClick(View view, int position, boolean isLondClick) {
            row_index = position; // Set row index to selected position
            Common.curretToDO = passwordList.get(position); // Set curret item is item selection
            Common.position = position;
            notifyDataSetChanged(); //made effect on recycleView adapter
        }
        });
        if(row_index == position)
            holder.itemView.setBackgroundColor(Color.parseColor("#949CAA"));
        else
            holder.itemView.setBackgroundColor(Color.parseColor("#FF343F4B"));


    }


    @Override
    public int getItemCount() {
        return passwordList.size();
    }
}