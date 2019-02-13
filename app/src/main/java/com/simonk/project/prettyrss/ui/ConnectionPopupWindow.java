package com.simonk.project.prettyrss.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.simonk.project.prettyrss.R;
import com.simonk.project.prettyrss.network.ConnectionManager;
import com.simonk.project.prettyrss.network.NetworkFactory;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ConnectionPopupWindow extends PopupWindow {

    private View mPlaceholder;

    private String mConnected;
    private String mConnecting;

    public ConnectionPopupWindow(Context applicationContext) {
        LayoutInflater inflater = (LayoutInflater)
                applicationContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_connection, null);
        setContentView(popupView);

        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        setAnimationStyle(R.style.ConnectionPopupWindowAnimation);

        mConnected = applicationContext.getResources().getString(R.string.connected);
        mConnecting = applicationContext.getResources().getString(R.string.connecting);
    }

    private ConnectionManager.ConnectionCallback mCallback = new ConnectionManager.ConnectionCallback() {

        @Override
        public void onConnected() {
            getContentView().post(() -> {
                ((ViewGroup) getContentView()).getChildAt(0).setVisibility(View.GONE);
                ((TextView)((ViewGroup)getContentView()).getChildAt(1)).setText(mConnected);
                dismiss();
            });
        }

        @Override
        public void onInternetAccess() {
            getContentView().post(() -> {
                if (!isShowing()) {
                    showAtLocation(mPlaceholder, Gravity.TOP, 0, 0);
                }

                ((ViewGroup) getContentView()).getChildAt(0).setVisibility(View.VISIBLE);
                ((TextView)((ViewGroup)getContentView()).getChildAt(1)).setText(mConnecting);
            });
        }

        @Override
        public void onDisconnected() {
            getContentView().post(() -> {
                if (!isShowing()) {
                    showAtLocation(mPlaceholder, Gravity.TOP, 0, 0);
                }

                ((ViewGroup) getContentView()).getChildAt(0).setVisibility(View.VISIBLE);
                ((TextView) ((ViewGroup) getContentView()).getChildAt(1)).setText(mConnecting);
            });
        }

        @Override
        public void onError(Throwable error) {

        }
    };

    public void trackConnection(View placeholder) {
        mPlaceholder = placeholder;
        NetworkFactory.getConnectionManager().registerCallback(mCallback);
    }

    public void untrack() {
        NetworkFactory.getConnectionManager().unregisterCallback(mCallback);
    }
}
