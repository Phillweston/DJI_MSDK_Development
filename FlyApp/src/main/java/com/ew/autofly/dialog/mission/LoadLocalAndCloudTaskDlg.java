package com.ew.autofly.dialog.mission;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.ew.autofly.R;
import com.ew.autofly.adapter.BaseViewPagerFragmentAdapter;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.entity.Mission2;
import com.ew.autofly.dialog.ui.fragment.LocalMissionFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class LoadLocalAndCloudTaskDlg extends BaseDialogFragment {

    private String workMode;

    private ArrayList<String> selectedIdList = new ArrayList<>();

    private BaseViewPagerFragmentAdapter mViewPagerFragmentAdapter;

    private TabLayout mSwitchView;

    private ViewPager mViewPager;

    private List<CharSequence> mListTitle;

    private List<Fragment> mListData;

    private LocalMissionFragment localMissionFragment;

    private OnItemLoadClickListener onItemLoadClickListener;

    private OnItemReviewClickListener onItemReviewClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workMode = getArguments().getString("mode");
        selectedIdList = getArguments().getStringArrayList("selectedIdList");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_load_task2, container, false);
        view.findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mViewPager = (ViewPager) view.findViewById(R.id.vp_load_task);
        mSwitchView = (TabLayout) view.findViewById(R.id.tl_switch_content);

        initView();
        return view;

    }

    @Override
    protected void onCreateSize() {
        setSize(0.7f, 0.9f);
    }

    private void initView() {
        mListTitle = new ArrayList<>();
        mListTitle.add("本地任务");
        mListTitle.add("云端任务");

        Bundle args = new Bundle();
        args.putString("mode", workMode);
        args.putStringArrayList("selectedIdList", selectedIdList);

        localMissionFragment = LocalMissionFragment.newInstance(args);

        initListener();

        mListData = new ArrayList<>();
        mListData.add(localMissionFragment);

        mViewPagerFragmentAdapter = new BaseViewPagerFragmentAdapter(getChildFragmentManager(), mListData, mListTitle);
        mViewPager.setAdapter(mViewPagerFragmentAdapter);
        mSwitchView.setupWithViewPager(mViewPager);
    }

    private void initListener() {
        localMissionFragment.setOnItemClickListener(new LocalMissionFragment.OnItemLoadClickListener() {
            @Override
            public void onItemLoadClick(Mission2 mission) {
                onItemLoadClickListener.onItemLoadClick(mission);
            }
        });
        localMissionFragment.setOnItemReviewClickListener(new LocalMissionFragment.OnItemReviewClickListener() {
            @Override
            public void onItemReviewClick() {
                onItemReviewClickListener.onItemReviewClick(selectedIdList);
                dismiss();
            }
        });
        localMissionFragment.setOnFragmentDismissListener(new LocalMissionFragment.OnFragmentDismissListener() {
            @Override
            public void OnFragmentDismiss() {
                dismiss();
            }
        });
    }

    public interface OnItemLoadClickListener {
        void onItemLoadClick(Mission2 mission);
    }

    public void setOnItemClickListener(OnItemLoadClickListener onItemLoadClickListener) {
        this.onItemLoadClickListener = onItemLoadClickListener;
    }

    public interface OnItemReviewClickListener {
        void onItemReviewClick(ArrayList<String> selectIdList);
    }

    public void setOnItemReviewClickListener(OnItemReviewClickListener onItemReviewClickListener) {
        this.onItemReviewClickListener = onItemReviewClickListener;
    }
}