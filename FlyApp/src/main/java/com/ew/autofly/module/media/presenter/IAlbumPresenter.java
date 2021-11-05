package com.ew.autofly.module.media.presenter;

import android.content.Context;
import androidx.annotation.NonNull;

import com.ew.autofly.module.media.bean.BaseDataBean;

import java.util.ArrayList;


public interface IAlbumPresenter {

    
    void init(@NonNull ArrayList<BaseDataBean> dataList);

    
    void loadAllData();

    
    void enterPreview(Context context, int position);

    
    void destroy();
}
