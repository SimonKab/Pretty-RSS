package com.simonk.project.ppoproject.error;

import com.google.firebase.FirebaseException;

public class GeneralFirebaseErrorInterceptor implements ErrorLayout.ErrorLayoutInterceptor  {

    @Override
    public boolean checkIsItHandlingError(Exception exception) {
        return exception instanceof FirebaseException;
    }

    @Override
    public ErrorLayout.InterceptorData handleError(Exception exception) {
        return new ErrorLayout.InterceptorData(exception.getMessage(), true);
    }

    @Override
    public boolean checkIsItHandlingError(ErrorType errorType) {
        return false;
    }

    @Override
    public ErrorLayout.InterceptorData handleError(ErrorType errorType) {
        return null;
    }

}
