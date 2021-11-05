package com.flycloud.autofly.ux.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;



public class BaseViewPagerFragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> listData;

    private List<CharSequence> listTitle;

    public BaseViewPagerFragmentAdapter(FragmentManager fm, List<Fragment> listData){
        this(fm,listData,null);
    }

    public BaseViewPagerFragmentAdapter(FragmentManager fm, List<Fragment> listData, List<CharSequence> listTitle) {
        super(fm);
        this.listData = listData;
        this.listTitle = listTitle;
    }


    public List<Fragment> getListData() {
        return listData;
    }

    public void setListData(List<Fragment> listData) {
        this.listData = listData;
    }

    public List<CharSequence> getListTitle() {
        return listTitle;
    }

    public void setListTitle(List<CharSequence> listTitle) {
        this.listTitle = listTitle;
    }

    @Override
    public Fragment getItem(int position) {
        return listData==null ? null : listData.get(position) ;
    }

    @Override
    public int getCount() {
        return listData == null ? 0 : listData.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        if(listTitle!=null && listTitle.size()!=0){
            return listTitle.get(position);
        }
        return super.getPageTitle(position);
    }

}