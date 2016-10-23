package com.example.batyamessagingapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.batyamessagingapp.R;
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        final Task task = taskList.get(position);
        holder.dateStartTextView.setText(task.getDateStart());
        holder.dateEndTextView.setText(task.getDateEnd());
        holder.messageTextView.setText(task.getMessage());


        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(task.getChecked());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //set your object's last status
                task.setChecked(isChecked);
            }
        });

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
            dateStartTextView = (TextView) itemView.findViewById(R.id.dateStartTextView);
            dateEndTextView = (TextView) itemView.findViewById(R.id.dateEndTextView);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            checkBox.setChecked(false);
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

    }
}