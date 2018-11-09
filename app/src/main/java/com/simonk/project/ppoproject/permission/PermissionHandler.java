package com.simonk.project.ppoproject.permission;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;

import com.simonk.project.ppoproject.R;
import com.simonk.project.ppoproject.error.ErrorInterceptor;
import com.simonk.project.ppoproject.error.ErrorLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

/**
 * Created by Simon on 13.07.2017.
 */

public class PermissionHandler {

    private List<PermissionRequest> mPermissionRequests;
    private Fragment mFragment;

    private ErrorLayout mLayout;

    private PermissionInterceptor mPermissionInterceptor;

    private Map<String, Boolean> mShouldShowRationalMap;

    private PermissionHandler(Fragment fragment) {
        mPermissionRequests = new ArrayList<>();
        mShouldShowRationalMap = new HashMap<>();
        mFragment = fragment;
        mPermissionInterceptor = new PermissionInterceptor();
    }

    public static PermissionHandler width(Fragment fragment) {
        return new PermissionHandler(fragment);
    }

    public void setPermissionInterceptor(PermissionInterceptor errorInterceptor) {
        mPermissionInterceptor = errorInterceptor;
        mLayout.setErrorInterceptor(mPermissionInterceptor);
    }

    private class OnEnableClickListenerImpl implements ErrorLayout.OnRetryListener {

        public int requestCode = 0;

        @Override
        public void onRetry() {
            onPermissionRequestEnabled(requestCode);
        }
    }

    public void onPermissionRequestEnabled(int requestCode) {
        for (PermissionRequest permissionRequest : mPermissionRequests) {
            if (permissionRequest.getRequestCode() == requestCode) {
                if (tryToFinishPermissionRequest(permissionRequest)) {
                    requestListPermissions(permissionRequest);
                }
                break;
            }
        }
    }

    public void postDelayed(PermissionRequest permissionRequest, int requestCode) {
        if (mLayout == null) {
            throw new RuntimeException("Permission layout was not installed");
        }

        PermissionRequest delayedPermission = findDelayedPermission();
        if (delayedPermission != null) {

            mPermissionRequests.remove(delayedPermission);
        }

        PermissionRequest samePermission = findSamePermissionRequest(requestCode);
        if (samePermission != null) {
            mPermissionRequests.remove(samePermission);
        }

        permissionRequest.setRequestCode(requestCode);
        permissionRequest.setDelayed(true);

        if (tryToFinishPermissionRequest(permissionRequest)) {
            PermissionRequest.PermissionDeniedListener permissionDeniedListener =
                    permissionRequest.getPermissionDeniedListener();
            if (permissionDeniedListener != null) {
                permissionDeniedListener.onPermissionDenied(permissionRequest);
            }

            mPermissionRequests.add(permissionRequest);

            if (mLayout.getVisibility() != View.VISIBLE) {
                mLayout.open();
            }

            OnEnableClickListenerImpl onEnableClickListener = new OnEnableClickListenerImpl();
            onEnableClickListener.requestCode = permissionRequest.getRequestCode();
            mLayout.setOnRetryListener(onEnableClickListener);

            refreshPermissionLayout(permissionRequest);
        }
    }

    private void refreshPermissionLayout(PermissionRequest permissionRequest) {
        mPermissionInterceptor.setPermissionRequest(permissionRequest);
        mLayout.handleError(ErrorInterceptor.ErrorType.PERMISSION);
    }

    public PermissionHandler connect(ErrorLayout permissionsLayout) {
        mLayout = permissionsLayout;
        mLayout.setErrorInterceptor(mPermissionInterceptor);
        mLayout.setErrorButtonText(mFragment.getString(R.string.permission_enable_button_text_def));
        return this;
    }

    public ErrorLayout getLayout() {
        return mLayout;
    }

    public void postImmediately(PermissionRequest permissionRequest, int requestCode) {
        PermissionRequest samePermission = findSamePermissionRequest(requestCode);
        if (samePermission != null) {
            mPermissionRequests.remove(samePermission);
        }

        permissionRequest.setRequestCode(requestCode);
        permissionRequest.setDelayed(false);

        if (tryToFinishPermissionRequest(permissionRequest)) {
            PermissionRequest.PermissionDeniedListener permissionDeniedListener =
                    permissionRequest.getPermissionDeniedListener();
            if (permissionDeniedListener != null) {
                permissionDeniedListener.onPermissionDenied(permissionRequest);
            }

            mPermissionRequests.add(permissionRequest);
            requestListPermissions(permissionRequest);
        }
    }

    private void checkIsPermissionRequestGranted(PermissionRequest permissionRequest) {
        String[] notGrantedPermissions = checkPermissionList(permissionRequest.getPermissions());
        permissionRequest.setNotGrantedPermissions(notGrantedPermissions);
        permissionRequest.setAllWasGranted(notGrantedPermissions.length == 0);
    }

    /**
     * @param permissionRequest
     * @return если permission полностью удовлетворен, то вернется false и можно ничего больше не делать.
     * Если не удовлетворен и нужны дальнейшие действия, вернется true
     */
    private boolean tryToFinishPermissionRequest(PermissionRequest permissionRequest) {
        checkIsPermissionRequestGranted(permissionRequest);
        if (permissionRequest.isAllWasGranted()) {
            onPermissionFullGranted(permissionRequest);
            return false;
        }

        return true;
    }

    private void requestListPermissions(PermissionRequest permission) {
        mShouldShowRationalMap.clear();
        for (String permissionManifest : permission.getNotGrantedPermissions()) {
            boolean shouldShow = mFragment.shouldShowRequestPermissionRationale(permissionManifest);
            mShouldShowRationalMap.put(permissionManifest, shouldShow);
        }
        PermissionsAccessor.requestListPermissions(mFragment, permission.getNotGrantedPermissions(),
                permission.getRequestCode());
    }

    private String[] checkPermissionList(String[] permissionsManifestList) {
        ArrayList<String> notGrantedPermissions = new ArrayList<>();
        for (String permissionManifest : permissionsManifestList) {
            boolean access = PermissionsAccessor.checkPermission(mFragment, permissionManifest);
            if (!access) {
                notGrantedPermissions.add(permissionManifest);
            }
        }

        return notGrantedPermissions.toArray(new String[] {});
    }

    private String[] checkPermissionsResult(String[] permissionsManifestList,
                                              @NonNull String[] permissionsManifestListResult,
                                              @NonNull int[] grantResults) {
        ArrayList<String> notGrantedPermissions = new ArrayList<>();
        for (String permissionManifest : permissionsManifestList) {
            boolean access = PermissionsAccessor.resultOfAccessPermission(permissionManifest,
                    permissionsManifestListResult, grantResults);
            if (!access) {
                notGrantedPermissions.add(permissionManifest);
            }
        }

        return notGrantedPermissions.toArray(new String[] {});
    }

    public void onSomePermissionResult(int requestCode,
                                       @NonNull String[] permissions,
                                       @NonNull int[] grantResults) {
        boolean isDelayedPermissionAlreadyProccessed = false;
        for (PermissionRequest permissionRequest : mPermissionRequests) {
            if (permissionRequest.getRequestCode() == requestCode) {
                if (tryToFinishPermissionRequest(permissionRequest)) {
                    if (permissionRequest.isDelayed()) {
                        isDelayedPermissionAlreadyProccessed = true;
                        refreshPermissionLayout(permissionRequest);
                    } else {
                        mPermissionRequests.remove(permissionRequest);
                    }
                    maybeShowOurDescriptionDialogInsteadSystemOne(permissionRequest);
                }
            }
        }

        if (!isDelayedPermissionAlreadyProccessed) {
            PermissionRequest delayedPermissionRequest = findDelayedPermission();
            if (delayedPermissionRequest != null) {
                tryToFinishPermissionRequest(delayedPermissionRequest);
            }
        }
    }

    private void onPermissionFullGranted(PermissionRequest permissionRequest) {
        if (permissionRequest.isDelayed()) {
            mLayout.hide();
        }
        mPermissionRequests.remove(permissionRequest);
        PermissionRequest.PermissionAction permissionAction = permissionRequest.getAction();
        if (permissionAction != null) {
            permissionAction.onPermissionGranted(permissionRequest);
        }
    }

    private void maybeShowOurDescriptionDialogInsteadSystemOne(PermissionRequest permission) {
        Collection<PermissionRequest.Descriptions> descriptions = createDescriptionsListForDialog(permission);
        if (descriptions != null && !descriptions.isEmpty()) {

            String dialogTitle = PermissionRequest.Descriptions.combineAdditionalDescriptionsTitles(descriptions);
            String dialogDescription = PermissionRequest.Descriptions.combineAdditionalDescriptions(descriptions);

            AlertDialog.Builder dialog = new AlertDialog.Builder(mFragment.requireContext());
            dialog.setTitle(dialogTitle);
            dialog.setMessage(dialogDescription);
            dialog.setPositiveButton(mFragment.getString(R.string.permission_dialog_setting_button),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package",
                            mFragment.requireActivity().getApplicationContext().getPackageName(), null);
                    intent.setData(uri);
                    mFragment.requireActivity().startActivity(intent);
                }
            });
            dialog.setNegativeButton(mFragment.getString(R.string.permission_dialog_cancel_button),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dialog.show();
        }
    }

    private String[] resolveForWhichPermissionsShowOurDialog(PermissionRequest permissionRequest) {
        String[] notGrantedPermissions = permissionRequest.getNotGrantedPermissions();
        List<String> notGrantedPermissionWithNeedDialog = new ArrayList<>();
        for (String permissionManifest : notGrantedPermissions) {
            boolean shouldShowAfterRequest = mFragment.shouldShowRequestPermissionRationale(permissionManifest);
            Boolean shouldShowBeforeRequest = mShouldShowRationalMap.get(permissionManifest);
            if (shouldShowBeforeRequest == null) shouldShowBeforeRequest = true;

            boolean shouldShowDialogForThisPerm = !shouldShowAfterRequest && !shouldShowBeforeRequest;
            if (shouldShowDialogForThisPerm) {
                notGrantedPermissionWithNeedDialog.add(permissionManifest);
            }
        }

        return notGrantedPermissionWithNeedDialog.toArray(new String[] {});
    }

    private Collection<PermissionRequest.Descriptions> createDescriptionsListForDialog(PermissionRequest permissionRequest) {
        String[] notGrantedPermissions = resolveForWhichPermissionsShowOurDialog(permissionRequest);

        Map<String, PermissionRequest.Descriptions> mapDescriptions =
                permissionRequest.findDescriptions(notGrantedPermissions);
        Collection<PermissionRequest.Descriptions> descriptionsList = mapDescriptions.values();
        return descriptionsList;
    }

    private PermissionRequest findSamePermissionRequest(int requestCode) {
        for (PermissionRequest permission : mPermissionRequests) {
            if (permission.getRequestCode() == requestCode) {
                return permission;
            }
        }
        return null;
    }

    private PermissionRequest findDelayedPermission() {
        for (PermissionRequest permission : mPermissionRequests) {
            if (permission.isDelayed()) {
                return permission;
            }
        }
        return null;
    }
}
