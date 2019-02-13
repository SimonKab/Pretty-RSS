package com.simonk.project.prettyrss.permission;

import com.simonk.project.prettyrss.R;
import com.simonk.project.prettyrss.error.ErrorLayout;

import java.util.Collection;

/**
 * Created by Simon on 23.08.2017.
 */

public class PermissionInterceptor implements ErrorLayout.ErrorLayoutInterceptor {

    private PermissionRequest mPermissionRequest;

    public PermissionInterceptor() {
    }

    @Override
    public boolean checkIsItHandlingError(Exception exception) {
        return false;
    }

    @Override
    public ErrorLayout.InterceptorData handleError(Exception exception) {
        return null;
    }

    @Override
    public boolean checkIsItHandlingError(ErrorType errorType) {
        return errorType == ErrorType.PERMISSION;
    }

    @Override
    public ErrorLayout.InterceptorData handleError(ErrorType errorType) {
        if(mPermissionRequest == null) {
            return new ErrorLayout.InterceptorData(R.string.error_unknown_permission, true);
        }

        Collection<PermissionRequest.Descriptions> descriptions =
                mPermissionRequest.findDescriptions(mPermissionRequest.getNotGrantedPermissions()).values();
        String descriptionText = PermissionRequest.Descriptions.combineMainDescriptions(descriptions);
        return new ErrorLayout.InterceptorData(descriptionText, true);
    }

    public void setPermissionRequest(PermissionRequest permission) {
        mPermissionRequest = permission;
    }
}
