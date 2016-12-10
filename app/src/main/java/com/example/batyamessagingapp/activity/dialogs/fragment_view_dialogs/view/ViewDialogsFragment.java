package com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.batyamessagingapp.R;
import com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.presenter.ViewDialogsPresenter;
import com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.presenter.ViewDialogsService;
import com.example.batyamessagingapp.activity.dialogs.view.DialogsActivity;
import com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.adapter.DialogAdapter;
import com.example.batyamessagingapp.activity.dialogs.fragment_view_dialogs.adapter.OnDialogClickListener;
import com.example.batyamessagingapp.activity.dialogs.view.DialogsView;
import com.example.batyamessagingapp.lib.SimpleDividerItemDecoration;

/**
 * Created by Кашин on 26.11.2016.
 */

public class ViewDialogsFragment extends Fragment implements ViewDialogsView {

    private RecyclerView mRecyclerView;
    private ViewDialogsPresenter mPresenter;
    private TextView mNoDialogsTextView;

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

        mPresenter = new ViewDialogsService(this, getActivity(), (DialogAdapter)mRecyclerView.getAdapter());
        mPresenter.onLoad();

        return rootView;
    }

    private void initializeViews(View rootView){
        mNoDialogsTextView = (TextView)rootView.findViewById(R.id.dialogs_no_dialogs_text_view);
    }

    @Override
    public void onResume(){
        super.onResume();
        ((DialogsView)getActivity()).setToolbarLabel(getString(R.string.fragment_messages_title));
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
        adapter.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, int position) {
                String dialogId = ((DialogAdapter)adapter).getDialogIdByPosition(position);
                ((DialogsActivity)getActivity()).openChatActivity(dialogId);
            }
        });
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
    }

}
