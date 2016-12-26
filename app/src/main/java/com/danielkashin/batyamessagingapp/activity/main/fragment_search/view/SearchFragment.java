package com.danielkashin.batyamessagingapp.activity.main.fragment_search.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.danielkashin.batyamessagingapp.R;
import com.danielkashin.batyamessagingapp.activity.main.fragment_search.adapter.UserAdapter;
import com.danielkashin.batyamessagingapp.activity.main.fragment_search.adapter.UserDataModel;
import com.danielkashin.batyamessagingapp.activity.main.fragment_search.presenter.SearchPresenter;
import com.danielkashin.batyamessagingapp.activity.main.fragment_search.presenter.SearchService;
import com.danielkashin.batyamessagingapp.activity.main.view.MainActivity;
import com.danielkashin.batyamessagingapp.activity.main.view.MainView;

/**
 * Created by Кашин on 18.12.2016.
 */

public class SearchFragment extends Fragment implements SearchView {

  private RecyclerView mRecyclerView;
  private TextView mTextView;
  private ProgressBar mProgressBar;
  private View mRootView;

  private SearchPresenter mPresenter;
  private MainView mActivity;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.fragment_search, null);
    mActivity = (MainActivity) getActivity();

    mTextView = (TextView) mRootView.findViewById(R.id.search_username_text_view);
    mProgressBar = (ProgressBar) mRootView.findViewById(R.id.search_progress_bar);
    initializeRecyclerView(mRootView);

    mPresenter = new SearchService(this, (Context) mActivity, (UserDataModel) mRecyclerView.getAdapter());

    return mRootView;
  }

  @Override
  public void onResume() {
    super.onResume();

    if (activityInitialized()) {
      mActivity.showSearchInterface();
      mProgressBar.setVisibility(View.INVISIBLE);
      showNoUsersTextView();
    }

    mPresenter.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void showProgressBar() {
    if (activityInitialized()) {
      mProgressBar.setVisibility(View.VISIBLE);
      hideTextView();
    }
  }

  @Override
  public void showClearIcon() {
    if (activityInitialized()) {
      mActivity.showClearIcon();
    }
  }

  @Override
  public void hideClearIcon() {
    if (activityInitialized()) {
      mActivity.hideClearIcon();
    }
  }

  @Override
  public boolean isInputEmpty() {
    if (activityInitialized()) {
      return mActivity.isInputEmpty();
    } else {
      return true;
    }
  }

  @Override
  public void hideProgressBar() {
    if (activityInitialized()) {
      mProgressBar.setVisibility(View.INVISIBLE);
    }
  }

  @Override
  public void showNoUsersTextView() {
    if (activityInitialized()) {
      mTextView.setText(getString(R.string.no_users));
      mTextView.setVisibility(View.VISIBLE);
      hideProgressBar();
    }
  }

  @Override
  public void showNoInternetConnectionTextView() {
    if (activityInitialized()) {
      mTextView.setText(getString(R.string.no_internet_connection));
      mTextView.setVisibility(View.VISIBLE);
      hideProgressBar();
    }
  }

  @Override
  public void hideTextView() {
    if (activityInitialized()) {
      mTextView.setVisibility(View.INVISIBLE);
    }
  }

  @Override
  public void openAuthenticationActivity() {
    if (activityInitialized()) mActivity.openAuthenticationActivity();
  }

  @Override
  public void setOnToolbarTextListener(TextWatcher textWatcher) {
    if (activityInitialized()) mActivity.setOnToolbarTextListener(textWatcher);
  }

  @Override
  public void openChatActivity(String dialogId, String username) {
    if (activityInitialized()) mActivity.openChatActivity(dialogId, username);
  }

  private boolean activityInitialized() {
    return isAdded() && getActivity() != null;
  }

  private void initializeRecyclerView(View rootView) {
    mRecyclerView = (RecyclerView) rootView.findViewById(R.id.search_recycler_view);

    LinearLayoutManager manager = new LinearLayoutManager(getActivity(),
        LinearLayoutManager.VERTICAL, false);
    mRecyclerView.setLayoutManager(manager);

    UserAdapter adapter = new UserAdapter(getActivity());
    mRecyclerView.setAdapter(adapter);
  }
}


