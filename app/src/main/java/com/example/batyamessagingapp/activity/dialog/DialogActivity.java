package com.example.batyamessagingapp.activity.dialog;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.batyamessagingapp.R;

public class DialogActivity extends AppCompatActivity implements DialogView {

    private EditText sendMessageEditText;
    private Button sendMessageButton;
    private DialogPresenter presenter;

    private void initializeViews() {
        sendMessageEditText = (EditText) findViewById(R.id.sendMessageEditText);
        sendMessageButton = (Button) findViewById(R.id.sendMessageButton);
    }

    private void setListeners() {
        sendMessageEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0){
                    sendMessageButton.animate().alpha(1).setDuration(200);
                    sendMessageButton.setVisibility(View.VISIBLE);
                } else if (s.length() == 0) {
                    sendMessageButton.animate().alpha(0).setDuration(200);
                    sendMessageButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSendMessageButtonClick();
            }
        });

    }

    @Override
    public String getMessageString() {
        return sendMessageEditText.getText().toString();
    }

    @Override
    public void showAlert(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        initializeViews();
        setListeners();

        presenter = new DialogService(this);
    }


}
