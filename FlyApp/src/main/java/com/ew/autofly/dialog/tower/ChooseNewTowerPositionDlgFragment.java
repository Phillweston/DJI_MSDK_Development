package com.ew.autofly.dialog.tower;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ew.autofly.R;
import com.ew.autofly.dialog.ui.adapter.TowerAdapter;
import com.ew.autofly.entity.Tower;
import com.ew.autofly.utils.ToastUtil;

import java.util.ArrayList;



public class ChooseNewTowerPositionDlgFragment extends DialogFragment implements View.OnClickListener {

    
    public final static String PARAMS_APPEND_TOWER = "PARAMS_APPEND_TOWER";

    
    public final static String PARAMS_TOWER_LIST = "PARAMS_TOWER_LIST";

    private ListView mLvTower;

    private TowerAdapter adapter;

    protected ArrayList<Tower> allTowerList = new ArrayList<>();

    private int mPosition = -2;

    private Tower mAppendTower;

    private OnSelectedPositionListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
        initData();
    }

    @Override
    public void onStart() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            if (dm.widthPixels > dm.heightPixels)
                dialog.getWindow().setLayout((int) (dm.widthPixels * 0.7), (int) (dm.heightPixels * 0.85));
            else
                dialog.getWindow().setLayout((int) (dm.heightPixels * 0.7), (int) (dm.widthPixels * 0.85));
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_choose_new_tower_position, container, false);
        initView(view);
        return view;
    }

    private void initData() {
        this.mAppendTower = getArguments().getParcelable(PARAMS_APPEND_TOWER);
        this.allTowerList = getArguments().getParcelableArrayList(PARAMS_TOWER_LIST);
    }

    private void initView(View view) {
        mLvTower = (ListView) view.findViewById(R.id.lv_tower);
        adapter = new TowerAdapter(getContext(), allTowerList);
        adapter.setShowGridLineName(false);
        mLvTower.setAdapter(adapter);
        adapter.invalidate();
        view.findViewById(R.id.tv_first_position).setOnClickListener(this);
        view.findViewById(R.id.tv_last_position).setOnClickListener(this);
        view.findViewById(R.id.tv_confirm).setOnClickListener(this);
        view.findViewById(R.id.ib_close).setOnClickListener(this);

        mLvTower.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                adapter.invalidate();
                allTowerList.get(position).setChecked(true);
                adapter.notifyDataSetChanged();
                mPosition = position + 1;
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_first_position:
                mPosition = 0;
                assignmentAppendTower();
                if (mListener != null) {
                    mListener.onSelected(mPosition);
                }
                dismiss();
                break;
            case R.id.tv_last_position:
                mPosition = allTowerList.size();
                assignmentAppendTower();
                if (mListener != null) {
                    mListener.onSelected(mPosition);
                }
                dismiss();
                break;
            case R.id.tv_confirm:
                if (mPosition < 0) {
                    ToastUtil.show(getContext(), "请选择要追加的位置");
                    break;
                }
                assignmentAppendTower();
                if (mListener != null) {
                    mListener.onSelected(mPosition);
                }
                dismiss();
                break;
            case R.id.ib_close:
                dismiss();
                break;
        }
    }

    
    private void assignmentAppendTower() {
        if (!allTowerList.isEmpty() && mAppendTower != null) {
            Tower tempTower;
            if (mPosition == 0) {

                tempTower = allTowerList.get(0);
                mAppendTower.setGridLineName(tempTower.getGridLineName());
                mAppendTower.setManageGroup(tempTower.getManageGroup());
                mAppendTower.setVoltage(tempTower.getVoltage());

            } else if (mPosition > 0) {

                tempTower = allTowerList.get(allTowerList.size() - 1);
                mAppendTower.setGridLineName(tempTower.getGridLineName());
                mAppendTower.setManageGroup(tempTower.getManageGroup());
                mAppendTower.setVoltage(tempTower.getVoltage());

            }
        }
    }

    public void setOnSelectedPositionListener(OnSelectedPositionListener listener) {
        this.mListener = listener;
    }

    public interface OnSelectedPositionListener {
        void onSelected(int position);
    }
}
