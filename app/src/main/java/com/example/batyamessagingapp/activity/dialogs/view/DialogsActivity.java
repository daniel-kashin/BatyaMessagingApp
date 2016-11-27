package com.example.batyamessagingapp.activity.dialogs.view;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.activity.authentication.view.AuthenticationActivity;
import com.example.batyamessagingapp.activity.chat.view.ChatActivity;
import com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.view.ViewDialogsFragment;
import com.example.batyamessagingapp.activity.dialogs.presenter.DialogsPresenter;
import com.example.batyamessagingapp.activity.dialogs.presenter.DialogsService;

public class DialogsActivity extends AppCompatActivity implements DialogsView {

    private ProgressDialog mProgressDialog;
    private Button mLogoutButton;
    private Button mFullLogoutButton;
    private Toolbar mDialogsToolbar;

    private DialogsPresenter mDialogsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);

        initializeViews();
        setOnClickListeners();

        mDialogsPresenter = new DialogsService(this);

        //TODO: remove bydlokod
        Fragment viewDialogsFragment = new ViewDialogsFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragmentContainer, viewDialogsFragment);
        transaction.commit();
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
    public void openAuthenticationActivity(){
        Intent intent = new Intent(this, AuthenticationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    @Override
    public void openChatActivity(String dialogId){
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("dialog_id", dialogId);
        startActivity(intent);
    }

    private void setOnClickListeners(){
        mLogoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mDialogsPresenter.onLogoutButtonClick();
            }
        });

        mFullLogoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mDialogsPresenter.onFullLogoutButtonClick();
            }
        });
    }

    private void initializeViews() {
        //toolbar
        mDialogsToolbar= (Toolbar) findViewById(R.id.dialogsToolbar);
        setSupportActionBar(mDialogsToolbar);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Messages");
        }

        mLogoutButton = (Button)findViewById(R.id.logoutButton);
        mFullLogoutButton = (Button)findViewById(R.id.fullLogoutButton);
    }
}
