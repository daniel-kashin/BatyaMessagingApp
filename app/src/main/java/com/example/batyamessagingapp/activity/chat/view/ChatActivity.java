package com.example.batyamessagingapp.activity.chat.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.activity.authentication.view.AuthenticationActivity;
import com.example.batyamessagingapp.activity.chat.adapter.ChatMessageAdapter;
import com.example.batyamessagingapp.activity.chat.adapter.MessagesDataModel;
import com.example.batyamessagingapp.activity.chat.presenter.ChatPresenter;
import com.example.batyamessagingapp.activity.chat.presenter.ChatService;

public class ChatActivity extends AppCompatActivity implements ChatView {

    private EditText mSendMessageEditText;
    private ImageView mSendMessageIcon;
    private View mActivityRootView;
    private Toolbar mToolbar;
    private TextView mToolbarLabel;
    private TextView mNoMessagesTextView;
    private ImageView mToolbarRefreshIcon;

    private RecyclerView mRecyclerView;
    private String mDialogId;

    private ChatPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mDialogId = getIntent().getStringExtra("dialog_id");

        initializeViews();
        setListeners();

        mPresenter = new ChatService(this, mDialogId, (MessagesDataModel)mRecyclerView.getAdapter());
    }

    @Override
    protected void onPause(){
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
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
    public void hideNoMessagesTextView() {
        mNoMessagesTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setToolbarLabelText(String text) {
        mToolbarLabel.setText(text);
    }

    @Override
    public void showRefreshIcon() {
        mToolbarRefreshIcon.setVisibility(View.VISIBLE);
    }

    @Override
    public void clearMessageEditText(){
        mSendMessageEditText.setText("");
    }

    @Override
    public void scrollRecyclerViewToLast() {
        if (mRecyclerView.getAdapter().getItemCount() != 0) {
            mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
        }
    }

    @Override
    public void scrollRecyclerViewToFirst() {
        if (mRecyclerView.getAdapter().getItemCount() != 0) {
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void openAuthenticationActivity() {
        Intent intent = new Intent(this, AuthenticationActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public String getMessage() {
        return mSendMessageEditText.getText().toString();
    }

    private void initializeViews() {
        //toolbar
        mToolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        mToolbarLabel = (TextView) findViewById(R.id.chat_toolbar_label);
        mToolbarLabel.setText(mDialogId);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mToolbarRefreshIcon = (ImageView)findViewById(R.id.chat_toolbar_refresh_icon);

        //recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.chat_message_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        manager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(new ChatMessageAdapter(this));

        mSendMessageEditText = (EditText) findViewById(R.id.chat_message_edit_text);
        mSendMessageIcon = (ImageView) findViewById(R.id.chat_send_message_icon);
        mActivityRootView = findViewById(R.id.activity_chat);
        mNoMessagesTextView = (TextView) findViewById(R.id.chat_no_messages_text_view);
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
                scrollRecyclerViewToLast();
            }
        });

        //hide keyboard when view is scrolled
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                View view = ChatActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        //scroll when keyboard appears
        mActivityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                DisplayMetrics metrics = ChatActivity.this.getResources().getDisplayMetrics();
                final float dp200 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, metrics);
                final int heightDiff = mActivityRootView.getRootView().getHeight() - mActivityRootView.getHeight();
                if (heightDiff > dp200) {
                    scrollRecyclerViewToLast();
                }
            }
        });

        mToolbarLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollRecyclerViewToFirst();
            }
        });

        mToolbarRefreshIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideRefreshIcon();
                mPresenter.onRefreshIconClick();
            }
        });
    }

    private void hideRefreshIcon() {
        mToolbarRefreshIcon.setVisibility(View.INVISIBLE);
    }
}