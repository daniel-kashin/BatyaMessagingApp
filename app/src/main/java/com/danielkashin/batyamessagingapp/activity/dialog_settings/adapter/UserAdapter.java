package com.danielkashin.batyamessagingapp.activity.dialog_settings.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.danielkashin.batyamessagingapp.R;

import java.util.ArrayList;

/**
 * Created by Кашин on 23.12.2016.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> implements UsersDataModel{

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
  public String getUserIdByPosition(int position) {
    if (position > mUserList.size() - 1 || position < 0) {
      throw new IndexOutOfBoundsException();
    } else {
      return mUserList.get(position).getId();
    }
  }

  public void setUsers(ArrayList<User> users) {
    mUserList = new ArrayList<User>();
    for (User user : users) {
      mUserList.add(user);
    }
    notifyDataSetChanged();
  }

  public int getSize() {
    return getItemCount();
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater
        .from(parent.getContext())
        .inflate(R.layout.item_group_user, parent, false);

    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
    User user = mUserList.get(position);

    viewHolder.setBitmap(user.getBitmap());
    viewHolder.setUsername(user.getName());
    viewHolder.setId("id: " + user.getId());
    viewHolder.setDate(user.getDateAdded());

    if (mOnUserClickListener != null) {
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
  }

  @Override
  public int getItemCount() {
    return mUserList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;
    private TextView usernameTextView;
    private TextView idTextView;
    private TextView dateTextView;

    ViewHolder(View itemView) {
      super(itemView);
      imageView = (ImageView) itemView.findViewById(R.id.user_image_view);
      usernameTextView = (TextView) itemView.findViewById(R.id.user_username_text_view);
      idTextView = (TextView) itemView.findViewById(R.id.user_id_text_view);
      dateTextView = (TextView) itemView.findViewById(R.id.user_date_text_view);
    }

    void setBitmap(Bitmap bitmap) {
      imageView.setImageBitmap(bitmap);
    }

    void setUsername(String text) {
      usernameTextView.setText(text);
    }

    void setId(String text) {
      idTextView.setText(text);
    }

    void setDate(String text) {
      dateTextView.setText(text);
    }
  }
}
