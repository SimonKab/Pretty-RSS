package com.simonk.project.prettyrss.ui.rss;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.base.Strings;
import com.google.firebase.FirebaseException;
import com.simonk.project.prettyrss.R;
import com.simonk.project.prettyrss.auth.AuthFactory;
import com.simonk.project.prettyrss.databinding.HistoryFragmentBinding;
import com.simonk.project.prettyrss.error.ErrorInterceptor;
import com.simonk.project.prettyrss.error.ErrorLayout;
import com.simonk.project.prettyrss.error.NotAuthenticatedErrorLayout;
import com.simonk.project.prettyrss.model.HistoryEntry;
import com.simonk.project.prettyrss.repository.RssRepository;
import com.simonk.project.prettyrss.ui.login.LoginActivity;
import com.simonk.project.prettyrss.viewmodels.HistoryListViewModel;
import com.simonk.project.prettyrss.viewmodels.RssListViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class HistoryFragment extends Fragment {

    private TextInputEditText mSource;
    private TextInputLayout mSourceLayout;
    private Button mGetNews;
    private ViewGroup mContent;
    private TabLayout mTabs;
    private ViewPager mViewPager;

    private HistoryListViewModel mViewModel;

    private ErrorLayout mErrorLayout;
    private ProgressBar mProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        HistoryFragmentBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.history_fragment, parent, false);
        init(binding);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(TabHistoryFragment.getInstance(true), getString(R.string.sources));
        adapter.addFragment(TabHistoryFragment.getInstance(false), getString(R.string.history));
        mViewPager.setAdapter(adapter);
        mTabs.setupWithViewPager(mViewPager);

        mViewModel = ViewModelProviders.of(requireActivity()).get(HistoryListViewModel.class);

        mSource.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSourceLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mGetNews.setOnClickListener(this::onGetNewsClickListener);

        mErrorLayout.addErrorInterceptor(new NotAuthenticatedErrorLayout());

        if (!AuthFactory.getAuthManager().isSignIn()) {
            setErrorView();
            mErrorLayout.setErrorButtonText(getString(R.string.sign_in));
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

        mViewModel.getEditSource().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mSource.setText(s);
            }
        });

        return binding.getRoot();
    }

    private void onGetNewsClickListener(View v) {
        String source = mSource.getText().toString().trim();
        if (Strings.isNullOrEmpty(source)) {
            return;
        }

        boolean isValid = URLUtil.isValidUrl(source);
        if (isValid) {
            Uri uriSource = Uri.parse(source);
            ViewModelProviders.of(requireActivity()).get(RssListViewModel.class).setRssSource(uriSource);
            Navigation.findNavController(getView()).navigate(R.id.action_second_blank_to_first_blank);

            saveInHistory(source);
        } else {
            mSourceLayout.setError(getString(R.string.error_invalid_url));
        }
    }

    private void saveInHistory(String source) {
        HistoryEntry historyEntry = new HistoryEntry();
        historyEntry.setPath(source);
        LiveData<RssRepository.DatabaseResult> result = mViewModel.saveHistory(historyEntry);
        result.observe(this, new Observer<RssRepository.DatabaseResult>() {
            @Override
            public void onChanged(RssRepository.DatabaseResult databaseResult) {
                if (databaseResult == null) {
                    return;
                }

                if (databaseResult.networkError) {
                    Snackbar.make(getView(), getString(R.string.error_network), Snackbar.LENGTH_LONG).show();
                }
                if (databaseResult.disconnect) {
                    Snackbar.make(getView(), getString(R.string.error_disconnected), Snackbar.LENGTH_LONG).show();
                }
                if (databaseResult.error != null) {
                    if (databaseResult.error instanceof FirebaseException) {
                        Snackbar.make(getView(), databaseResult.error.getMessage(), Snackbar.LENGTH_LONG).show();
                    } else {
                        throw new RuntimeException(databaseResult.error);
                    }
                }
            }
        });
    }

    private void init(HistoryFragmentBinding binding) {
        mSource = binding.historyFragmentSource;
        mGetNews = binding.historyFragmentGetNews;
        mTabs = binding.historyFragmentTabs;
        mViewPager = binding.historyFragmentViewPager;
        mSourceLayout = binding.historyFragmentSourceLayout;
        mProgress = binding.historyFragmentProgress;
        mErrorLayout = binding.historyFragmentError;
        mContent = binding.historyFragmentContent;
    }

    private void setProgressView() {
        mProgress.setVisibility(View.VISIBLE);
        mErrorLayout.setVisibility(View.GONE);
        mContent.setVisibility(View.GONE);
        mViewPager.setVisibility(View.GONE);
        mTabs.setVisibility(View.GONE);
    }

    private void setContentView() {
        mProgress.setVisibility(View.GONE);
        mErrorLayout.setVisibility(View.GONE);
        mContent.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
        mTabs.setVisibility(View.VISIBLE);
    }

    private void setErrorView() {
        mProgress.setVisibility(View.GONE);
        mErrorLayout.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);
        mViewPager.setVisibility(View.GONE);
        mTabs.setVisibility(View.GONE);
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }
}
