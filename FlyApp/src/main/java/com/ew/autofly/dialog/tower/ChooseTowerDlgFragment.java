package com.ew.autofly.dialog.tower;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.dialog.ui.adapter.TowerAdapter;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.Tower;
import com.ew.autofly.interfaces.OnChooseTowerDialogClickListener;
import com.ew.autofly.utils.MyUtils;
import com.ew.autofly.utils.ToastUtil;
import com.ew.autofly.utils.coordinate.LocationCoordinateUtils;
import com.ew.autofly.xflyer.utils.CoordinateUtils;

import java.util.ArrayList;

public class ChooseTowerDlgFragment extends BaseDialogFragment implements OnClickListener {

    public final static String TAG_CHOOSE_TOWER = "tower";
    public final static String ARG_PARAM_CHOOSE_TOWER_LIST = "towerList";
    public final static String ARG_PARAM_CHOOSE_TOWER_AIRCRAFT_LOCATION = "CHOOSE_TOWER_AIRCRAFT_LOCATION";

    private CheckBox cbAll;
    private TextView tvConfirm;
    private ListView mLvTower;

    protected ArrayList<Tower> allTowerList = new ArrayList<>();
    protected ArrayList<Tower> selectedTowerList = new ArrayList<>();

    protected LocationCoordinate mAirCraftLocation;

    private TowerAdapter adapter;
    protected OnChooseTowerDialogClickListener mOnChooseTowerListener;

    public void setOnChooseTowerListener(OnChooseTowerDialogClickListener onChooseTowerListener) {
        mOnChooseTowerListener = onChooseTowerListener;
    }

    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            selectedTowerList.clear();
            for (Tower tower : allTowerList) {
                tower.setChecked(isChecked);
            }
            if (isChecked)
                selectedTowerList.addAll(allTowerList);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    protected void onCreateSize() {
        setSize(0.7f, 0.85f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_choose_tower, container, false);
        bindField(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        findNearestTower();
    }

    private void bindField(View view) {
        mLvTower = (ListView) view.findViewById(R.id.lv_tower);
        adapter = new TowerAdapter(getContext(), allTowerList);
        mLvTower.setAdapter(adapter);
        tvConfirm = (TextView) view.findViewById(R.id.tv_confirm);
        tvConfirm.setOnClickListener(this);
        view.findViewById(R.id.ib_close).setOnClickListener(this);
        cbAll = (CheckBox) view.findViewById(R.id.cb_select_all);
        cbAll.setOnCheckedChangeListener(checkedChangeListener);


        mLvTower.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Tower tower = allTowerList.get(position);
                if (tower.isChecked()) {
                    mLvTower.setItemChecked(position, false);
                    allTowerList.get(position).setChecked(false);

                } else {
                    mLvTower.setItemChecked(position, true);
                    allTowerList.get(position).setChecked(true);

                    if (areAllChecked())
                        cbAll.setChecked(true);
                }
                adapter.notifyDataSetChanged();
            }
        });
        if (areAllChecked())
            cbAll.setChecked(true);
    }

    private boolean areAllChecked() {
        boolean result = true;
        for (Tower tower : allTowerList) {
            if (!tower.isChecked()) {
                result = false;
                break;
            }
        }
        return result;
    }

    private void initData() {
        this.allTowerList = getArguments().getParcelableArrayList(ARG_PARAM_CHOOSE_TOWER_LIST);
        this.mAirCraftLocation = getArguments().getParcelable(ARG_PARAM_CHOOSE_TOWER_AIRCRAFT_LOCATION);
    }

    private void findNearestTower() {
        if (LocationCoordinateUtils.checkGpsCoordinate(mAirCraftLocation)) {
            int position = MyUtils.findNearestTowerPositionByLocation(allTowerList, mAirCraftLocation);
            mLvTower.setSelection(position);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_confirm:

                selectedTowerList.clear();
                for (Tower t : allTowerList) {
                    if (t.isChecked()) {
                        selectedTowerList.add(t);
                    }
                }
                if (selectedTowerList.size() < 2) {
                    ToastUtil.show(getActivity(), "请至少选择两个杆塔");
                    return;
                }

                if (mOnChooseTowerListener != null) {
                    mOnChooseTowerListener.onChooseTowerConfirm(TAG_CHOOSE_TOWER, selectedTowerList);
                }
                dismiss();
                break;
            case R.id.ib_close:
                dismiss();
                break;
        }
    }
}