package com.example.batyamessagingapp.activity.dialogs.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
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
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.activity.authentication.view.AuthenticationActivity;
import com.example.batyamessagingapp.activity.chat.view.ChatActivity;
import com.example.batyamessagingapp.activity.dialogs.fragment_settings.view.SettingsFragment;
import com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.view.ViewDialogsFragment;
import com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.view.ViewDialogsView;
import com.example.batyamessagingapp.activity.dialogs.presenter.DialogsPresenter;
import com.example.batyamessagingapp.activity.dialogs.presenter.DialogsService;

public class DialogsActivity extends AppCompatActivity implements DialogsView {

    private ProgressDialog mProgressDialog;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView mToolbarLabel;
    private EditText mToolbarEditText;
    private ImageView mToolbarForwardIcon;
    private ImageView mToolbarRefreshIcon;

    private DialogsPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);

        initializeViews();
        setListeners();

        mPresenter = new DialogsService(this);
        applyFragment(new ViewDialogsFragment());
    }
/*
    private void applyFragment(Fragment fragment) {
        FragmentManager manager = getFragmentManager();
        String addingFragmentName = fragment.getClass().getName();

        boolean addFragment = manager.getBackStackEntryCount() == 0
                || !manager.getBackStackEntryAt(manager.getBackStackEntryCount() - 1).getName()
                .equals(addingFragmentName);

        if (addFragment) {
            boolean fragmentPopped = manager.popBackStackImmediate(addingFragmentName, 0);
            if (!fragmentPopped && manager.findFragmentByTag(addingFragmentName) == null) {
                mFragmentTransaction = manager.beginTransaction();
                mFragmentTransaction.add(R.id.fragment_container, fragment);
                mFragmentTransaction.addToBackStack(addingFragmentName);
                mFragmentTransaction.commit();
            }
        }
    }
*/

    private void applyFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();

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
    public void openChatActivity(String dialogId) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("dialog_id", dialogId);
        startActivity(intent);
    }

    /*
    @Override
    public void refreshToolbarLabelText() {
        if (!mToolbarLabel.getText().toString().equals(getString(R.string.no_internet_connection))) {
            FragmentManager manager = getFragmentManager();
            String topName = manager.getBackStackEntryAt(manager.getBackStackEntryCount() - 1).getName();

            if (topName.equals(mViewDialogsFragment.getClass().getName())) {
                setToolbarLabelText(getString(R.string.fragment_messages_title));
            } else if (topName.equals(mSettingsFragment.getClass().getName())) {
                setToolbarLabelText(getString(R.string.fragment_settings_title));
            }
        }
    }
*/

    private void setListeners() {
        mToolbarForwardIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onForwardIconButtonClick(
                        mToolbarEditText.getText().toString());
            }
        });
        /*
        mToolbarRefreshIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideRefreshIcon();
                ((ViewDialogsView) mViewDialogsFragment).onRefreshIconClick();
            }
        });
*/

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolbar, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                hideSearch();
                super.onDrawerOpened(drawerView);
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

        mToolbarEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0 && mToolbarEditText.getVisibility() == View.VISIBLE) {
                    mToolbarForwardIcon.setVisibility(View.VISIBLE);
                } else {
                    mToolbarForwardIcon.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
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

    private void initializeViews() {
        //toolbar
        mToolbar = (Toolbar) findViewById(R.id.dialogs_toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.dialogs_drawer_layout);
        mToolbarForwardIcon = (ImageView) findViewById(R.id.dialogs_toolbar_forward_icon);
        mToolbarRefreshIcon = (ImageView) findViewById(R.id.dialogs_toolbar_refresh_icon);
        mToolbarEditText = (EditText) findViewById(R.id.dialogs_toolbar_edit_text);
        mToolbarLabel = (TextView) findViewById(R.id.dialogs_toolbar_label);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle(null);
        mProgressDialog.setCancelable(false);
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
    protected void onResume() {
        super.onResume();

        hideSearch();
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
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0
                && mToolbarEditText.getVisibility() == View.VISIBLE) {
            hideSearch();
        } else {
            finish();
        }
        return true;
    }

    @Override
    public void showSearch() {
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
    public void hideSearch() {

        mToolbarLabel.setVisibility(View.VISIBLE);
        mToolbarEditText.setVisibility(View.INVISIBLE);
        mToolbarForwardIcon.setVisibility(View.INVISIBLE);
        mToolbarEditText.setText("");
    }

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
            holder.imageView.setImageResource(mIcons[position]);// Settimg the image with array of our icons
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.getAdapterPosition() == 0) {

                    } else if (holder.getAdapterPosition() == 1) {
                        showSearch();
                    } else if (holder.getAdapterPosition() == 2) {
                        applyFragment(new ViewDialogsFragment());
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
