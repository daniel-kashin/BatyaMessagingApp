package com.example.batyamessagingapp.activity.main.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.activity.authentication.view.AuthenticationActivity;
import com.example.batyamessagingapp.activity.chat.view.ChatActivity;
import com.example.batyamessagingapp.activity.main.fragment_search.view.SearchFragment;
import com.example.batyamessagingapp.activity.main.fragment_settings.view.SettingsFragment;
import com.example.batyamessagingapp.activity.main.fragment_dialogs.view.DialogsFragment;
import com.example.batyamessagingapp.activity.main.presenter.MainPresenter;
import com.example.batyamessagingapp.activity.main.presenter.MainService;

public class MainActivity extends AppCompatActivity implements MainView {

    private ProgressDialog mProgressDialog;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView mToolbarLabel;
    private EditText mToolbarEditText;
    private ImageView mToolbarForwardIcon;
    private View mToolbarRelativeLayout;
    private TextWatcher mCurrentTextWatcher;

    private MainPresenter mPresenter;



    //-----------------------------AppCompatActivity overriden methods------------------------------

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
            if (topFragment instanceof SearchFragment){
                applyFragment(new DialogsFragment());
            } if (mToolbarEditText.getVisibility() == View.VISIBLE) {
                hideSearch();
            }
            return true;
        }

        super.onKeyDown(keyCode, event);
        return true;
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


    //---------------------------------MainView overriden methods-----------------------------------

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
    public void startProgressDialog(String message) {
        mProgressDialog.setMessage(message);
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
    public void showNewConversationInterface() {
        mToolbarLabel.setVisibility(View.INVISIBLE);
        mToolbarForwardIcon.setVisibility(View.VISIBLE);
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
    public void showSearchInterface(){
        mToolbarLabel.setVisibility(View.INVISIBLE);
        mToolbarForwardIcon.setVisibility(View.INVISIBLE);
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
    public void setOnToolbarTextListener(TextWatcher textWatcher) {
        if (mCurrentTextWatcher != null) mToolbarEditText.removeTextChangedListener(mCurrentTextWatcher);

        mCurrentTextWatcher = textWatcher;
        mToolbarEditText.addTextChangedListener(textWatcher);
    }

    @Override
    public void hideSearch() {
        mToolbarLabel.setVisibility(View.VISIBLE);
        mToolbarEditText.setVisibility(View.INVISIBLE);
        mToolbarForwardIcon.setVisibility(View.INVISIBLE);
        mToolbarEditText.setText("");
    }

    @Override
    public void setOnToolbarDefaultTextListener() {
        setOnToolbarTextListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0 && mToolbarEditText.getVisibility() == View.VISIBLE) {
                    mToolbarForwardIcon.setVisibility(View.VISIBLE);
                } else {
                    mToolbarForwardIcon.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    //-----------------------------------------private methods--------------------------------------

    private void setListeners() {
        mToolbarForwardIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onForwardIconButtonClick(
                        mToolbarEditText.getText().toString());
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
                hideSearch();
                super.onDrawerOpened(drawerView);
                super.onDrawerSlide(drawerView, 0);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset){
                super.onDrawerSlide(drawerView, 0);
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

        //call auth button onClick when user finished to write the password
        mToolbarEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    mToolbarForwardIcon.callOnClick();
                }
                return false;
            }
        });
    }

    private void initializeViews() {
        //toolbar
        mToolbar = (Toolbar) findViewById(R.id.dialogs_toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        mToolbarForwardIcon = (ImageView) findViewById(R.id.dialogs_toolbar_forward_icon);
        mToolbarEditText = (EditText) findViewById(R.id.dialogs_toolbar_edit_text);
        mToolbarLabel = (TextView) findViewById(R.id.dialogs_toolbar_label);
        mToolbarRelativeLayout = findViewById(R.id.dialogs_toolbar_relative_layout);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.dialogs_drawer_layout);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle(null);
        mProgressDialog.setCancelable(false);
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
                    if (holder.getAdapterPosition() == 0) {
                        applyFragment(new SearchFragment());
                    } else if (holder.getAdapterPosition() == 1) {
                        setOnToolbarDefaultTextListener();
                        showNewConversationInterface();
                    } else if (holder.getAdapterPosition() == 2) {
                        applyFragment(new DialogsFragment());
                    } else {
                        applyFragment(new SettingsFragment());
                    }
                    mDrawerLayout.closeDrawers();
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