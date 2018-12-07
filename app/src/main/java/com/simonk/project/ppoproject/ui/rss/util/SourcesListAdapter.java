package com.simonk.project.ppoproject.ui.rss.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simonk.project.ppoproject.R;
import com.simonk.project.ppoproject.databinding.HistoryListItemBinding;
import com.simonk.project.ppoproject.databinding.SourceListItemBinding;
import com.simonk.project.ppoproject.model.HistoryEntry;
import com.simonk.project.ppoproject.ui.util.ObjectListAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class SourcesListAdapter extends ObjectListAdapter<HistoryEntry, SourcesListAdapter.HistoryListViewHolder> {

    private HistoryListViewHolder.ClickListener mClickListener;

    @NonNull
    @Override
    public HistoryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        SourceListItemBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.source_list_item, parent, false);

        return new HistoryListViewHolder(binding, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryListViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public void setClickListener(HistoryListViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public static class HistoryListViewHolder extends RecyclerView.ViewHolder {

        private ClickListener mClickListener;
        private HistoryEntry mItem;

        private TextView mName;
        private TextView mText;

        public interface ClickListener {
            void onClicked(View v, HistoryEntry historyEntry);
            void onLongClicked(View v, HistoryEntry historyEntry);
        }

        public HistoryListViewHolder(@NonNull SourceListItemBinding binding, ClickListener clickListener) {
            super(binding.getRoot());
            mClickListener = clickListener;

            mName = binding.sourceListItemName;
            mText = binding.sourceListItemPath;

            binding.getRoot().setOnClickListener(v -> {
                if (mClickListener != null) {
                    mClickListener.onClicked(v, mItem);
                }
            });

            binding.getRoot().setOnLongClickListener(v -> {
                if (mClickListener != null) {
                    mClickListener.onLongClicked(v, mItem);
                    return true;
                }
                return false;
            });
        }

        void bind(HistoryEntry item) {
            mItem = item;
            mName.setText(item.getName());
            mText.setText(item.getPath());
        }
    }

}
