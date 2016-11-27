package com.example.batyamessagingapp.activity.chat.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.lib.TimestampHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Кашин on 23.10.2016.
 */

public class ChatMessageAdapter
        extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> implements MessagesDataModel{

    private ArrayList<ChatMessage> mChatMessageList;
    private final Context mContext;

    private OnMessageLongClickListener onMessageLongClickListener;
    private OnMessageClickListener onMessageClickListener;


    public ChatMessageAdapter(Context context, ArrayList<ChatMessage> chatMessageList) {
        mChatMessageList = chatMessageList;
        mContext = context;
    }

    public ChatMessageAdapter(Context context){
        this(context,new ArrayList<ChatMessage>());
    }

    @Override
    public void addMessage(ChatMessage chatMessage) {
        mChatMessageList.add(chatMessage);
        notifyItemInserted(mChatMessageList.size() - 1);
    }

    @Override
    public void addMessages(List<ChatMessage> messages){
        for (ChatMessage message : messages){
            mChatMessageList.add(message);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getSize(){
        return getItemCount();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        ChatMessage.Direction direction;
        if (viewType == ChatMessage.Direction.Incoming.getIntValue()) {
            layout = R.layout.message_incoming;
            direction = ChatMessage.Direction.Incoming;
        } else {
            layout = R.layout.message_outcoming;
            direction = ChatMessage.Direction.Outcoming;
        }

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);

        return new ViewHolder(view, direction);
    }

    @Override
    public int getItemViewType(int position) {
        return mChatMessageList.get(position).getDirection().getIntValue();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        //get chatMessage
        final ChatMessage chatMessage = mChatMessageList.get(position);

        //set text
        if (viewHolder.getDirection() == mChatMessageList.get(position).getDirection()) {
            viewHolder.setMessage(chatMessage.getMessageText());
            viewHolder.setTime(chatMessage.getTimeText());
        }

        //create alert dialog
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(new ContextThemeWrapper(mContext,
                        android.R.style.Theme_DeviceDefault_Light_Dialog));
        builder.setTitle("ChatMessage");

        final ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(mContext, android.R.layout.select_dialog_item);
        arrayAdapter.add("Copy");
        builder.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = arrayAdapter.getItem(which);
                        if (name.equals("Copy")){
                            ClipboardManager clipboardManager = (ClipboardManager) mContext
                                    .getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("chatMessage", chatMessage.getMessageText());
                            clipboardManager.setPrimaryClip(clip);
                        }
                    }
                });

        final AlertDialog alert= builder.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //show alertDialog on click
        viewHolder.setMessageTextViewOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChatMessageList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView messageTextView;
        private TextView timeTextView;

        private ChatMessage.Direction direction;

        private ViewHolder(View view, ChatMessage.Direction direction) {
            super(view);

            this.direction = direction;

            messageTextView = (TextView) view.findViewById(R.id.messageTextView);
            timeTextView = (TextView) view.findViewById(R.id.timeTextView);
        }

        public void setMessage(String message) {
            if (messageTextView != null) messageTextView.setText(message);
        }

        public void setTime(String time){
            if (timeTextView != null) timeTextView.setText(time);
        }

        private void setMessageTextViewOnClickListener(View.OnClickListener listener){
            messageTextView.setOnClickListener(listener);
        }

        private ChatMessage.Direction getDirection() {
            return direction;
        }
    }

}