package com.simonk.project.ppoproject.ui.about;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simonk.project.ppoproject.BuildConfig;
import com.simonk.project.ppoproject.R;
import com.simonk.project.ppoproject.databinding.AboutFragmentBinding;
import com.simonk.project.ppoproject.error.ErrorLayout;
import com.simonk.project.ppoproject.permission.PermissionHandler;
import com.simonk.project.ppoproject.permission.PermissionRequest;
import com.simonk.project.ppoproject.permission.PermissionsAccessor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

public class AboutFragment extends Fragment {

    private PermissionHandler mPermissionHandler;

    private TextView mVersionTextView;
    private TextView mImeiTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        AboutFragmentBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.about_fragment, parent, false);
        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.appBarInclude.toolbarLayout.toolbar);

        ErrorLayout permissionRequestLayout = binding.galleryPermissionLayout;
        permissionRequestLayout.shouldAnimate(false);
        mPermissionHandler = PermissionHandler.with(this).connect(permissionRequestLayout);

        mVersionTextView = binding.version;
        mImeiTextView = binding.imei;

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUiIfHavePermissions();
    }

    private void updateUi() {
        mImeiTextView.setVisibility(View.VISIBLE);
        mVersionTextView.setVisibility(View.VISIBLE);

        mVersionTextView.setText(BuildConfig.VERSION_NAME);

        if (PermissionsAccessor.tryToAccessPhoneStatePermission(this)) {
            TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            mImeiTextView.setText(telephonyManager.getDeviceId());
        }
    }

    private void updateUiIfHavePermissions() {
        mImeiTextView.setVisibility(View.GONE);
        mVersionTextView.setVisibility(View.GONE);

        PermissionRequest permission = new PermissionRequest(Manifest.permission.READ_PHONE_STATE);
        permission.addDescriptions(getResources(), R.array.about_phone_permission);
        permission.setAction(new PermissionRequest.PermissionAction() {
            @Override
            public void onPermissionGranted(PermissionRequest permission) {
                updateUi();
            }
        });
        mPermissionHandler.postDelayed(permission, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        mPermissionHandler.onSomePermissionResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
