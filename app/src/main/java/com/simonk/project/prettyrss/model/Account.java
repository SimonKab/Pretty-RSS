package com.simonk.project.prettyrss.model;

public class Account {

    private String mId;
    private Picture mPicture;
    private String mFirstName;
    private String mLastName;
    private String mTelephone;
    private String mAddress;
    private String mEmail;
    private boolean mMain;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public Picture getPicture() {
        return mPicture;
    }

    public void setPicrute(Picture picture) {
        this.mPicture = picture;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        this.mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        this.mLastName = lastName;
    }

    public String getTelephone() {
        return mTelephone;
    }

    public void setTelephone(String telephon) {
        this.mTelephone = telephon;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public boolean isMain() {
        return mMain;
    }

    public void setMain(boolean main) {
        mMain = main;
    }
}
