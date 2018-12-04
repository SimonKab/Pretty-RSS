package com.simonk.project.ppoproject.utils;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.common.base.Joiner;

import androidx.loader.content.CursorLoader;

/**
 * Created by Serious Man on 31.01.2017.
 */
public class GalleryCursorLoader extends CursorLoader {

    private static final String MEDIA_SCANNER_VOLUME_EXTERNAL = "external";
    private static final Uri STORAGE_URI = MediaStore.Files.getContentUri(MEDIA_SCANNER_VOLUME_EXTERNAL);

    private static final String[] IMAGE_PROJECTION = new String[] {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_MODIFIED};

    private static final String[] ACCEPTABLE_IMAGE_TYPES =
            new String[] { "image/jpeg", "image/jpg", "image/png", "image/gif" };
    private static final String IMAGE_SELECTION = createImageSelection(
            ACCEPTABLE_IMAGE_TYPES,
            new Integer[] { MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE });

    private static final String SORT_ORDER = MediaStore.Images.Media.DATE_MODIFIED + " DESC";

    public GalleryCursorLoader(final Context context) {
        super(context, STORAGE_URI, IMAGE_PROJECTION,
                IMAGE_SELECTION, null, SORT_ORDER);
    }

    private static String createImageSelection(final String[] mimeTypes, Integer[] mediaTypes) {
        return MediaStore.Images.Media.MIME_TYPE + " IN ('" + Joiner.on("','").join(mimeTypes) + "') AND "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + " IN (" + Joiner.on(',').join(mediaTypes) + ")";
    }
}
