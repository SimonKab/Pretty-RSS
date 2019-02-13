package com.simonk.project.prettyrss.error;

import com.simonk.project.prettyrss.R;

public class RssUrlNotProvidedErrorInterceptor implements ErrorLayout.ErrorLayoutInterceptor {

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
        return errorType == ErrorType.RSS_URL_NOT_PROVIDED;
    }

    @Override
    public ErrorLayout.InterceptorData handleError(ErrorType errorType) {
        return new ErrorLayout.InterceptorData(R.string.error_rss_not_provided, false);
    }

}
