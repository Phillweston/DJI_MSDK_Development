package com.ew.autofly.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ew.autofly.R;
import com.ew.autofly.adapter.LeftMenuAdapter;
import com.ew.autofly.widgets.CustomPopupWindow;

import java.util.List;



public class MainSecondMenuPop extends CustomPopupWindow {

    private Context mContext;

    private ListView mLvMenu;

    private LeftMenuAdapter mMenuAdapter;

    public MainSecondMenuPop(Context context) {
        this(context, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public MainSecondMenuPop(Context context, int width, int height) {
        this(LayoutInflater.from(context).inflate(R.layout.popwindow_slidemenu, null), width, height, false);
        mContext = context;
    }

    public MainSecondMenuPop(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        mLvMenu = (ListView) contentView.findViewById(R.id.left_list_view_menu);
        init();
    }

    private void init() {
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        setFocusable(true);
        setTouchable(true);
    }

    public void setMenu(List<Integer> icons, List<String> titles) {
        mMenuAdapter = new LeftMenuAdapter(mContext, icons, titles);
        mLvMenu.setAdapter(mMenuAdapter);
        if (titles != null) {
            changeSize(titles.size());
        }
    }

    public void setItemClickListener(AdapterView.OnItemClickListener mItemClickListener) {
        mLvMenu.setOnItemClickListener(mItemClickListener);
    }

    public void setMenuCheck(int position, boolean isCheck) {
        if (position >= 0 && position < mLvMenu.getCount()) {
            mLvMenu.setItemChecked(position, isCheck);
        }
    }

    public void changeSize(int size) {
        if (size > 5) {
            ViewGroup.LayoutParams params = mLvMenu.getLayoutParams();
            params.height = (int) mContext.getResources().getDimension(R.dimen.main_listview_height);
            mLvMenu.setLayoutParams(params);
        }
    }
}
