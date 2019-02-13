package com.simonk.project.prettyrss.ui.rss;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.FirebaseException;
import com.simonk.project.prettyrss.R;
import com.simonk.project.prettyrss.databinding.TabHistoryFragmentBinding;
import com.simonk.project.prettyrss.model.HistoryEntry;
import com.simonk.project.prettyrss.repository.RssRepository;
import com.simonk.project.prettyrss.ui.rss.util.HistoryListAdapter;
import com.simonk.project.prettyrss.ui.rss.util.SourcesListAdapter;
import com.simonk.project.prettyrss.ui.util.ObjectListAdapter;
import com.simonk.project.prettyrss.viewmodels.HistoryListViewModel;

import java.util.Collections;
import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TabHistoryFragment extends Fragment {

    private static final String SOURCE_ARGUMENT = "SOURCE_ARGUMENT";

    private static final String SOURCE_DIALOG_FRAGMENT_TAG = "SOURCE_DIALOG_FRAGMENT_TAG";

    private RecyclerView mList;

    private ObjectListAdapter mAdapter;

    private HistoryListViewModel mViewModel;

    public static TabHistoryFragment getInstance(boolean sources) {
        Bundle arguments = new Bundle();
        arguments.putBoolean(SOURCE_ARGUMENT, sources);

        TabHistoryFragment fragment = new TabHistoryFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        TabHistoryFragmentBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.tab_history_fragment, parent, false);
        init(binding);

        mViewModel = ViewModelProviders.of(requireActivity()).get(HistoryListViewModel.class);

        if (getArguments().getBoolean(SOURCE_ARGUMENT)) {
            SourcesListAdapter adapter = new SourcesListAdapter();
            adapter.setClickListener(new SourcesListAdapter.HistoryListViewHolder.ClickListener() {
                @Override
                public void onClicked(View v, HistoryEntry historyEntry) {
                    mViewModel.setEditSource(historyEntry.getPath());
                }

                @Override
                public void onLongClicked(View v, HistoryEntry historyEntry) {
                    SourceDialogFragment sourceDialogFragment = SourceDialogFragment.getInstance(historyEntry);
                    sourceDialogFragment.show(requireFragmentManager(), SOURCE_DIALOG_FRAGMENT_TAG);
                }
            });
            mAdapter = adapter;
        } else {
            HistoryListAdapter adapter = new HistoryListAdapter();
            adapter.setClickListener(new HistoryListAdapter.HistoryListViewHolder.ClickListener() {
                @Override
                public void onClicked(View v, HistoryEntry historyEntry) {
                    mViewModel.setEditSource(historyEntry.getPath());
                }

                @Override
                public void onLongClicked(View v, HistoryEntry historyEntry) {
                    HistoryEntry entry = new HistoryEntry();
                    entry.setPath(historyEntry.getPath());
                    SourceDialogFragment sourceDialogFragment = SourceDialogFragment.getInstance(entry);
                    sourceDialogFragment.show(requireFragmentManager(), SOURCE_DIALOG_FRAGMENT_TAG);
                }
            });
            mAdapter = adapter;
        }
        mList.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        mList.setAdapter(mAdapter);

        updateUi();

        return binding.getRoot();
    }

    private void updateUi() {
        if (getArguments().getBoolean(SOURCE_ARGUMENT)) {
            mViewModel.getSources().observe(this, new Observer<RssRepository.DatabaseResult<List<HistoryEntry>>>() {
                @Override
                public void onChanged(RssRepository.DatabaseResult<List<HistoryEntry>> listDatabaseResult) {
                    if (listDatabaseResult == null) {
                        return;
                    }

                    if (listDatabaseResult.data != null) {
                        List<HistoryEntry> historyEntries = listDatabaseResult.data;
                        Collections.reverse(historyEntries);

                        mAdapter.resolveActionChange(() -> {
                            mAdapter.setItemsList(historyEntries);
                        });
                    }
                }
            });
        } else {
            mViewModel.getHistory().observe(this, new Observer<RssRepository.DatabaseResult<List<HistoryEntry>>>() {
                @Override
                public void onChanged(RssRepository.DatabaseResult<List<HistoryEntry>> listDatabaseResult) {
                    if (listDatabaseResult == null) {
                        return;
                    }

                    if (listDatabaseResult.data != null) {
                        List<HistoryEntry> historyEntries = listDatabaseResult.data;
                        Collections.reverse(historyEntries);

                        mAdapter.resolveActionChange(() -> {
                            mAdapter.setItemsList(historyEntries);
                        });
                    }
                }
            });
        }
    }

    private void init(TabHistoryFragmentBinding binding) {
        mList = binding.tabHistoryFragmentList;
    }

}
