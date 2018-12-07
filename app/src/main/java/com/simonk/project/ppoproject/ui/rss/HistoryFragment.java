package com.simonk.project.ppoproject.ui.rss;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.base.Strings;
import com.google.firebase.FirebaseException;
import com.simonk.project.ppoproject.R;
import com.simonk.project.ppoproject.auth.AuthFactory;
import com.simonk.project.ppoproject.databinding.FirstBlankFragmentBinding;
import com.simonk.project.ppoproject.databinding.HistoryFragmentBinding;
import com.simonk.project.ppoproject.databinding.SecondBlankFragmentBinding;
import com.simonk.project.ppoproject.error.ErrorInterceptor;
import com.simonk.project.ppoproject.error.ErrorLayout;
import com.simonk.project.ppoproject.error.NotAuthenticatedErrorLayout;
import com.simonk.project.ppoproject.model.HistoryEntry;
import com.simonk.project.ppoproject.repository.RssRepository;
import com.simonk.project.ppoproject.rss.RssChannel;
import com.simonk.project.ppoproject.ui.login.LoginActivity;
import com.simonk.project.ppoproject.ui.rss.util.HistoryListAdapter;
import com.simonk.project.ppoproject.utils.DateUtils;
import com.simonk.project.ppoproject.viewmodels.HistoryListViewModel;
import com.simonk.project.ppoproject.viewmodels.RssListViewModel;

import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryFragment extends Fragment {

    private TextInputEditText mSource;
    private TextInputLayout mSourceLayout;
    private Button mGetNews;
    private RecyclerView mHistory;
    private ViewGroup mContent;
    private ViewGroup mHistoryLayout;

    private HistoryListAdapter mAdapter;

    private HistoryListViewModel mViewModel;

    private ErrorLayout mErrorLayout;
    private ProgressBar mProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        HistoryFragmentBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.history_fragment, parent, false);
        init(binding);

        mViewModel = ViewModelProviders.of(requireActivity()).get(HistoryListViewModel.class);

        mAdapter = new HistoryListAdapter();
        mHistory.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        mHistory.setAdapter(mAdapter);
        mAdapter.setClickListener(new HistoryListAdapter.HistoryListViewHolder.ClickListener() {
            @Override
            public void onClicked(View v, String path) {
                mSource.setText(path);
            }
        });

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

        updateUi();

        return binding.getRoot();
    }

    private void updateUi() {
        setProgressView();

        mViewModel.getHistory().observe(this, new Observer<RssRepository.DatabaseResult<List<HistoryEntry>>>() {
            @Override
            public void onChanged(RssRepository.DatabaseResult<List<HistoryEntry>> listDatabaseResult) {
                if (listDatabaseResult == null) {
                    setProgressView();
                    return;
                }

                if (listDatabaseResult.data != null) {
                    setContentView();

                    List<HistoryEntry> historyEntries = listDatabaseResult.data;
                    Collections.reverse(historyEntries);

                    mAdapter.resolveActionChange(() -> {
                        mAdapter.setItemsList(historyEntries);
                    });
                }

                if (listDatabaseResult.networkError) {
                    setErrorView();
                    mErrorLayout.handleError(ErrorInterceptor.ErrorType.NETWORK_ERROR);
                }
                if (listDatabaseResult.disconnect) {
                    setErrorView();
                    mErrorLayout.handleError(ErrorInterceptor.ErrorType.NETWORK_ERROR);
                }
                if (listDatabaseResult.error != null) {
                    setErrorView();
                    if (listDatabaseResult.error instanceof FirebaseException) {
                        mErrorLayout.handleError(listDatabaseResult.error);
                    } else {
                        throw new RuntimeException(listDatabaseResult.error);
                    }
                }
            }
        });

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
            mSourceLayout.setError("Invalid url");
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

                if (databaseResult.complete) {
                    updateUi();
                }

                if (databaseResult.networkError) {
                    Snackbar.make(getView(), "Network error", Snackbar.LENGTH_LONG).show();
                }
                if (databaseResult.disconnect) {
                    Snackbar.make(getView(), "Disconnected", Snackbar.LENGTH_LONG).show();
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
        mHistory = binding.historyFragmentHistory;
        mSourceLayout = binding.historyFragmentSourceLayout;
        mProgress = binding.historyFragmentProgress;
        mErrorLayout = binding.historyFragmentError;
        mContent = binding.historyFragmentContent;
        mHistoryLayout = binding.historyFragmentHistoryLayout;
    }

    private void setProgressView() {
        mProgress.setVisibility(View.VISIBLE);
        mErrorLayout.setVisibility(View.GONE);
        mContent.setVisibility(View.GONE);
        mHistoryLayout.setVisibility(View.GONE);
    }

    private void setContentView() {
        mProgress.setVisibility(View.GONE);
        mErrorLayout.setVisibility(View.GONE);
        mContent.setVisibility(View.VISIBLE);
        mHistoryLayout.setVisibility(View.VISIBLE);
    }

    private void setErrorView() {
        mProgress.setVisibility(View.GONE);
        mErrorLayout.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);
        mHistoryLayout.setVisibility(View.GONE);
    }
}
