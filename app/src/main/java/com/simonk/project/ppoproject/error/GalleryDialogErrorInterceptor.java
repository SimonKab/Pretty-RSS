package com.simonk.project.ppoproject.error;

import android.util.Pair;

/**
 * Created by Simon on 28.08.2017.
 */

public class GalleryDialogErrorInterceptor extends GalleryErrorInterceptor {
    @Override
    public Pair<String, Boolean> handleError(Exception exception) {
        Pair<String, Boolean> superResult = super.handleError(exception);
        return new Pair<>(superResult.first, false);
    }
}
