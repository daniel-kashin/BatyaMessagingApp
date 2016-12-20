package com.example.batyamessagingapp.activity.main.fragment_search.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.model.NetworkExecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Кашин on 18.12.2016.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>
        implements UserDataModel {

    private ArrayList<User> mUserList;
    private final Context mContext;
    private OnUserClickListener mOnUserClickListener;

    public UserAdapter(Context context) {
        mContext = context;
        mUserList = new ArrayList<>();
    }

    @Override
    public void setOnUserClickListener(OnUserClickListener onUserClickListener){
        mOnUserClickListener = onUserClickListener;
    }

    @Override
    public void addUsers(ArrayList<String> userIds) throws IOException, InterruptedException , ExecutionException {
        mUserList = new ArrayList<>(25);
        for (String id : userIds){
            mUserList.add(new User(id, NetworkExecutor.getDialogNameFromId(id)));
        }
        notifyDataSetChanged();
    }

    @Override
    public void clearUsers() {
        mUserList = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public String getUserIdByPosition(int position) {
        if (position < 0 || position > mUserList.size() - 1) {
            throw new IndexOutOfBoundsException();
        } else {
            return mUserList.get(position).getId();
        }
    }

    @Override
    public String getUsernameByPosition(int position) {
        if (position < 0 || position > mUserList.size() - 1) {
            throw new IndexOutOfBoundsException();
        } else {
            return mUserList.get(position).getUsername();
        }
    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_search, parent, false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserAdapter.ViewHolder viewHolder, final int position) {
        User user = mUserList.get(position);

        viewHolder.setText(user.getUsername());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mOnUserClickListener.onItemClick(
                        UserAdapter.this,
                        viewHolder.getAdapterPosition()
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView usernameTextView;

        ViewHolder(View itemView) {
            super(itemView);
            usernameTextView = (TextView) itemView.findViewById(R.id.search_text_view);
        }

        void setText(String text) {
            usernameTextView.setText(text);
        }
    }
}
