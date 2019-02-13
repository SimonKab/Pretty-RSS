package com.simonk.project.prettyrss.error;

import com.simonk.project.prettyrss.R;

public class NetworkErrorInterceptor implements ErrorLayout.ErrorLayoutInterceptor {

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
        return errorType == ErrorType.NETWORK_ERROR;
    }

    @Override
    public ErrorLayout.InterceptorData handleError(ErrorType errorType) {
        return new ErrorLayout.InterceptorData(R.string.error_network, true);
    }

}
