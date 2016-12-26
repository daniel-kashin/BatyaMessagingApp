package com.danielkashin.batyamessagingapp.activity.dialog_add_users.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.danielkashin.batyamessagingapp.R;
import com.danielkashin.batyamessagingapp.activity.dialog_add_users.adapter.UserAdapter;
import com.danielkashin.batyamessagingapp.activity.dialog_add_users.adapter.UserDataModel;
import com.danielkashin.batyamessagingapp.activity.dialog_add_users.presenter.DialogAddUsersPresenter;
import com.danielkashin.batyamessagingapp.activity.dialog_add_users.presenter.DialogAddUsersService;
import com.danielkashin.batyamessagingapp.activity.main.view.MainActivity;

public class DialogAddUsersActivity extends AppCompatActivity implements DialogAddUsersView {

  private RecyclerView mRecyclerView;
  private TextView mTextView;
  private ProgressBar mProgressBar;
  private Toolbar mToolbar;
  private EditText mToolbarEditText;
  private ImageView mToolbarClearIcon;

  private String mDialogId;

  private DialogAddUsersPresenter mPresenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dialog_add_users);

    mDialogId = getIntent().getStringExtra("dialog_id");

    initializeViews();
    setListeners();

    mPresenter = new DialogAddUsersService(this, (Context) this,
        (UserDataModel) mRecyclerView.getAdapter(), mDialogId);
  }

  @Override
  public void onResume() {
    super.onResume();
    mPresenter.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void showProgressBar() {
    mProgressBar.setVisibility(View.VISIBLE);
    hideTextView();
  }

  @Override
  public void showClearIcon() {
    mToolbarClearIcon.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideClearIcon() {
    mToolbarClearIcon.setVisibility(View.INVISIBLE);
  }

  @Override
  public boolean isInputEmpty() {
    return mToolbarEditText.getText().toString().isEmpty();
  }

  @Override
  public void hideProgressBar() {
    mProgressBar.setVisibility(View.INVISIBLE);
  }

  @Override
  public void showAlert(String message, String title) {
    new AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(true)
        .create()
        .show();
  }

  @Override
  public void showNoUsersTextView() {
    mTextView.setText(getString(R.string.no_users));
    mTextView.setVisibility(View.VISIBLE);
    hideProgressBar();
  }

  @Override
  public void showNoFriendsTextView() {
    mTextView.setText(getString(R.string.no_friends));
    mTextView.setVisibility(View.VISIBLE);
    hideProgressBar();
  }

  @Override
  public void showNoInternetConnectionTextView() {
    mTextView.setText(getString(R.string.no_internet_connection));
    mTextView.setVisibility(View.VISIBLE);
    hideProgressBar();
  }

  @Override
  public void hideTextView() {
    mTextView.setVisibility(View.INVISIBLE);
  }

  @Override
  public void openMainActivity() {
    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
        | Intent.FLAG_ACTIVITY_NO_ANIMATION);
    startActivity(intent);
    finish();
  }

  @Override
  public void setOnToolbarTextListener(TextWatcher textWatcher) {
    mToolbarEditText.addTextChangedListener(textWatcher);
  }


  private void initializeViews() {
    mTextView = (TextView) findViewById(R.id.dialog_add_users_username_text_view);
    mProgressBar = (ProgressBar) findViewById(R.id.dialog_add_users_progress_bar);

    //toolbar
    mToolbar = (Toolbar) findViewById(R.id.dialog_add_users_toolbar);
    setSupportActionBar(mToolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    mToolbarEditText = (EditText) findViewById(R.id.dialog_add_users_toolbar_edit_text);
    mToolbarClearIcon = (ImageView) findViewById(R.id.dialog_add_users_toolbar_clear_icon);

    // recycler view
    mRecyclerView = (RecyclerView) findViewById(R.id.dialog_add_users_recycler_view);
    LinearLayoutManager manager = new LinearLayoutManager(this,
        LinearLayoutManager.VERTICAL, false);
    mRecyclerView.setLayoutManager(manager);
    mRecyclerView.setAdapter(new UserAdapter(this));
  }

  private void setListeners() {
    mToolbarClearIcon.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mToolbarEditText.setText("");
      }
    });
  }
}
