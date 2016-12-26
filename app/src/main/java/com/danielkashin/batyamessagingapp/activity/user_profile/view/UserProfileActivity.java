package com.danielkashin.batyamessagingapp.activity.user_profile.view;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.danielkashin.batyamessagingapp.R;
import com.danielkashin.batyamessagingapp.activity.user_profile.adapter.GrammarDataAdapter;
import com.danielkashin.batyamessagingapp.activity.user_profile.adapter.GrammarDataModel;
import com.danielkashin.batyamessagingapp.activity.user_profile.presenter.UserProfilePresenter;
import com.danielkashin.batyamessagingapp.activity.user_profile.presenter.UserProfileService;
import com.danielkashin.batyamessagingapp.lib.CircleBitmapFactory;

public class UserProfileActivity extends AppCompatActivity implements UserProfileView {

  private Toolbar mToolbar;
  private RecyclerView mRecyclerView;
  private TextView mToolbarLabel;
  private EditText mIdEditText;
  private EditText mUsernameEditText;

  private String mDialogId;
  private String mDialogName;

  private UserProfilePresenter mPresenter;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_profile);

    mDialogId = getIntent().getStringExtra("dialog_id");
    mDialogName = getIntent().getStringExtra("dialog_name");

    initializeViews();

    mPresenter = new UserProfileService((UserProfileView) this, mDialogId,
        (GrammarDataModel) mRecyclerView.getAdapter());
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
  public void setNoInternetConnectionToolbarLabel() {
    mToolbarLabel.setText(getString(R.string.waiting_for_connection));
  }

  @Override
  public void setCommonToolbarLabel() {
    mToolbarLabel.setText("User profile");
  }

  @Override
  public void setLoadingToolbarLabel() {
    mToolbarLabel.setText("Loading...");
  }

  @Override
  public void openChatActivity() {
    finish();
  }

  private void initializeViews() {
    //toolbar
    mToolbar = (Toolbar) findViewById(R.id.user_profile_toolbar);
    setSupportActionBar(mToolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);
    }
    mToolbarLabel = (TextView) findViewById(R.id.user_profile_toolbar_label);

    LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    mRecyclerView = (RecyclerView) findViewById(R.id.user_profile_recycler_view);
    mRecyclerView.setLayoutManager(manager);
    mRecyclerView.setAdapter(new GrammarDataAdapter(this));

    mIdEditText = (EditText) findViewById(R.id.user_profile_id_edit_text);
    mIdEditText.setText(mDialogId);
    mIdEditText.setEnabled(false);

    mUsernameEditText = (EditText) findViewById(R.id.user_profile_username_edit_text);
    mUsernameEditText.setText(mDialogName);
    mUsernameEditText.setEnabled(false);

    Bitmap bitmap = CircleBitmapFactory.generateCircleBitmap(
        this,
        CircleBitmapFactory.getMaterialColor(mDialogId.hashCode()),
        75,
        CircleBitmapFactory.getFirstLetter(mDialogName)
    );
    ((ImageView) findViewById(R.id.user_profile_main_icon)).setImageBitmap(bitmap);
  }


}
