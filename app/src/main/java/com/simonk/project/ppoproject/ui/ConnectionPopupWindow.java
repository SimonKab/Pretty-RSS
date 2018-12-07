package com.simonk.project.ppoproject.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.simonk.project.ppoproject.R;
import com.simonk.project.ppoproject.network.ConnectionManager;
import com.simonk.project.ppoproject.network.NetworkFactory;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ConnectionPopupWindow extends PopupWindow {

    private View mPlaceholder;

    public ConnectionPopupWindow(Context applicationContext) {
        LayoutInflater inflater = (LayoutInflater)
                applicationContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_connection, null);
        setContentView(popupView);

        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        setAnimationStyle(R.style.ConnectionPopupWindowAnimation);
    }

    private ConnectionManager.ConnectionCallback mCallback = new ConnectionManager.ConnectionCallback() {

        @Override
        public void onConnected() {
            getContentView().post(() -> {
                ((ViewGroup) getContentView()).getChildAt(0).setVisibility(View.GONE);
                ((TextView)((ViewGroup)getContentView()).getChildAt(1)).setText("Connected");
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
                ((TextView)((ViewGroup)getContentView()).getChildAt(1)).setText("Connecting...");
            });
        }

        @Override
        public void onDisconnected() {
            getContentView().post(() -> {
                if (!isShowing()) {
                    showAtLocation(mPlaceholder, Gravity.TOP, 0, 0);
                }

                ((ViewGroup) getContentView()).getChildAt(0).setVisibility(View.VISIBLE);
                ((TextView) ((ViewGroup) getContentView()).getChildAt(1)).setText("Connecting...");
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
