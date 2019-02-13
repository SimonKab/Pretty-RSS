package com.simonk.project.prettyrss.error;

import com.simonk.project.prettyrss.R;
import com.simonk.project.prettyrss.network.exceptions.InvalidRssPathException;

public class InvalidRssUrlErrorInterceptor implements ErrorLayout.ErrorLayoutInterceptor {

    @Override
    public boolean checkIsItHandlingError(Exception exception) {
        return exception instanceof InvalidRssPathException;
    }

    @Override
    public ErrorLayout.InterceptorData handleError(Exception exception) {
        return new ErrorLayout.InterceptorData(R.string.error_rss_url, false);
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
