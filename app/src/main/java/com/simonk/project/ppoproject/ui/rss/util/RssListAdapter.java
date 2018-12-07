package com.simonk.project.ppoproject.ui.rss.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.simonk.project.ppoproject.R;
import com.simonk.project.ppoproject.databinding.RssListItemBinding;
import com.simonk.project.ppoproject.rss.RssChannel;
import com.simonk.project.ppoproject.rss.RssDataProvider;
import com.simonk.project.ppoproject.ui.util.ObjectListAdapter;
import com.simonk.project.ppoproject.utils.DateUtils;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class RssListAdapter extends ObjectListAdapter<RssChannel.Item, RssListAdapter.RssListViewHolder> {

    private RssListViewHolder.ClickListener mClickListener;

    @NonNull
    @Override
    public RssListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        RssListItemBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.rss_list_item, parent, false);

        return new RssListViewHolder(binding, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RssListViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public void setClickListener(RssListViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public static class RssListViewHolder extends RecyclerView.ViewHolder {

        private ClickListener mClickListener;
        private RssChannel.Item mItem;

        private TextView mTitle;
        private TextView mDescription;
        private TextView mDate;

        private ImageView mImage;

        public interface ClickListener {
            void onRssClicked(View v, String link);
        }

        public RssListViewHolder(@NonNull RssListItemBinding binding, ClickListener clickListener) {
            super(binding.getRoot());
            mClickListener = clickListener;

            mTitle = binding.rssListItemTitle;
            mDescription = binding.rssListItemDescription;
            mDate = binding.rssListItemPublication;
            mImage = binding.rssListItemImage;

            binding.getRoot().setOnClickListener(v -> {
                if (mClickListener != null) {

                    mClickListener.onRssClicked(v, RssDataProvider.getValidLink(mItem.getLinks()));
                }
            });
        }

        void bind(RssChannel.Item item) {
            mItem = item;

            mTitle.setText(item.getTitle());
            mDescription.setText(item.getNormalizedDescription());
            mDate.setText(DateUtils.convertRssDateToLocale(itemView.getContext(), item.getPubDate()));

            String url = RssDataProvider.getImageUrlForItem(mItem);
            if (url != null) {
                Glide.with(itemView.getContext())
                        .load(url)
                        .into(mImage);
            }
        }
    }

}
