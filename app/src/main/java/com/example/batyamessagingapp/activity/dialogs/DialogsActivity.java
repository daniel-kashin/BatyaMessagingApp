package com.example.batyamessagingapp.activity.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.activity.authentication.AuthenticationActivity;
import com.example.batyamessagingapp.activity.chat.ChatActivity;
import com.example.batyamessagingapp.activity.dialogs.adapter.Dialog;
import com.example.batyamessagingapp.activity.dialogs.adapter.DialogAdapter;
import com.example.batyamessagingapp.activity.dialogs.adapter.OnDialogClickListener;
import com.example.batyamessagingapp.model.PreferencesService;

public class DialogsActivity extends AppCompatActivity implements DialogsView {

    private ProgressDialog mProgressDialog;
    private Button mLogoutButton;
    private Button mFullLogoutButton;
    private RecyclerView mRecyclerView;

    private DialogsPresenter mDialogsPresenter;


    private void initializeViews() {
        mLogoutButton = (Button)findViewById(R.id.logoutButton);
        mFullLogoutButton = (Button)findViewById(R.id.fullLogoutButton);

        mRecyclerView = (RecyclerView) findViewById(R.id.dialogRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(manager);
        DialogAdapter adapter = new DialogAdapter(this);
        adapter.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, int position) {
                String dialogId = ((DialogAdapter)adapter).getDialogIdByPosition(position);
                DialogsActivity.this.openChatActivity(dialogId);
            }
        });
        mRecyclerView.setAdapter(adapter);

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

    @Override
    public int getAdapterSize() {
        return mRecyclerView.getAdapter().getItemCount();
    }

    @Override
    public void setDialogMessageAndTimeInAdapter(int position, String message, String time) {
        ((DialogAdapter)mRecyclerView.getAdapter())
                .setDialogMessageAndTime(position, message, time);
    }

    @Override
    public void refreshAdapter(){
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void addDialogToAdapter(Dialog dialog) {
        ((DialogAdapter)mRecyclerView.getAdapter())
                .addDialog(dialog);
    }

    @Override
    public int findDialogPositionByIdInAdapter(String id) {
        return ((DialogAdapter)mRecyclerView.getAdapter())
                .findDialogPositionById(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);

        initializeViews();
        setOnClickListeners();

        mDialogsPresenter = new DialogsService((DialogsView)this, (Context) this);
        mDialogsPresenter.onLoad();
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
}
