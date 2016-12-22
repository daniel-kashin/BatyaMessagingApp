package com.example.batyamessagingapp.activity.main.fragment_dialogs.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.lib.TimestampHelper;
import com.example.batyamessagingapp.model.pojo.Message;
import com.example.batyamessagingapp.model.pojo.PairLastMessageDialogId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Кашин on 25.11.2016.
 */

//TODO: add separator

public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.ViewHolder>
    implements DialogsDataModel {

  private ArrayList<Dialog> mDialogList;
  private final Context mContext;
  private OnDialogClickListener mOnDialogClickListener;

  public DialogAdapter(Context context, ArrayList<Dialog> dialogList) {
    mContext = context;
    mDialogList = dialogList;
  }

  public void setOnDialogClickListener(OnDialogClickListener onDialogClickListener) {
    mOnDialogClickListener = onDialogClickListener;
  }

  @Override
  public String getDialogIdByPosition(int position) {
    if (position > mDialogList.size() - 1 || position < 0) {
      throw new IndexOutOfBoundsException();
    } else {
      return mDialogList.get(position).getId();
    }
  }

  @Override
  public String getDialogNameByPosition(int position) {
    if (position > mDialogList.size() - 1 || position < 0) {
      throw new IndexOutOfBoundsException();
    } else {
      return mDialogList.get(position).getName();
    }
  }

  @Override
  public boolean noChanges(ArrayList<PairLastMessageDialogId> dialogs) {
    if (getSize() == 0) {
      return false;
    } else {
      long maxTimestamp = -1;

      for  (PairLastMessageDialogId pair: dialogs){
        if (pair.getMessage().getTimestamp() > maxTimestamp) {
          maxTimestamp = pair.getMessage().getTimestamp();
        }
      }

      Dialog lastInAdapter = mDialogList.get(0);
      return lastInAdapter.getTimestamp() == maxTimestamp;
    }
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
  }

  public void refresh() {
    Collections.sort(mDialogList, new Comparator<Dialog>() {
      @Override
      public int compare(Dialog o1, Dialog o2) {
        if (o1.getTimestamp() < o2.getTimestamp()) {
          return 1;
        } else if (o1.getTimestamp() > o2.getTimestamp()) {
          return -1;
        } else {
          return 0;
        }
      }
    });
    notifyDataSetChanged();
  }

  public int getSize() {
    return getItemCount();
  }

  public void setDialogMessageAndTimestamp(int position, String message, long timestamp) {
    if (position < mDialogList.size() && position >= 0) {
      Dialog dialog = mDialogList.get(position);
      if (dialog.getTimestamp() != timestamp) {
        Dialog newDialog = new Dialog(dialog.getBitmap(), dialog.getId(), dialog.getName(),
            message, timestamp);
        mDialogList.remove(position);
        mDialogList.add(0, newDialog);
      }
    }
  }

  @Override
  public DialogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater
        .from(parent.getContext())
        .inflate(R.layout.item_dialog, parent, false);

    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final DialogAdapter.ViewHolder viewHolder, final int position) {
    Dialog dialog = mDialogList.get(position);

    viewHolder.setImage(dialog.getBitmap());
    viewHolder.setMessage(dialog.getMessage());
    viewHolder.setTime(TimestampHelper.formatTimestamp(dialog.getTimestamp()));
    viewHolder.setDialogName(dialog.getName());

    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mOnDialogClickListener.onItemClick(
            DialogAdapter.this,
            viewHolder.getAdapterPosition()
        );
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
    private TextView dialogNameTextView;
    private TextView timeTextView;

    ViewHolder(View itemView) {
      super(itemView);
      imageView = (ImageView) itemView.findViewById(R.id.dialog_image_view);
      messageTextView = (TextView) itemView.findViewById(R.id.dialog_message_text_view);
      dialogNameTextView = (TextView) itemView.findViewById(R.id.dialog_username_text_view);
      timeTextView = (TextView) itemView.findViewById(R.id.dialog_time_text_view);
    }

    void setImage(Bitmap bitmap) {
      imageView.setImageBitmap(bitmap);
    }

    void setMessage(String text) {
      messageTextView.setText(text);
    }

    void setDialogName(String text) {
      dialogNameTextView.setText(text);
    }

    void setTime(String text) {
      timeTextView.setText(text);
    }

  }

}