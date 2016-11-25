package com.example.batyamessagingapp.activity.dialogs.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.batyamessagingapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Кашин on 25.11.2016.
 */

public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.ViewHolder> {

    private ArrayList<Dialog> mDialogList;
    private final Context mContext;
    private OnDialogClickListener mOnDialogClickListener;

    public DialogAdapter(Context context, ArrayList<Dialog> dialogList) {
        mContext = context;
        mDialogList = dialogList;
    }

    public void setOnDialogClickListener(OnDialogClickListener onDialogClickListener){
        mOnDialogClickListener = onDialogClickListener;
    }

    public String getDialogIdByPosition(int position){
        if (position <= mDialogList.size() - 1 && position >= 0){
            return mDialogList.get(position).getId();
        }

        return null;
    }

    public DialogAdapter(Context context) {
        this(context, new ArrayList<Dialog>());
    }

    public void addDialog(Dialog dialog) {
        mDialogList.add(dialog);
    }

    public void addDialogs(List<Dialog> dialogs) {
        for (Dialog dialog : dialogs) {
            mDialogList.add(dialog);
        }

        notifyDataSetChanged();
    }

    public void setDialogMessageAndTime(int position, String message, String time) {
        if (position < mDialogList.size() - 1 && position >=0) {
            Dialog dialog = mDialogList.get(position);
            dialog.setMessage(message);
            dialog.setTime(time);
        }
    }

    @Override
    public DialogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.dialog, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DialogAdapter.ViewHolder viewHolder, final int position) {
        Dialog dialog = mDialogList.get(position);

        viewHolder.setImage(dialog.getBitmap());
        viewHolder.setMessage(dialog.getMessage());
        viewHolder.setTime(dialog.getTime());
        viewHolder.setId(dialog.getId());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mOnDialogClickListener.onItemClick(DialogAdapter.this, position);
            }
        });
    }

    public int findDialogPositionById(String id) {
        for (int i = 0; i < mDialogList.size(); ++i) {
            if (mDialogList.get(i).getId().equals(id)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public int getItemCount() {
        return mDialogList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView messageTextView;
        private TextView idTextView;
        private TextView timeTextView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.dialogImageView);
            messageTextView = (TextView) itemView.findViewById(R.id.dialogMessageTextView);
            idTextView = (TextView) itemView.findViewById(R.id.dialogIdTextView);
            timeTextView = (TextView) itemView.findViewById(R.id.dialogTimeTextView);
        }

        void setImage(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }

        void setMessage(String text) {
            messageTextView.setText(text);
        }

        void setId(String text) {
            idTextView.setText(text);
        }

        void setTime(String text) {
            timeTextView.setText(text);
        }

    }

}