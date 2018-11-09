package com.simonk.project.ppoproject.error;

import android.util.Pair;

/**
 * Created by Simon on 23.08.2017.
 */

public class NothingFoundErrorInterceptor implements ErrorInterceptor<Pair<String, Boolean>> {

    @Override
    public boolean checkIsItHandlingError(ErrorType errorType) {
        return errorType == ErrorType.NOTHING_FOUND;
    }

    @Override
    public Pair<String, Boolean> handleError(ErrorType errorType) {
        if (errorType == ErrorType.NOTHING_FOUND) {
            return new Pair<>("Nothing found", false);
        }

        return null;
    }

    @Override
    public boolean checkIsItHandlingError(Exception exception) {
        return false;
    }

    @Override
    public Pair<String, Boolean> handleError(Exception exception) {
        return null;
    }
}
