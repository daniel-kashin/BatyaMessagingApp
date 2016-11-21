package com.example.batyamessagingapp.activity.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.activity.authentication.AuthenticationActivity;
import com.example.batyamessagingapp.activity.dialog.recycler_view.Message;
import com.example.batyamessagingapp.activity.dialog.recycler_view.MessageAdapter;

import java.util.ArrayList;

public class DialogActivity extends AppCompatActivity implements DialogView {

    private EditText sendMessageEditText;
    private Button sendMessageButton;
    private DialogPresenter presenter;
    private RecyclerView recyclerView;
    private View activityRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        initializeViews();
        setListeners();

        presenter = new DialogService(this);


    }

    private void initializeViews() {
        sendMessageEditText = (EditText) findViewById(R.id.messageEditText);
        sendMessageButton = (Button) findViewById(R.id.messageButton);
        activityRootView = findViewById(R.id.activity_dialog);

        recyclerView = (RecyclerView) findViewById(R.id.messagesRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        //manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(new MessageAdapter(this, new ArrayList<Message>()));
    }

    private void setListeners() {

        //hide/show send button when text changed
        sendMessageEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    sendMessageButton.animate().alpha(1).setDuration(200);
                    sendMessageButton.setVisibility(View.VISIBLE);
                } else if (s.length() == 0) {
                    sendMessageButton.animate().alpha(0).setDuration(200);
                    sendMessageButton.setVisibility(View.INVISIBLE);
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
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSendMessageButtonClick();
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
            }
        });

        //hide keyboard when view is scrolled
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                View view = DialogActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        //scroll when keyboard appears
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                DisplayMetrics metrics = DialogActivity.this.getResources().getDisplayMetrics();
                final float dp200 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, metrics);
                final int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dp200) {
                    recyclerView.post(new Runnable() {
                        public void run() {
                            recyclerView.scrollBy(0, heightDiff);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void clearMessageEditText(){
        sendMessageEditText.setText("");
    }

    @Override
    public void addMessageToAdapter(String message, Message.Direction direction){
        ((MessageAdapter)recyclerView.getAdapter()).addMessage(message, direction);
        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
    }

    @Override
    public String getMessageString() {
        return sendMessageEditText.getText().toString();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
}