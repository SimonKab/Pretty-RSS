package com.simonk.project.ppoproject.permission;

import android.util.Pair;

import com.simonk.project.ppoproject.error.ErrorInterceptor;

import java.util.Collection;

/**
 * Created by Simon on 23.08.2017.
 */

public class PermissionInterceptor implements ErrorInterceptor<Pair<String, Boolean>> {

    private PermissionRequest mPermissionRequest;

    public PermissionInterceptor() {
    }

    @Override
    public boolean checkIsItHandlingError(Exception exception) {
        return false;
    }

    @Override
    public Pair<String, Boolean> handleError(Exception exception) {
        return null;
    }

    @Override
    public boolean checkIsItHandlingError(ErrorType errorType) {
        return errorType == ErrorType.PERMISSION;
    }

    @Override
    public Pair<String, Boolean> handleError(ErrorType errorType) {
        if(mPermissionRequest == null) {
            return new Pair<>("Some permissions was not enabled", true);
        }

        Collection<PermissionRequest.Descriptions> descriptions =
                mPermissionRequest.findDescriptions(mPermissionRequest.getNotGrantedPermissions()).values();
        String descriptionText = PermissionRequest.Descriptions.combineMainDescriptions(descriptions);
        return new Pair<>(descriptionText, true);
    }

    public void setPermissionRequest(PermissionRequest permission) {
        mPermissionRequest = permission;
    }
}
