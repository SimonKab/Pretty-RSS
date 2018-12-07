package com.simonk.project.ppoproject.error;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.simonk.project.ppoproject.R;
import com.simonk.project.ppoproject.ui.RevealFrameLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Created by Simon on 23.08.2017.
 */

public class ErrorLayout extends RevealFrameLayout {

    public interface OnRetryListener {
        void onRetry();
    }

    protected TextView mErrorTextView;
    protected Button mErrorButton;

    private ErrorLayout.OnRetryListener mOnRetryListener;

    protected int mLayoutId = R.layout.error_layout;

    protected Exception mException;
    protected ErrorInterceptor.ErrorType mErrorType;

    protected List<ErrorLayoutInterceptor> mErrorInterceptor;

    public ErrorLayout(Context context) {
        this(context, null);
    }

    public ErrorLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ErrorLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ErrorLayout);

        int layoutId = ta.getResourceId(R.styleable.ErrorLayout_layout, 0);
        if (layoutId != 0) {
            mLayoutId = layoutId;
        }

        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ErrorLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ErrorLayout);

        int layoutId = ta.getResourceId(R.styleable.ErrorLayout_layout, 0);
        if (layoutId != 0) {
            mLayoutId = layoutId;
        }

        init();
    }

    public void setLayoutId(int id) {
        if (id == mLayoutId)
            return;

        if (id == 0) {
            id = R.layout.error_layout;
        }

        mLayoutId = id;
        removeAllViews();
        init();
    }

    public int getLayoutId() {
        return mLayoutId;
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(mLayoutId, this, true);

        mErrorTextView = (TextView) findViewById(R.id.internet_error_text_view);
        mErrorButton = (Button) findViewById(R.id.internet_error_button_retry);

        if (mOnRetryListener != null) {
            setOnRetryListener(mOnRetryListener);
        }

        if (mErrorInterceptor == null) {
            mErrorInterceptor = new ArrayList<>();
        }

        setVisibility(GONE);
    }

    public void setErrorButtonText(String text) {
        mErrorButton.setText(text);
    }

    public void setOnRetryListener(ErrorLayout.OnRetryListener onRetryListener) {
        mOnRetryListener = onRetryListener;
        mErrorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnRetryListener.onRetry();
            }
        });
    }

    public void addErrorInterceptor(ErrorLayoutInterceptor errorInterceptor) {
        mErrorInterceptor.add(errorInterceptor);
    }

    public void clearError() {
        mException = null;
        mErrorType = null;
        mErrorTextView.setText("");
        mErrorButton.setText("Retry");
    }

    public boolean checkIsItHandlingError(ErrorInterceptor.ErrorType exception) {
        return findInterceptor(exception) != null;
    }

    public boolean checkIsItHandlingError(Exception exception) {
        return findInterceptor(exception) != null;
    }

    public void handleError(Exception exception) {
        mException = exception;
        ErrorLayoutInterceptor interceptor = findInterceptor(exception);
        if (interceptor == null) {
            interceptor = mDefaultInterceptor;
        }
        handleErrorInternal(interceptor.handleError(exception));
    }

    public void handleError(ErrorInterceptor.ErrorType errorType) {
        mErrorType = errorType;
        ErrorLayoutInterceptor interceptor = findInterceptor(errorType);
        if (interceptor == null) {
            interceptor = mDefaultInterceptor;
        }
        handleErrorInternal(interceptor.handleError(errorType));
    }

    private void handleErrorInternal(InterceptorData interceptorData) {
        if (interceptorData.descriptionId != -1) {
            mErrorTextView.setText(getResources().getString(interceptorData.descriptionId));
        } else {
            mErrorTextView.setText(interceptorData.description);
        }
        mErrorButton.setVisibility(interceptorData.retryButtonVisible ? VISIBLE : GONE);
    }

    private ErrorLayoutInterceptor findInterceptor(Exception exception) {
        for (ErrorLayoutInterceptor interceptor : mErrorInterceptor) {
            if (interceptor.checkIsItHandlingError(exception)) {
                return interceptor;
            }
        }

        return null;
    }

    private ErrorLayoutInterceptor findInterceptor(ErrorInterceptor.ErrorType error) {
        for (ErrorLayoutInterceptor interceptor : mErrorInterceptor) {
            if (interceptor.checkIsItHandlingError(error)) {
                return interceptor;
            }
        }

        return null;
    }

    private ErrorLayoutInterceptor mDefaultInterceptor = new ErrorLayoutInterceptor() {
        @Override
        public boolean checkIsItHandlingError(Exception exception) {
            return true;
        }

        @Override
        public InterceptorData handleError(Exception exception) {
            return new InterceptorData(R.string.error_general, true);
        }

        @Override
        public boolean checkIsItHandlingError(ErrorType errorType) {
            return true;
        }

        @Override
        public InterceptorData handleError(ErrorType errorType) {
            return new InterceptorData(R.string.error_general, true);
        }
    };

    public static class InterceptorData {
        public int descriptionId = -1;
        public String description;
        public boolean retryButtonVisible;

        public InterceptorData(int descriptionId, boolean retryButtonVisible) {
            this.descriptionId = descriptionId;
            this.retryButtonVisible = retryButtonVisible;
        }

        public InterceptorData(String description, boolean retryButtonVisible) {
            this.description = description;
            this.retryButtonVisible = retryButtonVisible;
        }
    }

    public interface ErrorLayoutInterceptor extends ErrorInterceptor<InterceptorData> {

    }

    /*@Override
    protected void onRestoreInstanceState(Parcelable state) {
        ErrorLayout.SavedState ss = (ErrorLayout.SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mException = ss.exception;
        mErrorType = ss.errorType;
        if (mException != null) {
            handleError(mException);
        } else {
            handleError(mErrorType);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        ErrorLayout.SavedState ss = new ErrorLayout.SavedState(superState);
        ss.exception = mException;
        ss.errorType = mErrorType;
        return ss;
    }*/

    private static class SavedState extends View.BaseSavedState {
        public Exception exception;
        public ErrorInterceptor.ErrorType errorType;

        SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            exception = (Exception) source.readSerializable();
            errorType = (ErrorInterceptor.ErrorType) source.readSerializable();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeSerializable(exception);
            dest.writeSerializable(errorType);
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public ErrorLayout.SavedState createFromParcel(Parcel in) {
                return new ErrorLayout.SavedState(in);
            }

            public ErrorLayout.SavedState[] newArray(int size) {
                return new ErrorLayout.SavedState[size];
            }
        };
    }
}