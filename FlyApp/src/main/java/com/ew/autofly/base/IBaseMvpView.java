package com.ew.autofly.base;



public interface IBaseMvpView {

    void showToast(String toast);

    void showLoading(boolean isShow, String loadingMsg);

    void showError(boolean isShow,String errorMsg);

    void showEmpty(boolean isShow,String emptyMsg);
}
