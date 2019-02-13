package com.simonk.project.prettyrss.ui.rss.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.simonk.project.prettyrss.R;
import com.simonk.project.prettyrss.databinding.HistoryListItemBinding;
import com.simonk.project.prettyrss.model.HistoryEntry;
import com.simonk.project.prettyrss.ui.util.ObjectListAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryListAdapter extends ObjectListAdapter<HistoryEntry, HistoryListAdapter.HistoryListViewHolder> {

    private HistoryListViewHolder.ClickListener mClickListener;

    @NonNull
    @Override
    public HistoryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        HistoryListItemBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.history_list_item, parent, false);

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

        private TextView mText;

        public interface ClickListener {
            void onClicked(View v, HistoryEntry historyEntry);
            void onLongClicked(View v, HistoryEntry historyEntry);
        }

        public HistoryListViewHolder(@NonNull HistoryListItemBinding binding, ClickListener clickListener) {
            super(binding.getRoot());
            mClickListener = clickListener;

            mText = binding.historyListItemPath;

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
            mText.setText(item.getPath());
        }
    }

}
