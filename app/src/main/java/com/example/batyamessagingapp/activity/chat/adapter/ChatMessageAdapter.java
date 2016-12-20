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
        extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> implements MessagesDataModel {

    private ArrayList<ChatMessage> mChatMessageList;
    private Context mContext;

    public ChatMessageAdapter(Context context, ArrayList<ChatMessage> chatMessageList) {
        if (chatMessageList == null) throw new NullPointerException();

        mChatMessageList = chatMessageList;
        mContext = context;
    }

    public ChatMessageAdapter(Context context) {
        this(context, new ArrayList<ChatMessage>());
    }

    @Override
    public void addMessage(ChatMessage chatMessage) {
        long currentTimestamp = chatMessage.getTimestamp();
        if (mChatMessageList.size() == 0 || getLast() == null
                || TimestampHelper.datesDiffer(getLast().getTimestamp(), currentTimestamp)){
            mChatMessageList.add(new ChatMessage(
                    TimestampHelper.formatTimestampToDate(currentTimestamp),
                    currentTimestamp,
                    ChatMessage.Direction.System,
                    ""
            ));
        }

        mChatMessageList.add(chatMessage);

        notifyDataSetChanged();
    }

    @Override
    public void addMessagesToEnd(List<ChatMessage> messages) {
        messages = addTimesToMessages(messages, true);
        for (ChatMessage message : messages) {
            mChatMessageList.add(message);
        }
        notifyDataSetChanged();
    }

    @Override
    public void addMessagesToBegin(List<ChatMessage> messages) {
        messages = addTimesToMessages(messages, false);
        for (ChatMessage message : messages) {
            mChatMessageList.add(message);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getSize() {
        return getItemCount();
    }

    @Override
    public boolean hasItemWithId(String guid) {
        for (int i = mChatMessageList.size() - 1; i >= 0; --i) {
            if (mChatMessageList.get(i).getGuid().equals(guid)) return true;
        }

        return false;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        ChatMessage.Direction direction;
        if (viewType == ChatMessage.Direction.Incoming.getIntValue()) {
            layout = R.layout.item_message_incoming;
            direction = ChatMessage.Direction.Incoming;
        } else if (viewType == ChatMessage.Direction.Outcoming.getIntValue()) {
            layout = R.layout.item_message_outcoming;
            direction = ChatMessage.Direction.Outcoming;
        } else {
            layout = R.layout.item_message_system;
            direction = ChatMessage.Direction.System;
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
            viewHolder.setMessage(chatMessage.getContent());
            if (viewHolder.getDirection() != ChatMessage.Direction.System) {
                viewHolder.setTime(chatMessage.getTime());
            }
        }

        //create alert item_dialog
        if (viewHolder.getDirection() != ChatMessage.Direction.System) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    new ContextThemeWrapper(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog));
            builder.setTitle("Message");
            final ArrayAdapter<String> arrayAdapter =
                    new ArrayAdapter<>(mContext, android.R.layout.select_dialog_item);
            arrayAdapter.add("Copy");
            builder.setAdapter(
                    arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String name = arrayAdapter.getItem(which);
                            if (name != null && name.equals("Copy")) {
                                ClipboardManager clipboardManager = (ClipboardManager)
                                        mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("chatMessage", chatMessage.getContent());
                                clipboardManager.setPrimaryClip(clip);
                            }
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.requestWindowFeature(Window.FEATURE_NO_TITLE);

            //show alertDialog on click
            viewHolder.setMessageTextViewOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.show();
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return mChatMessageList.size();
    }

    private List<ChatMessage> addTimesToMessages(List<ChatMessage> messages, boolean addToEnd) {
        List<ChatMessage> messagesWithDates = new ArrayList<>();

        for (int i = 0; i < messages.size(); ++i) {
            ChatMessage currentMessage = messages.get(i);
            if (currentMessage != null) {
                long currentTimestamp = currentMessage.getTimestamp();
                boolean addCurrent = false;


                if (addToEnd) {
                    if (messagesWithDates.size() == 0 && mChatMessageList.size() == 0){
                        addCurrent = true;
                    } else if (messagesWithDates.size() != 0 && TimestampHelper.datesDiffer(currentTimestamp,
                            messagesWithDates.get(messagesWithDates.size() - 1).getTimestamp())) {
                        addCurrent = true;
                    } else if (mChatMessageList.size() != 0 && TimestampHelper.datesDiffer(currentTimestamp,
                            mChatMessageList.get(mChatMessageList.size() - 1).getTimestamp())){
                        addCurrent = true;
                    }
                }

                if (addCurrent) {
                    messagesWithDates.add(new ChatMessage(
                            TimestampHelper.formatTimestampToDate(currentTimestamp),
                            currentTimestamp,
                            ChatMessage.Direction.System,
                            ""
                    ));
                }

                messagesWithDates.add(messages.get(i));
            }
        }

        return messagesWithDates;
    }

    private ChatMessage getLast() {
        if (mChatMessageList.size() == 0) {
            return null;
        } else {
            return mChatMessageList.get(mChatMessageList.size() - 1);
        }
    }

    private ChatMessage getFirst() {
        if (mChatMessageList.size() == 0) {
            return null;
        } else {
            return mChatMessageList.get(0);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView messageTextView;
        private TextView timeTextView;
        private ChatMessage.Direction direction;

        private ViewHolder(View view, ChatMessage.Direction direction) {
            super(view);
            this.direction = direction;
            messageTextView = (TextView) view.findViewById(R.id.message_message_text_view);
            timeTextView = (TextView) view.findViewById(R.id.message_time_text_view);
        }

        private void setMessage(String message) {
            if (messageTextView != null) messageTextView.setText(message);
        }

        private void setTime(String time) {
            if (timeTextView != null) timeTextView.setText(time);
        }

        private void setMessageTextViewOnClickListener(View.OnClickListener listener) {
            messageTextView.setOnClickListener(listener);
        }

        private ChatMessage.Direction getDirection() {
            return direction;
        }
    }

}