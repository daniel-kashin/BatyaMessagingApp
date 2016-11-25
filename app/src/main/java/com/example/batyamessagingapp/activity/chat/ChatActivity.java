package com.example.batyamessagingapp.activity.chat;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.activity.chat.adapter.ChatMessage;
import com.example.batyamessagingapp.activity.chat.adapter.ChatMessageAdapter;

import java.util.List;

public class ChatActivity extends AppCompatActivity implements ChatView {

    private EditText mSendMessageEditText;
    private Button mSendMessageButton;
    private RecyclerView mRecyclerView;
    private View mActivityRootView;

    private ChatPresenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initializeViews();
        setListeners();

        mPresenter = new ChatService(this, getIntent().getStringExtra("dialog_id"));
        mPresenter.onLoad();
    }

    private void initializeViews() {
        mSendMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendMessageButton = (Button) findViewById(R.id.messageButton);
        mActivityRootView = findViewById(R.id.activity_dialog);

        mRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        manager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(new ChatMessageAdapter(this));
    }

    private void setListeners() {

        //hide/show send button when text changed
        mSendMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    mSendMessageButton.animate().alpha(1).setDuration(200);
                    mSendMessageButton.setVisibility(View.VISIBLE);
                } else if (s.length() == 0) {
                    mSendMessageButton.animate().alpha(0).setDuration(200);
                    mSendMessageButton.setVisibility(View.INVISIBLE);
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
        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onSendMessageButtonClick();
                mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
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
                    mRecyclerView.post(new Runnable() {
                        public void run() {
                            mRecyclerView.scrollBy(0, heightDiff);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void clearMessageEditText(){
        mSendMessageEditText.setText("");
    }

    @Override
    public void addMessageToAdapter(ChatMessage message){
        ((ChatMessageAdapter) mRecyclerView.getAdapter()).addMessage(message);
        mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount()-1);
    }

    @Override
    public void addMessagesToAdapter(List<ChatMessage> messages){
        ((ChatMessageAdapter) mRecyclerView.getAdapter()).addMessages(messages);
        mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount()-1);
    }

    @Override
    public String getMessageString() {
        return mSendMessageEditText.getText().toString();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
}