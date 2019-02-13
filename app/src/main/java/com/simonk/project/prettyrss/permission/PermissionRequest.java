package com.simonk.project.prettyrss.permission;

import android.content.res.Resources;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Simon on 13.07.2017.
 */

public class PermissionRequest {

    public interface PermissionAction {
        void onPermissionGranted(PermissionRequest permissionRequest);
    }

    public interface  PermissionDeniedListener {
        void onPermissionDenied(PermissionRequest permissionRequest);
    }

    private String[] mPermissions;
    private String[] mNotGrantedPermissions;
    private boolean mAllWasGranted;
    private PermissionAction mAction;
    private PermissionDeniedListener mPermissionDeniedListener;
    private int mRequestCode;
    private boolean mDelayed;
    private Map<String, Descriptions> mDescriptions;

    public PermissionRequest(String... manifestPermissions) {
        mDescriptions = new HashMap<>();
        mPermissions = manifestPermissions;
    }

    boolean isDelayed() {
        return mDelayed;
    }

    void setDelayed(boolean delayed) {
        mDelayed = delayed;
    }

    String[] getPermissions() {
        return mPermissions;
    }

    void setPermissions(String[] permissions) {
        mPermissions = permissions;
    }

    String[] getNotGrantedPermissions() {
        return mNotGrantedPermissions;
    }

    void setNotGrantedPermissions(String[] permissionNotGranted) {
        mNotGrantedPermissions = permissionNotGranted;
    }

    boolean isAllWasGranted() {
        return mAllWasGranted;
    }

    void setAllWasGranted(boolean allWasGranted) {
        mAllWasGranted = allWasGranted;
    }

    PermissionAction getAction() {
        return mAction;
    }

    public PermissionRequest setAction(PermissionAction action) {
        mAction = action;
        return this;
    }

    public PermissionRequest setOnDeniedListener(PermissionDeniedListener permissionDeniedListener) {
        mPermissionDeniedListener = permissionDeniedListener;
        return this;
    }

    PermissionDeniedListener getPermissionDeniedListener() {
        return mPermissionDeniedListener;
    }

    int getRequestCode() {
        return mRequestCode;
    }

    void setRequestCode(int requestCode) {
        mRequestCode = requestCode;
    }

    Map<String, Descriptions> getDescriptions() {
        return mDescriptions;
    }

    void setDescriptions(Map<String, Descriptions> descriptions) {
        mDescriptions = descriptions;
    }

    static class Descriptions {
        private String mMainDescription;
        private String mAdditionalDescriptionTitle;
        private String mAdditionalDescription;

        protected static String combineMainDescriptions(Collection<Descriptions> descriptions) {
            StringBuilder descriptionText = new StringBuilder();
            if (!descriptions.isEmpty()) {
                Iterator<Descriptions> descriptionsIterator = descriptions.iterator();
                descriptionText.append(descriptionsIterator.next().mMainDescription);
                while (descriptionsIterator.hasNext()) {
                    descriptionText.append("\n\n");
                    descriptionText.append(descriptionsIterator.next().mMainDescription);
                }
            }
            return descriptionText.toString();
        }

        protected static String combineAdditionalDescriptionsTitles(Collection<Descriptions> descriptions) {
            StringBuilder descriptionText = new StringBuilder();
            if (!descriptions.isEmpty()) {
                Iterator<Descriptions> descriptionsIterator = descriptions.iterator();
                descriptionText.append(descriptionsIterator.next().mAdditionalDescriptionTitle);
                while (descriptionsIterator.hasNext()) {
                    descriptionText.append("; ");
                    descriptionText.append(descriptionsIterator.next().mAdditionalDescriptionTitle);
                }
            }
            return descriptionText.toString();
        }

        protected static String combineAdditionalDescriptions(Collection<Descriptions> descriptions) {
            StringBuilder descriptionText = new StringBuilder();
            if (!descriptions.isEmpty()) {
                Iterator<Descriptions> descriptionsIterator = descriptions.iterator();
                descriptionText.append(descriptionsIterator.next().mAdditionalDescription);
                while (descriptionsIterator.hasNext()) {
                    descriptionText.append("\n\n");
                    descriptionText.append(descriptionsIterator.next().mAdditionalDescription);
                }

                descriptionText.append("Now you have to go to settings and enable permission manually in Permission section");
            }
            return descriptionText.toString();
        }
    }

    public PermissionRequest addDescriptions(Resources resources, int... resIdArray) {
        for (int resId : resIdArray) {
            String[] stringArray = resources.getStringArray(resId);

            if (stringArray.length != 4) {
                throw new IllegalArgumentException("Wrong array. Array must contain exactly 4 items");
            }

            String manifestPermission = stringArray[0];
            String mainDescription = stringArray[1];
            String additionalDescriptionTitle = stringArray[2];
            String additionalDescription = stringArray[3];

            Descriptions descriptions = new Descriptions();
            descriptions.mMainDescription = mainDescription;
            descriptions.mAdditionalDescriptionTitle = additionalDescriptionTitle;
            descriptions.mAdditionalDescription = additionalDescription;

            mDescriptions.put(manifestPermission, descriptions);
        }
        return this;
    }

    public PermissionRequest addDescriptions(String description, String dialogTitle,
                                             String dialogDescription) {
        Descriptions descriptions = new Descriptions();
        descriptions.mMainDescription = description;
        descriptions.mAdditionalDescriptionTitle = dialogDescription;
        descriptions.mAdditionalDescription = dialogTitle;

        mDescriptions.put(mPermissions[0], descriptions);
        return this;
    }

    public PermissionRequest addDescriptions(String manifestPermission, String description,
                                             String dialogTitle, String dialogDescription) {
        Descriptions descriptions = new Descriptions();
        descriptions.mMainDescription = description;
        descriptions.mAdditionalDescriptionTitle = dialogDescription;
        descriptions.mAdditionalDescription = dialogTitle;

        mDescriptions.put(manifestPermission, descriptions);
        return this;
    }

    Map<String, Descriptions> findDescriptions(String[] manifestPermissions) {
        Map<String, Descriptions> result = new HashMap<>();
        for (String permission : manifestPermissions) {
            Descriptions descriptions = mDescriptions.get(permission);
            if (descriptions != null) {
                result.put(permission, descriptions);
            }
        }

        return result;
    }
}
