package com.example.batyamessagingapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.batyamessagingapp.util.Message;

import java.util.List;

/**
 * Created by Кашин on 23.10.2016.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.Holder> {

    public static final int DIRECTION_INCOMING = 0;
    public static final int DIRECTION_OUTGOING = 1;

    private List<Message> taskList;


    public MessageAdapter(List<Message> list) {
        this.taskList = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void removeAt(int n) {
        taskList.remove(n);
        notifyItemRemoved(n);
        notifyItemRangeChanged(n, taskList.size());
    }

    class Holder extends RecyclerView.ViewHolder {

        private TextView dateStartTextView;
        private TextView dateEndTextView;
        private TextView messageTextView;
        private CheckBox checkBox;

        public Holder(View itemView) {
            super(itemView);
            checkBox.setChecked(false);
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

    }
}