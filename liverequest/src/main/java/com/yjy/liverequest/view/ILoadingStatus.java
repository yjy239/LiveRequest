package com.yjy.liverequest.view;

import android.view.View;

/**
 *
 */
public interface ILoadingStatus {

    void showLoading(String msg);

    void showEmpty(String msg, int imgResId, String btnText, View.OnClickListener onClickListener);

    void showError(String msg, int imgResId, String btnText, View.OnClickListener onClickListener);

    void restore();

    void showToast(String msg);

    // 展示进度条
    void showProgressDialog();

    // 关闭进度条
    void dismissProgressDialog();

}
