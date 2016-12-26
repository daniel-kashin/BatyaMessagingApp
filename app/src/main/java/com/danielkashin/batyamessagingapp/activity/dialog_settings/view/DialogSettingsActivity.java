package com.danielkashin.batyamessagingapp.activity.dialog_settings.view;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.danielkashin.batyamessagingapp.R;
import com.danielkashin.batyamessagingapp.activity.dialog_add_users.view.DialogAddUsersActivity;
import com.danielkashin.batyamessagingapp.activity.dialog_settings.adapter.UserAdapter;
import com.danielkashin.batyamessagingapp.activity.dialog_settings.adapter.UserDataModel;
import com.danielkashin.batyamessagingapp.activity.dialog_settings.presenter.DialogSettingsPresenter;
import com.danielkashin.batyamessagingapp.activity.dialog_settings.presenter.DialogSettingsService;
import com.danielkashin.batyamessagingapp.activity.main.view.MainActivity;
import com.danielkashin.batyamessagingapp.activity.user_profile.view.UserProfileActivity;
import com.danielkashin.batyamessagingapp.lib.CircleBitmapFactory;

import java.util.regex.Pattern;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class DialogSettingsActivity extends AppCompatActivity implements DialogSettingsView {

  private TextView mToolbarLabel;
  private Toolbar mToolbar;
  private EditText mDialogNameEditText;
  private ImageView mConfirmIcon;
  private ImageView mMainIcon;
  private Button mAddButton;
  private Button mLeaveButton;
  private RecyclerView mRecyclerView;

  private String mDialogId;
  private String mDialogName;
  private boolean mIsAdmin;

  private DialogSettingsPresenter mPresenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dialog_settings);

    mDialogId = getIntent().getStringExtra("dialog_id");
    mDialogName = getIntent().getStringExtra("dialog_name");
    mIsAdmin = getIntent().getBooleanExtra("is_admin", false);

    initializeViews();
    setListeners();

    mPresenter = new DialogSettingsService(this, mDialogId, (UserDataModel)mRecyclerView.getAdapter());
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
  public void setDialogName(String newDialogName) {
    mDialogName = newDialogName;

    String firstLetter = CircleBitmapFactory.getFirstLetter(mDialogName);
    int color = CircleBitmapFactory.getMaterialColor(mDialogId.hashCode());
    mMainIcon.setImageBitmap(CircleBitmapFactory.generateCircleBitmap(this, color, 80, firstLetter));
  }

  @Override
  public void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
  public void openChatActivity() {
    finish();
  }

  @Override
  public void openMainActivity() {
    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
        | Intent.FLAG_ACTIVITY_NO_ANIMATION);
    startActivity(intent);
  }

  @Override
  public void openUserProfileActivity(String id, String username) {
    Intent intent = new Intent(this, UserProfileActivity.class);
    intent.putExtra("dialog_id", id);
    intent.putExtra("dialog_name", username);
    startActivity(intent);
  }

  @Override
  public boolean isAdmin() {
    return mIsAdmin;
  }

  @Override
  public void setNoInternetConnectionToolbarLabel() {
    mToolbarLabel.setText(getString(R.string.waiting_for_connection));
  }

  @Override
  public void setCommonToolbarLabel() {
    mToolbarLabel.setText("Group settings");
  }

  @Override
  public void hideConfirmIcon() {
    mConfirmIcon.setVisibility(View.GONE);
  }

  @Override
  public boolean checkInputs() {
    String dialogName = mDialogNameEditText.getText().toString();
    String error = null;
    Pattern usernamePattern = Pattern.compile("[A-Za-z0-9_-]+");

    if (!usernamePattern.matcher(dialogName).matches()) {
      error = "Dialog name can contain only letters, numbers and _-";
    }

    if (dialogName.length() < 2 || dialogName.length() > 256) {
      error = "Dialog name should be 2..256 length";
    }

    if (error != null) showAlert(error, "Error");

    if (dialogName.isEmpty()) {
      return false;
    } else {
      return error == null;
    }
  }

  private void initializeViews() {
    // recycler view
    mRecyclerView = (RecyclerView) findViewById(R.id.dialog_settings_users_recycler_view);
    LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    mRecyclerView.setLayoutManager(manager);
    mRecyclerView.setAdapter(new UserAdapter(this));

    //toolbar
    mToolbar = (Toolbar) findViewById(R.id.dialog_settings_toolbar);
    setSupportActionBar(mToolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);
    }
    mToolbarLabel = (TextView) findViewById(R.id.dialog_settings_toolbar_label);
    mToolbarLabel.setText("Group settings");

    mConfirmIcon = (ImageView) findViewById(R.id.dialog_settings_confirm_icon);
    mConfirmIcon.setVisibility(GONE);

    mDialogNameEditText = (EditText) findViewById(R.id.dialog_settings_username_edit_text);
    mDialogNameEditText.setText(mDialogName);

    mMainIcon = (ImageView) findViewById(R.id.dialog_settings_main_icon);
    String firstLetter = CircleBitmapFactory.getFirstLetter(mDialogName);
    int color = CircleBitmapFactory.getMaterialColor(mDialogId.hashCode());
    mMainIcon.setImageBitmap(CircleBitmapFactory.generateCircleBitmap(this, color, 80, firstLetter));

    mAddButton = (Button) findViewById(R.id.dialog_settings_add_button);
    mLeaveButton = (Button) findViewById(R.id.dialog_settings_leave_button);


    if (mIsAdmin){
      mAddButton.setVisibility(VISIBLE);
      mLeaveButton.setVisibility(GONE);
      mDialogNameEditText.setEnabled(true);
    } else {
      mAddButton.setVisibility(View.GONE);
      mLeaveButton.setVisibility(VISIBLE);
      mDialogNameEditText.setEnabled(false);
    }
  }

  private void setListeners() {
    if (mIsAdmin) {
      mDialogNameEditText.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
          if (!s.toString().equals(mDialogName)) {
            mConfirmIcon.setVisibility(VISIBLE);
          } else {
            mConfirmIcon.setVisibility(GONE);
          }
        }
      });

      mConfirmIcon.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (checkInputs()) {
            mPresenter.onConfirmIconClick(mDialogNameEditText.getText().toString());
          }
        }
      });

      mAddButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(DialogSettingsActivity.this, DialogAddUsersActivity.class);
          intent.putExtra("dialog_id", mDialogId);
          startActivity(intent);
        }
      });
    } else {
      mLeaveButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          mPresenter.onLeaveButtonClick();
        }
      });
    }
  }

}
