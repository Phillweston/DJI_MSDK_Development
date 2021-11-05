package com.ew.autofly.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.ew.autofly.R;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.interfaces.TopFragmentClickListener;

public class TopFragment extends BaseFragment implements OnClickListener {
    private ImageButton ivMenu;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_topbar, container, true);
        ivMenu = (ImageButton) view.findViewById(R.id.iv_menu);
        view.findViewById(R.id.iv_menu).setOnClickListener(this);
        view.findViewById(R.id.imgBtnSimulateSetting).setOnClickListener(this);
        view.findViewById(R.id.tvTopTitle).setOnClickListener(this);
        view.findViewById(R.id.imgBtnBattery).setOnClickListener(this);
        view.findViewById(R.id.vw_vision_detection).setOnClickListener(this);
      
        view.findViewById(R.id.imgBtnFigure).setOnClickListener(this);
        view.findViewById(R.id.imgBtnSingle).setOnClickListener(this);
        view.findViewById(R.id.tvTopTitle).setOnClickListener(this);
        view.findViewById(R.id.tvBtnBattery).setOnClickListener(this);
        view.findViewById(R.id.iv_more).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (getParentFragment() instanceof TopFragmentClickListener) {
            ((TopFragmentClickListener) getParentFragment()).onTopFragmentClick(v);
        }
    }
}