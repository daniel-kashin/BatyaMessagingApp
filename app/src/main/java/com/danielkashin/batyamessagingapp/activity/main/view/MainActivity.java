package com.danielkashin.batyamessagingapp.activity.main.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.danielkashin.batyamessagingapp.R;
import com.danielkashin.batyamessagingapp.activity.authentication.view.AuthenticationActivity;
import com.danielkashin.batyamessagingapp.activity.chat.view.ChatActivity;
import com.danielkashin.batyamessagingapp.activity.main.fragment_search.view.SearchFragment;
import com.danielkashin.batyamessagingapp.activity.main.fragment_settings.view.SettingsFragment;
import com.danielkashin.batyamessagingapp.activity.main.fragment_dialogs.view.DialogsFragment;
import com.danielkashin.batyamessagingapp.activity.main.presenter.MainPresenter;
import com.danielkashin.batyamessagingapp.activity.main.presenter.MainService;
import com.danielkashin.batyamessagingapp.model.BasicAsyncTask;
import com.danielkashin.batyamessagingapp.model.NetworkService;
import com.danielkashin.batyamessagingapp.model.pojo.GroupId;

import java.util.regex.Pattern;

import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements MainView {

  private ProgressDialog mProgressDialog;
  private Toolbar mToolbar;
  private DrawerLayout mDrawerLayout;
  private ActionBarDrawerToggle mDrawerToggle;
  private TextView mToolbarLabel;
  private EditText mToolbarEditText;
  private ImageView mToolbarClearIcon;
  private View mToolbarRelativeLayout;
  private TextWatcher mCurrentTextWatcher;

  private MainPresenter mPresenter;


  //---------------------------------AppCompatActivity methods------------------------------------

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    initializeViews();
    setListeners();

    mPresenter = new MainService(this);
    applyFragment(new DialogsFragment());
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (mDrawerToggle.onOptionsItemSelected(item)) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
      FragmentManager fragmentManager = getSupportFragmentManager();
      Fragment topFragment = fragmentManager.findFragmentById(R.id.fragment_container);
      boolean actionPerformed = false;

      if (topFragment instanceof SearchFragment || topFragment instanceof SettingsFragment) {
        applyFragment(new DialogsFragment());
        actionPerformed = true;
      }
      if (mToolbarEditText.getVisibility() == View.VISIBLE) {
        hideSearch();
        actionPerformed = true;
      }

      if (actionPerformed) {
        return false;
      }
    }

    return super.onKeyDown(keyCode, event);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    mDrawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    mDrawerToggle.onConfigurationChanged(newConfig);
  }


  //-------------------------------------MainView methods-----------------------------------------

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
  public void openAuthenticationActivity() {
    Intent intent = new Intent(this, AuthenticationActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
        | Intent.FLAG_ACTIVITY_NO_ANIMATION);
    startActivity(intent);
    finish();
  }

  @Override
  public void openChatActivity(String dialogId, String dialogName) {
    Intent intent = new Intent(this, ChatActivity.class);
    intent.putExtra("dialog_id", dialogId);
    intent.putExtra("dialog_name", dialogName);
    startActivity(intent);
  }


  @Override
  public void setToolbarLabelText(String newLabel) {
    mToolbarLabel.setText(newLabel);
  }

  @Override
  public ProgressDialog getProgressDialog() {
    return mProgressDialog;
  }

  @Override
  public void startProgressDialog() {
    if (!mProgressDialog.isShowing()) {
      mProgressDialog.show();
    }
  }

  @Override
  public void stopProgressDialog() {
    if (mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
    }
  }

  @Override
  public void showSearchInterface() {
    mToolbarLabel.setVisibility(View.INVISIBLE);
    mToolbarClearIcon.setVisibility(View.INVISIBLE);
    mToolbarEditText.setVisibility(View.VISIBLE);
    mToolbarEditText.post(new Runnable() {
      @Override
      public void run() {
        mToolbarEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mToolbarEditText, InputMethodManager.SHOW_IMPLICIT);
      }
    });
  }

  @Override
  public void clearOnToolbarTextListener() {
    if (mCurrentTextWatcher != null) {
      mToolbarEditText.removeTextChangedListener(mCurrentTextWatcher);
    }
  }

  @Override
  public void setOnToolbarTextListener(TextWatcher textWatcher) {
    clearOnToolbarTextListener();

    mCurrentTextWatcher = textWatcher;
    mToolbarEditText.addTextChangedListener(textWatcher);
  }

  @Override
  public void hideSearch() {
    mToolbarLabel.setVisibility(View.VISIBLE);
    mToolbarEditText.setVisibility(View.INVISIBLE);
    mToolbarClearIcon.setVisibility(View.INVISIBLE);
    mToolbarEditText.setText("");
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

  //-----------------------------------------private methods--------------------------------------

  private void setListeners() {
    mToolbarClearIcon.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mToolbarEditText.setText("");
      }
    });

    mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
        mToolbar, R.string.app_name, R.string.app_name) {
      @Override
      public void onDrawerClosed(View view) {
        super.onDrawerClosed(view);
      }

      @Override
      public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        super.onDrawerSlide(drawerView, 0);
      }

      @Override
      public void onDrawerSlide(View drawerView, float slideOffset) {
      }
    };
    mDrawerToggle.setDrawerIndicatorEnabled(true);
    mDrawerLayout.addDrawerListener(mDrawerToggle);
    String[] actions = {"New Group", "New Chat", "Messages", "Settings"};
    int[] icons = {R.drawable.vector_new_group, R.drawable.vector_new_contact,
        R.drawable.vector_messages, R.drawable.vector_settings};
    RecyclerView drawerRecyclerView = (RecyclerView) findViewById(R.id.dialogs_drawer_recycler_view);
    drawerRecyclerView.setAdapter(new DrawerAdapter(actions, icons));
    drawerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
  }

  private void initializeViews() {
    //toolbar
    mToolbar = (Toolbar) findViewById(R.id.dialogs_toolbar);
    setSupportActionBar(mToolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);
    }
    mToolbarClearIcon = (ImageView) findViewById(R.id.dialogs_toolbar_clear_icon);
    mToolbarEditText = (EditText) findViewById(R.id.dialogs_toolbar_edit_text);
    mToolbarLabel = (TextView) findViewById(R.id.dialogs_toolbar_label);
    mToolbarRelativeLayout = findViewById(R.id.dialogs_toolbar_relative_layout);

    mDrawerLayout = (DrawerLayout) findViewById(R.id.dialogs_drawer_layout);

    mProgressDialog = new ProgressDialog(this);
    mProgressDialog.setIndeterminate(true);
    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    mProgressDialog.setTitle(null);
    mProgressDialog.setCancelable(false);
    mProgressDialog.setMessage(getString(R.string.loading));
  }

  private void applyFragment(Fragment fragment) {
    FragmentManager fragmentManager = getSupportFragmentManager();

    Fragment topFragment = fragmentManager.findFragmentById(R.id.fragment_container);

    if (topFragment == null) {
      FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
      fragmentTransaction.add(R.id.fragment_container, fragment);
      fragmentTransaction.commit();
    } else if (!fragment.getClass().getName().equals(topFragment.getClass().getName())) {
      FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
      fragmentTransaction.replace(R.id.fragment_container, fragment);
      fragmentTransaction.commit();
    }
  }

  private void handleCreateNewConference() {
    // create alertdialog
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
    alertDialog.setTitle("Choose group name");

    // create its layout
    LinearLayout layout = new LinearLayout(this);
    layout.setOrientation(LinearLayout.VERTICAL);
    layout.setPadding(30, 0, 30, 0);

    // create an input
    final EditText newGroupnameEditText = new EditText(this);
    newGroupnameEditText.setHint("Group name");
    layout.addView(newGroupnameEditText);

    alertDialog.setView(layout);

    // set positive button listener
    DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        final String newUsername = newGroupnameEditText.getText().toString();

        Pattern usernamePattern = Pattern.compile("[A-Za-z0-9_-]+");

        if (!usernamePattern.matcher(newUsername).matches()) {
          showAlert("Group name can contain only letters, numbers and _-", "Error");
        } else if (newUsername.length() < 2 || newUsername.length() > 256) {
          showAlert("Group name length should be 2..256 characters long",
              "Error");
        } else { // name is correct
          BasicAsyncTask.AsyncTaskCompleteListener
              <Pair<GroupId, BasicAsyncTask.ErrorType>> createGroupCallback =
              new BasicAsyncTask.AsyncTaskCompleteListener<Pair<GroupId, BasicAsyncTask.ErrorType>>() {
                @Override
                public void onTaskComplete(Pair<GroupId, BasicAsyncTask.ErrorType> result) {
                  if (result.second == BasicAsyncTask.ErrorType.NoError) { // the group was created

                    BasicAsyncTask.AsyncTaskCompleteListener
                        <Pair<ResponseBody, BasicAsyncTask.ErrorType>> changeNameCallback =
                        new BasicAsyncTask.AsyncTaskCompleteListener
                            <Pair<ResponseBody, BasicAsyncTask.ErrorType>>() {
                          @Override
                          public void onTaskComplete(Pair<ResponseBody, BasicAsyncTask.ErrorType> result) {
                            if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection) {
                              showAlert("No internet connection. The group was created. You can set" +
                                  "its name later", "Error");
                            } else if (result.second == BasicAsyncTask.ErrorType.NoAccess) {
                              showAlert("The group was created. You can set its name later", "Error");
                            } else {
                              Toast.makeText(MainActivity.this, "The group was successfully created",
                                  Toast.LENGTH_LONG).show();
                            }
                          }
                        };

                    new BasicAsyncTask<ResponseBody>(
                        NetworkService.getChangeUsernameCall(newUsername, result.first.toString()),
                        MainActivity.this,
                        false,
                        changeNameCallback).execute();
                  } else if (result.second == BasicAsyncTask.ErrorType.NoInternetConnection) {
                    showAlert("No internet connection. Group was not created", "Error");
                  } else {
                    openAuthenticationActivity();
                  }
                }
              };

          new BasicAsyncTask<GroupId>(
              NetworkService.getGetNewGroupIdCall(),
              MainActivity.this,
              false,
              createGroupCallback
          ).execute();
        }
      }
    };

    alertDialog.setPositiveButton("OK", positiveListener);

    // set negative button listener
    alertDialog.setNegativeButton("CANCEL",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
          }
        });

    alertDialog.show();
  }


//--------------------------------------inner classes-------------------------------------------

  private class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {
    private String mTitles[];
    private int mIcons[];

    DrawerAdapter(String titles[], int icons[]) {
      mTitles = titles;
      mIcons = icons;
    }

    @Override
    public DrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View item = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_drawer, parent, false);
      return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(final DrawerAdapter.ViewHolder holder, int position) {
      holder.textView.setText(mTitles[position]); // Setting the Text with the array of our Titles
      holder.imageView.setImageResource(mIcons[position]);// Setting the image with array of our icons
      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Handler handler = new Handler();
          if (holder.getAdapterPosition() == 0) {         // new conference
            mDrawerLayout.closeDrawers();
            handler.postDelayed(new Runnable() {
              @Override
              public void run() {
                handleCreateNewConference();
              }
            }, 200);
          } else if (holder.getAdapterPosition() == 1) {  // new chat
            mDrawerLayout.closeDrawers();
            handler.postDelayed(new Runnable() {
              @Override
              public void run() {
                applyFragment(new SearchFragment());
              }
            }, 100);
          } else if (holder.getAdapterPosition() == 2) {  // messages
            mDrawerLayout.closeDrawers();
            handler.postDelayed(new Runnable() {
              @Override
              public void run() {
                applyFragment(new DialogsFragment());
              }
            }, 300);
          } else {                                        // settings
            mDrawerLayout.closeDrawers();
            handler.postDelayed(new Runnable() {
              @Override
              public void run() {
                applyFragment(new SettingsFragment());
              }
            }, 350);
          }
        }
      });
    }

    @Override
    public int getItemCount() {
      return mTitles.length; // the number of items in the list will be +1 the titles including the header view.
    }

    class ViewHolder extends RecyclerView.ViewHolder {
      TextView textView;
      ImageView imageView;

      ViewHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.drawer_item_text);
        imageView = (ImageView) itemView.findViewById(R.id.drawer_item_icon);
      }
    }
  }
}
