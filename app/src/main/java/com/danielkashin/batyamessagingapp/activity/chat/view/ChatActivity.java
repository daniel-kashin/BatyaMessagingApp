package com.danielkashin.batyamessagingapp.activity.chat.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.danielkashin.batyamessagingapp.R;
import com.danielkashin.batyamessagingapp.activity.chat.adapter.ChatMessageAdapter;
import com.danielkashin.batyamessagingapp.activity.chat.adapter.MessagesDataModel;
import com.danielkashin.batyamessagingapp.activity.chat.presenter.ChatPresenter;
import com.danielkashin.batyamessagingapp.activity.chat.presenter.ChatService;
import com.danielkashin.batyamessagingapp.activity.dialog_settings.view.DialogSettingsActivity;
import com.danielkashin.batyamessagingapp.activity.main.view.MainActivity;

public class ChatActivity extends AppCompatActivity implements ChatView {

  private EditText mSendMessageEditText;
  private ImageView mSendMessageIcon;
  private ProgressBar mProgressBar;
  private RecyclerView mRecyclerView;

  private Toolbar mToolbar;
  private TextView mToolbarLabel;
  private TextView mToolbarSmallLabel;
  private ImageView mToolbarSettingsIcon;
  private View mToolbarLayout;
  private View mToolbarHiddenLayout;

  private String mDialogId;
  private String mDialogName;
  private boolean mIsGroup;
  private boolean mIsGroupOriginator;
  private boolean mIsChatWithMyself;
  private int mGroupCount;

  private ChatPresenter mPresenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);

    mDialogId = getIntent().getStringExtra("dialog_id");
    mDialogName = getIntent().getStringExtra("dialog_name");

    initializeViews();

    mPresenter = new ChatService(this, mDialogId, mDialogName, (MessagesDataModel) mRecyclerView.getAdapter());
  }

  @Override
  protected void onPause() {
    super.onPause();
    mPresenter.onPause();
  }

  @Override
  protected void onResume() {
    super.onResume();

    mPresenter.onInitializeProperties();
    initializeToolbar();
    setListeners();

    mPresenter.onLoad();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void clearMessageEditText() {
    mSendMessageEditText.setText("");
  }

  @Override
  public void setProperties(boolean isGroup, boolean isGroupOriginator, boolean isChatWithMyself, int groupCount) {
    mIsGroup = isGroup;
    mIsGroupOriginator = isGroupOriginator;
    mIsChatWithMyself = isChatWithMyself;
    mGroupCount = groupCount;
  }

  @Override
  public void scrollRecyclerViewToLast() {
    if (mRecyclerView.getAdapter().getItemCount() != 0) {
      mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount());
    }
  }

  @Override
  public void scrollRecyclerViewToFirst() {
    if (mRecyclerView.getAdapter().getItemCount() != 0) {
      mRecyclerView.smoothScrollToPosition(0);
    }
  }

  @Override
  public void setCommonToolbarLabelText() {
    if (!mToolbarLabel.getText().toString().equals(mDialogName)) {
      mToolbarLabel.setText(mDialogName);
    }
  }

  @Override
  public void setLoadingToolbarLabelText() {
    if (!mToolbarLabel.getText().toString().equals(getString(R.string.loading)))
    mToolbarLabel.setText(getString(R.string.loading));
  }

  @Override
  public void setNoInternetToolbarLabelText() {
    if (!mToolbarLabel.getText().toString().equals(getString(R.string.waiting_for_connection))) {
      mToolbarLabel.setText(getString(R.string.waiting_for_connection));
    }
  }

  @Override
  public void openDialogsActivity() {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
    finish();
  }

  @Override
  public String getInputMessage() {
    return mSendMessageEditText.getText().toString();
  }



  private void initializeViews() {
    //recycler view
    mRecyclerView = (RecyclerView) findViewById(R.id.chat_message_recycler_view);
    LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    manager.setStackFromEnd(true);
    mRecyclerView.setLayoutManager(manager);
    mRecyclerView.setAdapter(new ChatMessageAdapter(this, mIsGroup));

    mSendMessageEditText = (EditText) findViewById(R.id.chat_message_edit_text);
    mSendMessageIcon = (ImageView) findViewById(R.id.chat_send_message_icon);
  }

  private void initializeToolbar(){
    mToolbar = (Toolbar) findViewById(R.id.chat_toolbar);

    if (mIsChatWithMyself || mIsGroup) {
      mToolbarSettingsIcon = (ImageView) findViewById(R.id.chat_toolbar_settings_icon);
      if (mIsGroup){
        mToolbarSettingsIcon.setVisibility(View.VISIBLE);
      } else {
        mToolbarSettingsIcon.setVisibility(View.INVISIBLE);
      }

      mToolbarLayout = findViewById(R.id.chat_toolbar_layout_double);
      mToolbarHiddenLayout = findViewById(R.id.chat_toolbar_layout_single);

      mToolbarLabel = (TextView) findViewById(R.id.chat_toolbar_double_label);
      mToolbarSmallLabel = (TextView) findViewById(R.id.chat_toolbar_double_small_label);
      mToolbarSmallLabel.setText(mIsChatWithMyself ? "chat with yourself"
          : mGroupCount + ((mGroupCount % 10 == 1) ? "member" : " members"));
    } else {
      mToolbarHiddenLayout  = findViewById(R.id.chat_toolbar_layout_double);
      mToolbarLayout = findViewById(R.id.chat_toolbar_layout_single);

      mToolbarLabel = (TextView) findViewById(R.id.chat_toolbar_single_label);
    }

    mToolbarHiddenLayout.setVisibility(View.GONE);
    mToolbarLayout.setVisibility(View.VISIBLE);

    mToolbarLabel.setText(mDialogId);
    mToolbarLabel.setSelected(true);

    setSupportActionBar(mToolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
  }

  private void setListeners() {
    //hide/show send button when text changed
    mSendMessageEditText.addTextChangedListener(new TextWatcher() {
      @Override
      public void afterTextChanged(Editable s) {
        if (s.length() != 0 && mPresenter.initialized()) {
          mSendMessageIcon.animate().alpha(1).setDuration(200);
          mSendMessageIcon.setVisibility(View.VISIBLE);
        } else {
          mSendMessageIcon.animate().alpha(0).setDuration(200);
          mSendMessageIcon.setVisibility(View.INVISIBLE);
        }
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }
    });

    //send message and scroll when button is clicked
    mSendMessageIcon.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mPresenter.onSendMessageButtonClick();
      }
    });

    mToolbarLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        scrollRecyclerViewToFirst();
      }
    });

    if (mIsGroupOriginator){
      mToolbarSettingsIcon.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(ChatActivity.this, DialogSettingsActivity.class);
          startActivity(intent);
        }
      });
    }
  }

}