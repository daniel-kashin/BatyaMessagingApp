package com.example.batyamessagingapp.activity.main.fragment_search.view;

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

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.activity.main.fragment_search.adapter.User;
import com.example.batyamessagingapp.activity.main.fragment_search.adapter.UserAdapter;
import com.example.batyamessagingapp.activity.main.fragment_search.adapter.UserDataModel;
import com.example.batyamessagingapp.activity.main.fragment_search.presenter.SearchPresenter;
import com.example.batyamessagingapp.activity.main.fragment_search.presenter.SearchService;
import com.example.batyamessagingapp.activity.main.view.MainView;

import java.util.ArrayList;

/**
 * Created by Кашин on 18.12.2016.
 */

public class SearchFragment extends Fragment implements SearchView {

    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private ProgressBar mProgressBar;

    private SearchPresenter mPresenter;
    private MainView mActivity;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, null);

        mTextView = (TextView) rootView.findViewById(R.id.search_text_view);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.search_progress_bar);
        initializeRecyclerView(rootView);

        mActivity = (MainView)getActivity();
        mPresenter = new SearchService(this, getActivity(), (UserDataModel)mRecyclerView.getAdapter());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
        if (activityInitialized()) {
            mActivity.showSearchInterface();
            showNoUsersTextView();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if (activityInitialized()) {
            mActivity.hideSearch();
            mProgressBar.setVisibility(View.INVISIBLE);
        }
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
        if (activityInitialized()){
            mActivity.showClearIcon();
        }
    }

    @Override
    public void hideClearIcon() {
        if (activityInitialized()){
            mActivity.hideClearIcon();
        }
    }

    @Override
    public void hideProgressBar() {
        if (activityInitialized()){
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
            mTextView.setVisibility(View.INVISIBLE);
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

    private boolean activityInitialized(){
        return isAdded() && getActivity()!= null;
    }

    private void initializeRecyclerView(View rootView){
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.search_recycler_view);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(manager);

        UserAdapter adapter = new UserAdapter(getActivity());
        mRecyclerView.setAdapter(adapter);
    }
}


