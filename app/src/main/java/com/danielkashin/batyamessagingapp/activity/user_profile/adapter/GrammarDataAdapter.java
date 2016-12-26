package com.danielkashin.batyamessagingapp.activity.user_profile.adapter;

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

public class GrammarDataAdapter extends RecyclerView.Adapter<GrammarDataAdapter.ViewHolder>
    implements GrammarDataModel {

  private ArrayList<GrammarData> mGrammarDataList = new ArrayList<>();
  private final Context mContext;

  public GrammarDataAdapter(Context context) {
    mContext = context;
  }

  @Override
  public void setData(ArrayList<GrammarData> data) {
    mGrammarDataList = data;
    notifyDataSetChanged();
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater
        .from(parent.getContext())
        .inflate(R.layout.item_grammar, parent, false);

    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
    GrammarData data = mGrammarDataList.get(position);

    viewHolder.setDate(data.getDate());
    viewHolder.setCorrect(data.getCountRight() + " correct");
    viewHolder.setPercent(data.getPercent() + "%");
    viewHolder.setTotal(data.getCountAll() + " total");
  }

  @Override
  public int getItemCount() {
    return mGrammarDataList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {

    private TextView dateTextView;
    private TextView correctTextView;
    private TextView totalTextView;
    private TextView percentTextView;

    ViewHolder(View itemView) {
      super(itemView);
      dateTextView = (TextView) itemView.findViewById(R.id.grammar_date);
      correctTextView = (TextView) itemView.findViewById(R.id.grammar_correct);
      totalTextView = (TextView) itemView.findViewById(R.id.grammar_total);
      percentTextView = (TextView) itemView.findViewById(R.id.grammar_percent);
    }

    public void setDate(String date) {
      dateTextView.setText(date);
    }

    void setCorrect(String correct) {
      correctTextView.setText(correct);
    }

    void setTotal(String total) {
      totalTextView.setText(total);
    }

    void setPercent(String percent) {
      percentTextView.setText(percent);
    }
  }
}
