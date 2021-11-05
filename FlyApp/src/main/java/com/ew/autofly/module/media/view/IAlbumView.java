package com.ew.autofly.module.media.view;

import com.ew.autofly.base.IBaseMvpView;
import com.ew.autofly.module.media.bean.BaseDataBean;

import java.util.ArrayList;


public interface IAlbumView extends IBaseMvpView {

    /**
     * 刷新数据
     * @param mDataList
     */
    void refreshData(ArrayList<BaseDataBean> mDataList);
}
