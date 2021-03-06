package com.danielkashin.batyamessagingapp.activity.main.fragment_search.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.danielkashin.batyamessagingapp.R;

import java.util.ArrayList;

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
  public void setOnUserClickListener(OnUserClickListener onUserClickListener) {
    mOnUserClickListener = onUserClickListener;
  }

  @Override
  public void setUsers(ArrayList<User> users) {
    mUserList = users;
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

    viewHolder.setUsername(user.getUsername());
    viewHolder.setId(user.getId());

    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
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
    private TextView idTextView;

    ViewHolder(View itemView) {
      super(itemView);
      usernameTextView = (TextView) itemView.findViewById(R.id.search_username_text_view);
      idTextView = (TextView) itemView.findViewById(R.id.search_id_text_view);
    }

    void setUsername(String username) {
      usernameTextView.setText(username);
    }

    void setId(String id) {
      idTextView.setText("id: " + id);
    }
  }
}
