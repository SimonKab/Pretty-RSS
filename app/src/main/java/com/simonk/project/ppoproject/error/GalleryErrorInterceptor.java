package com.simonk.project.ppoproject.error;

import android.util.Pair;

import java.io.IOException;

/**
 * Created by Simon on 23.08.2017.
 */

public class GalleryErrorInterceptor implements ErrorInterceptor<Pair<String, Boolean>> {
    @Override
    public boolean checkIsItHandlingError(Exception exception) {
        return true;
    }

    @Override
    public Pair<String, Boolean> handleError(Exception exception) {
        if (exception instanceof IOException) {
            return new Pair<>("Ошибка чтения файла", true);
        }

        return null;
    }

    @Override
    public boolean checkIsItHandlingError(ErrorType errorType) {
        return false;
    }

    @Override
    public Pair<String, Boolean> handleError(ErrorType errorType) {
        return null;
    }
}
