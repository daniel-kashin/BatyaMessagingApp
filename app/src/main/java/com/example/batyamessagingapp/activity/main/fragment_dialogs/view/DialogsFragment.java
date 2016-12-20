package com.example.batyamessagingapp.activity.main.fragment_dialogs.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.activity.main.fragment_dialogs.adapter.DialogsDataModel;
import com.example.batyamessagingapp.activity.main.fragment_dialogs.presenter.DialogsPresenter;
import com.example.batyamessagingapp.activity.main.fragment_dialogs.presenter.DialogsService;
import com.example.batyamessagingapp.activity.main.fragment_dialogs.adapter.DialogAdapter;
import com.example.batyamessagingapp.activity.main.view.MainView;
import com.example.batyamessagingapp.lib.SimpleDividerItemDecoration;

/**
 * Created by Кашин on 26.11.2016.
 */

public class DialogsFragment extends Fragment implements DialogsView {

    private RecyclerView mRecyclerView;
    private TextView mNoDialogsTextView;
    private ProgressBar mProgressBar;
    private View mRootView;

    private DialogsPresenter mPresenter;
    private MainView mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = (MainView)getActivity();
        mRootView = inflater.inflate(R.layout.fragment_view_dialogs, null);

        initializeViews(mRootView);
        initializeRecyclerView(mRootView);

        mPresenter = new DialogsService(this, (Context)mActivity, (DialogsDataModel)mRecyclerView.getAdapter());

        return mRootView;
    }

    @Override
    public void openAuthenticationActivity() {
        if (activityInitialized()) mActivity.openAuthenticationActivity();
    }

    @Override
    public void openChatActivity(String dialogId, String dialogName) {
        if (activityInitialized()) mActivity.openChatActivity(dialogId, dialogName);
    }

    @Override
    public void setNoInternetToolbarLabelText() {
        if (activityInitialized()) mActivity.setToolbarLabelText(getString(R.string.waiting_for_connection));
    }

    @Override
    public void setCommonToolbarLabelText(){
        if (activityInitialized()) mActivity.setToolbarLabelText(getString(R.string.fragment_messages_title));
    }

    @Override
    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void initializeViews(View rootView){
        mNoDialogsTextView = (TextView)rootView.findViewById(R.id.dialogs_no_dialogs_text_view);
        mProgressBar = (ProgressBar)rootView.findViewById(R.id.dialogs_progress_bar);
    }

    @Override
    public void onResume(){
        super.onResume();

        if (activityInitialized()) {
            mActivity.hideSearch();
            mActivity.clearOnToolbarTextListener();
            setCommonToolbarLabelText();
        }

        mPresenter.onLoad();
    }

    @Override
    public void onPause(){
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    public void hideNoDialogsTextView() {
        mNoDialogsTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showNoDialogsTextView() {
        mNoDialogsTextView.setVisibility(View.VISIBLE);
    }

    private boolean activityInitialized(){
        return isAdded() && getActivity()!= null;
    }

    private void initializeRecyclerView(View rootView){
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.dialog_recycler_view);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL,false);

        mRecyclerView.setLayoutManager(manager);

        DialogAdapter adapter = new DialogAdapter(getActivity());
        mRecyclerView.setAdapter(adapter);


        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
    }
}
