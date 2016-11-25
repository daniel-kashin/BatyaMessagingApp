package com.example.batyamessagingapp.activity.chat.adapter;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Кашин on 23.11.2016.
 */

public interface OnMessageLongClickListener {
    boolean onItemLongClick(RecyclerView.Adapter adapter, int position);
}