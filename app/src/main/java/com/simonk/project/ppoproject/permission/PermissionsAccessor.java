package com.simonk.project.ppoproject.permission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/**
 * Created by Simon on 09.06.2017.
 */

public class PermissionsAccessor {

    public static boolean tryToAccessCameraPermission(Fragment fragment) {
        return tryToAccessPermission(fragment, -1, Manifest.permission.CAMERA, false);
    }

    public static boolean tryToAccessPhoneStatePermission(Fragment fragment) {
        return tryToAccessPermission(fragment, -1, Manifest.permission.READ_PHONE_STATE, false);
    }

    public static boolean tryToAccessReadExternalStoragePermission(Fragment fragment) {
        return tryToAccessPermission(fragment, -1, Manifest.permission.READ_EXTERNAL_STORAGE, false);
    }

    public static boolean tryToAccessWriteExternalStorageCameraPermission(Fragment fragment) {
        return tryToAccessPermission(fragment, -1, Manifest.permission.WRITE_EXTERNAL_STORAGE, false);
    }

    static boolean tryToAccessPermission(Fragment fragment, int requestCode, String manifestPermission, boolean makeRequest) {
        if (checkPermission(fragment, manifestPermission)) {
            return true;
        } else {
            if (makeRequest) {
                fragment.requestPermissions(new String[]{manifestPermission},
                        requestCode);
            }
            return false;
        }
    }

    static void requestListPermissions(Fragment fragment, String[] manifestPermission, int requestCode) {
        if (manifestPermission != null && manifestPermission.length != 0) {
            fragment.requestPermissions(manifestPermission, requestCode);
        }
    }

    static boolean checkPermission(Fragment fragment, String manifestPermission) {
        Context context = fragment.getContext();
        if (context == null || manifestPermission == null) {
            return false;
        }

        return ContextCompat.checkSelfPermission(fragment.getContext(), manifestPermission)
                == PackageManager.PERMISSION_GRANTED;
    }

    static boolean resultOfAccessPermission(String manifestPermission,
                                             @NonNull String[] permissions,
                                             @NonNull int[] grantResults) {
        if (permissions.length > 0 && grantResults.length > 0) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(manifestPermission)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
