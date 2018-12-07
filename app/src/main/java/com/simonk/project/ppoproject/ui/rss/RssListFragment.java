package com.simonk.project.ppoproject.ui.rss;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.simonk.project.ppoproject.R;
import com.simonk.project.ppoproject.auth.AuthFactory;
import com.simonk.project.ppoproject.databinding.RssListFragmentBinding;
import com.simonk.project.ppoproject.error.ErrorInterceptor;
import com.simonk.project.ppoproject.error.ErrorLayout;
import com.simonk.project.ppoproject.error.InvalidRssUrlErrorInterceptor;
import com.simonk.project.ppoproject.error.NotAuthenticatedErrorLayout;
import com.simonk.project.ppoproject.error.RssUrlNotProvidedErrorInterceptor;
import com.simonk.project.ppoproject.repository.AccountRepository;
import com.simonk.project.ppoproject.repository.RssRepository;
import com.simonk.project.ppoproject.rss.RssChannel;
import com.simonk.project.ppoproject.ui.login.LoginActivity;
import com.simonk.project.ppoproject.ui.rss.util.RssListAdapter;
import com.simonk.project.ppoproject.ui.web.WebActivity;
import com.simonk.project.ppoproject.utils.DateUtils;
import com.simonk.project.ppoproject.viewmodels.RssListViewModel;

import java.util.List;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.SharedElementCallback;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RssListFragment extends Fragment {

    private ProgressBar mLoadingProgress;

    private TextView mSourceTitle;
    private TextView mSourceDescription;
    private TextView mSourceLastBuild;
    private ImageView mSourceImage;

    private ViewGroup mContent;

    private RecyclerView mRssRecyclerView;
    private RssListAdapter mAdapter;

    private ErrorLayout mErrorLayout;

    private RssListViewModel mRssListViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        RssListFragmentBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.rss_list_fragment, parent, false);
        initBinding(binding);

        if (getResources().getBoolean(R.bool.isTablet)) {
            mRssRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(),
                    getResources().getInteger(R.integer.items_in_grid),
                    RecyclerView.VERTICAL, false));
        } else {
            mRssRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        }
        mAdapter = new RssListAdapter();
        mAdapter.setClickListener(new RssListAdapter.RssListViewHolder.ClickListener() {
            @Override
            public void onRssClicked(View v, String link) {
                ActivityOptionsCompat transitionActivityOptions
                        = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(),
                        v, v.getTransitionName());
                startActivity(WebActivity.getIntent(requireContext(), link), transitionActivityOptions.toBundle());
            }
        });
        mRssRecyclerView.setAdapter(mAdapter);
        mRssRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mErrorLayout.addErrorInterceptor(new InvalidRssUrlErrorInterceptor());
        mErrorLayout.addErrorInterceptor(new RssUrlNotProvidedErrorInterceptor());
        mErrorLayout.addErrorInterceptor(new NotAuthenticatedErrorLayout());
        mErrorLayout.setOnRetryListener(new ErrorLayout.OnRetryListener() {
            @Override
            public void onRetry() {
                setProgressView();
                mRssListViewModel.forceFetchRssChannel();
            }
        });

        if (!AuthFactory.getAuthManager().isSignIn()) {
            setErrorView();
            mErrorLayout.setErrorButtonText("Sign in");
            mErrorLayout.handleError(ErrorInterceptor.ErrorType.NOT_AUTHENTICATED);
            mErrorLayout.setOnRetryListener(new ErrorLayout.OnRetryListener() {
                @Override
                public void onRetry() {
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
            return binding.getRoot();
        }

        requireActivity().setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                super.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots);
                ((ViewGroup)sharedElements.get(0)).getChildAt(0).setAlpha(0);
                ((ViewGroup)sharedElements.get(0)).getChildAt(0).animate().setDuration(300).alpha(1);
            }
        });

        mRssListViewModel = ViewModelProviders.of(requireActivity()).get(RssListViewModel.class);

        setObservers();

        setProgressView();

        return binding.getRoot();
    }

    private void setObservers() {
        mRssListViewModel.getRssSource().observe(this, new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                setProgressView();
            }
        });

        mRssListViewModel.getRssChannel().observe(this, new Observer<RssRepository.RemoteServerResult<RssChannel>>() {
            @Override
            public void onChanged(RssRepository.RemoteServerResult<RssChannel> rssChannelRemoteServerResult) {
                if (rssChannelRemoteServerResult == null) {
                    setProgressView();
                    return;
                }

                if (rssChannelRemoteServerResult.sourceUri == null) {
                    if (mRssListViewModel.getRssSource().getValue() == null) {
                        setErrorView();
                        mErrorLayout.handleError(ErrorInterceptor.ErrorType.RSS_URL_NOT_PROVIDED);
                    }
                    return;
                }

                if (!rssChannelRemoteServerResult.sourceUri.equals(mRssListViewModel.getRssSource().getValue())) {
                    return;
                }

                if (rssChannelRemoteServerResult.data != null) {
                    RssChannel data = rssChannelRemoteServerResult.data;

                    setContentView();

                    mSourceTitle.setText(data.getChannel().getTitle());
                    if (data.getChannel().getDescription() != null) {
                        mSourceDescription.setText(data.getChannel().getDescription().getText());
                    }
                    mSourceLastBuild.setText(DateUtils.convertRssDateToLocale(requireContext(),
                            data.getChannel().getLastBuildDate()));

                    Glide.with(RssListFragment.this)
                            .load(data.getChannel().getImage().getUrl())
                            .into(mSourceImage);

                    mAdapter.resolveActionChange(() -> {
                        mAdapter.setItemsList(data.getChannel().getItems());
                    });
                }

                if (rssChannelRemoteServerResult.error != null) {
                    setErrorView();
                    mErrorLayout.handleError((Exception) rssChannelRemoteServerResult.error);
                }
            }
        });
    }

    private void initBinding(RssListFragmentBinding binding) {
        mSourceImage = binding.rssListFragmentSourceImage;
        mSourceTitle = binding.rssListFragmentSourceTitle;
        mSourceDescription = binding.rssListFragmentSourceDescription;
        mSourceLastBuild = binding.rssListFragmentSourceLastBuild;
        mRssRecyclerView = binding.rssListFragmentNews;
        mContent = binding.rssListFragmentContent;
        mErrorLayout = binding.rssListFragmentError;
        mLoadingProgress = binding.rssListFragmentProgress;
    }

    private void setProgressView() {
        mContent.setVisibility(View.GONE);
        mRssRecyclerView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mErrorLayout.setVisibility(View.GONE);
    }

    private void setErrorView() {
        mContent.setVisibility(View.GONE);
        mRssRecyclerView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.GONE);
        mErrorLayout.setVisibility(View.VISIBLE);
    }

    private void setContentView() {
        mContent.setVisibility(View.VISIBLE);
        mRssRecyclerView.setVisibility(View.VISIBLE);
        mLoadingProgress.setVisibility(View.GONE);
        mErrorLayout.setVisibility(View.GONE);
    }
}
