package com.simonk.project.ppoproject.error;

import com.simonk.project.ppoproject.R;

public class NotAuthenticatedErrorLayout implements ErrorLayout.ErrorLayoutInterceptor {

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
        return errorType == ErrorType.NOT_AUTHENTICATED;
    }

    @Override
    public ErrorLayout.InterceptorData handleError(ErrorType errorType) {
        return new ErrorLayout.InterceptorData(R.string.error_not_authenticated, true);
    }

}
