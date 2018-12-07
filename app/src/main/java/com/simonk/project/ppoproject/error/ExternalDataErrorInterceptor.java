package com.simonk.project.ppoproject.error;

public class ExternalDataErrorInterceptor implements ErrorLayout.ErrorLayoutInterceptor {

    private String mText;
    private boolean mRetryButtonVisible;

    private Class mHandlingExceptionClass;
    private ErrorType mHandlingErrorType;

    public void setText(String text) {
        mText = text;
    }

    public void setIsRetryButtonVisible(boolean isRetryButtonVisible) {
        mRetryButtonVisible = isRetryButtonVisible;
    }

    public void setException(Class exceptionClass) {
        mHandlingExceptionClass = exceptionClass;
    }

    public void setErrorType(ErrorType errorType) {
        mHandlingErrorType = errorType;
    }

    @Override
    public boolean checkIsItHandlingError(Exception exception) {
        return mHandlingExceptionClass.isInstance(exception);
    }

    @Override
    public ErrorLayout.InterceptorData handleError(Exception exception) {
        return new ErrorLayout.InterceptorData(mText, mRetryButtonVisible);
    }

    @Override
    public boolean checkIsItHandlingError(ErrorType errorType) {
        return mHandlingErrorType == errorType;
    }

    @Override
    public ErrorLayout.InterceptorData handleError(ErrorType errorType) {
        return new ErrorLayout.InterceptorData(mText, mRetryButtonVisible);
    }

}
