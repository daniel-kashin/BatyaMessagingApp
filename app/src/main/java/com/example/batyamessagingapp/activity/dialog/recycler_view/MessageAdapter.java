package com.example.batyamessagingapp.activity.dialog.recycler_view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.batyamessagingapp.R;

import java.util.List;

/**
 * Created by Кашин on 23.10.2016.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> messageList;
    private Context context;

    public MessageAdapter(Context context, List<Message> messageList) {
        this.messageList = messageList;
        this.context = context;
    }

    public void addMessage(String message, Message.Direction direction){
        messageList.add(new Message(message, direction));
        notifyItemInserted(messageList.size() - 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        Message.Direction direction;
        if (viewType == Message.Direction.Incoming.getIntValue()){
            layout = R.layout.message_incoming;
            direction = Message.Direction.Incoming;
        } else {
            layout = R.layout.message_outcoming;
            direction = Message.Direction.Outcoming;
        }

        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(v,direction);
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getDirection().getIntValue();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Message message = messageList.get(position);

        if (viewHolder.getDirection() == messageList.get(position).getDirection()){
            viewHolder.setMessage(message.getMessageText());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView messageTextView;
        private Message.Direction direction;

        public ViewHolder(View itemView, Message.Direction direction) {
            super(itemView);
            messageTextView = (TextView)itemView.findViewById(R.id.messageTextView);
            this.direction = direction;
        }

        public void setMessage(String message) {
            if (messageTextView == null) return;
            messageTextView.setText(message);
        }

        public Message.Direction getDirection(){
            return direction;
        }
    }

}