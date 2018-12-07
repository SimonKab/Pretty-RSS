package com.simonk.project.ppoproject.ui.rss.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.simonk.project.ppoproject.R;
import com.simonk.project.ppoproject.databinding.HistoryListItemBinding;
import com.simonk.project.ppoproject.databinding.RssListItemBinding;
import com.simonk.project.ppoproject.model.HistoryEntry;
import com.simonk.project.ppoproject.rss.RssChannel;
import com.simonk.project.ppoproject.rss.RssDataProvider;
import com.simonk.project.ppoproject.ui.util.ObjectListAdapter;
import com.simonk.project.ppoproject.utils.DateUtils;

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
            void onClicked(View v, String path);
        }

        public HistoryListViewHolder(@NonNull HistoryListItemBinding binding, ClickListener clickListener) {
            super(binding.getRoot());
            mClickListener = clickListener;

            mText = binding.historyListItemPath;

            binding.getRoot().setOnClickListener(v -> {
                if (mClickListener != null) {
                    mClickListener.onClicked(v, mItem.getPath());
                }
            });
        }

        void bind(HistoryEntry item) {
            mItem = item;
            mText.setText(item.getPath());
        }
    }

}
