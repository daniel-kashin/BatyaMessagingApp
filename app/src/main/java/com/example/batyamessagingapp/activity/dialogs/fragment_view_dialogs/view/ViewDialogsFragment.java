package com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.adapter.DialogsDataModel;
import com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.presenter.ViewDialogsPresenter;
import com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.presenter.ViewDialogsService;
import com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.adapter.DialogAdapter;
import com.example.batyamessagingapp.activity.dialogs.view.DialogsView;
import com.example.batyamessagingapp.lib.SimpleDividerItemDecoration;

/**
 * Created by Кашин on 26.11.2016.
 */

public class ViewDialogsFragment extends Fragment implements ViewDialogsView {

    private RecyclerView mRecyclerView;
    private TextView mNoDialogsTextView;

    private ViewDialogsPresenter mPresenter;
    private DialogsView mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_dialogs, null);

        initializeViews(rootView);
        initializeRecyclerView(rootView);

        mActivity = (DialogsView)getActivity();
        mPresenter = new ViewDialogsService(this, getActivity(), (DialogsDataModel)mRecyclerView.getAdapter());
        mPresenter.onLoad();

        return rootView;
    }

    @Override
    public void openAuthenticationActivity() {
        mActivity.openAuthenticationActivity();
    }

    @Override
    public void showNoInternetConnection() {
        mActivity.showRefreshIcon();
        mActivity.setToolbarLabelText(getString(R.string.no_internet_connection));
    }

    @Override
    public void onRefreshIconClick() {
        mPresenter.onLoad();
    }

    @Override
    public void refreshToolbarLabelText(){
        mActivity.refreshToolbarLabelText();
    }

    @Override
    public Activity getParentActivity(){
        return getActivity();
    }

    private void initializeViews(View rootView){
        mNoDialogsTextView = (TextView)rootView.findViewById(R.id.dialogs_no_dialogs_text_view);
    }

    @Override
    public void onResume(){
        super.onResume();
        refreshToolbarLabelText();
    }

    @Override
    public void hideNoDialogsTextView() {
        mNoDialogsTextView.setVisibility(View.INVISIBLE);
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
