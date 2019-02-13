package com.simonk.project.prettyrss.ui.rss;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;

import com.simonk.project.prettyrss.R;
import com.simonk.project.prettyrss.model.HistoryEntry;
import com.simonk.project.prettyrss.viewmodels.HistoryListViewModel;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

public class SourceDialogFragment extends DialogFragment {

    private static final String PATH_KEY = "PATH_KEY";
    private static final String NAME_KEY = "NAME_KEY";
    private static final String ID_KEY = "ID_KEY";

    private EditText mSourceName;

    private HistoryListViewModel mViewModel;

    private String mPath;
    private String mName;

    public static SourceDialogFragment getInstance(HistoryEntry historyEntry) {
        Bundle arguments = new Bundle();
        arguments.putString(PATH_KEY, historyEntry.getPath());
        arguments.putString(NAME_KEY, historyEntry.getName());
        arguments.putString(ID_KEY, historyEntry.getId());

        SourceDialogFragment sourseDialogFragment = new SourceDialogFragment();
        sourseDialogFragment.setArguments(arguments);
        return sourseDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle onSaveInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        ViewGroup root = (ViewGroup) layoutInflater.inflate(R.layout.source_dialog_fragment, null);

        mSourceName = root.findViewById(R.id.source_dialog_fragment_edit);
        if (getArguments().getString(NAME_KEY) != null) {
            mSourceName.setText(getArguments().getString(NAME_KEY));
        }

        mViewModel = ViewModelProviders.of(requireActivity()).get(HistoryListViewModel.class);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(root)
                .setTitle(R.string.source_dialog_fragment_title)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HistoryEntry historyEntry = new HistoryEntry();
                        historyEntry.setId(getArguments().getString(ID_KEY));
                        historyEntry.setName(mSourceName.getText().toString());
                        historyEntry.setPath(getArguments().getString(PATH_KEY));
                        mViewModel.saveSource(historyEntry);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        return dialog;
    }


}
