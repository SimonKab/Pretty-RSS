package com.simonk.project.ppoproject.error;

import java.io.Serializable;

/**
 * Created by Simon on 23.08.2017.
 */

public interface ErrorInterceptor<T> extends Serializable {

    boolean checkIsItHandlingError(Exception exception);

    T handleError(Exception exception);

    boolean checkIsItHandlingError(ErrorType errorType);

    T handleError(ErrorType errorType);

    enum ErrorType { NOTHING_FOUND, PERMISSION, NETWORK_ERROR, RSS_URL_NOT_PROVIDED, NOT_AUTHENTICATED }
}
