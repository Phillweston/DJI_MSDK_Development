package com.ew.autofly.dialog.tower;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.Tower;
import com.ew.autofly.event.flight.LocationStateEvent;
import com.ew.autofly.interfaces.IConfirmListener;
import com.ew.autofly.model.AircraftManager;
import com.ew.autofly.utils.DemReaderUtils;
import com.ew.autofly.utils.coordinate.LocationCoordinateUtils;
import com.flycloud.autofly.ux.interfaces.OnDialogClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.ew.autofly.dialog.tower.TowerFlyAltitudeInputDialog.PARAMS_IS_SET_MAX_TOWER_ALTITUDE;




public class TowerAttitudeSettingDialog extends BaseDialogFragment implements View.OnClickListener {

    public final static String TAG_TOWER_ALTITUDE = "TAG_TOWER_ALTITUDE";
    public final static String PARAMS_TOWER_ALTITUDE_SELECTEDLIST = "PARAMS_TOWER_ALTITUDE_SELECTEDLIST";
    public final static String PARAMS_TOWER_DEFAULT_ALTITUDE = "PARAMS_TOWER_DEFAULT_ALTITUDE";

    private TextView tvConfirm;
    private ImageButton ibCancel;
    private GridView gvEdit;

    
    private Button mBtnQuickSetting;
    private TextView mTvHomeSeaLevel;

    private WayPointGridAdapter adapter;

    private boolean isAltitudeDemSource = true;

    private DemReaderUtils demReaderUtils = null;
    private IConfirmListener mIConfirmListener;

    private List<Tower> selectedTowerList = new ArrayList<>();

    private float homeAltitude = 0;

  
    private int mDefaultAltitude = 0;

  
    private boolean isRtkCoordinate;

  
    private LocationCoordinate mPlaneLoc;


    public void setConfirmListener(IConfirmListener IConfirmListener) {
        mIConfirmListener = IConfirmListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_common_waypoint_altitude_setting, container, false);
        initView(view);
        return view;
    }

    @Override
    protected void onCreateSize() {
        setSize(0.8f, 0.9f);
    }

    private void initData() {
        demReaderUtils = DemReaderUtils.getInstance(getContext());
        selectedTowerList = (List<Tower>) getArguments().getSerializable(PARAMS_TOWER_ALTITUDE_SELECTEDLIST);
        mDefaultAltitude = getArguments().getInt(PARAMS_TOWER_DEFAULT_ALTITUDE, 0);
    }

    private void initView(View view) {
        gvEdit = (GridView) view.findViewById(R.id.gv_edit);
        ibCancel = (ImageButton) view.findViewById(R.id.ib_cancel);
        tvConfirm = (TextView) view.findViewById(R.id.tv_ok);
        mBtnQuickSetting = (Button) view.findViewById(R.id.btn_quick_setting);
        mBtnQuickSetting.setOnClickListener(this);
        mTvHomeSeaLevel = (TextView) view.findViewById(R.id.tv_home_sea_level);
        ibCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);

        adapter = new WayPointGridAdapter(getContext(), selectedTowerList);
        gvEdit.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        getHomeAltitude(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                if (!checkAltitudeValid()) {
                    showToast("有输入高度为空或不正确，请重新检查输入");
                    return;
                }
                changeAltitude();
                if (mIConfirmListener != null) {
                    mIConfirmListener.onConfirm(TAG_TOWER_ALTITUDE, null);
                }
                dismiss();

                break;
            case R.id.ib_cancel:
                dismiss();
                break;
            case R.id.btn_quick_setting:
                quickSetting();
                break;
        }
    }

    private void quickSetting() {

        TowerFlyAltitudeSourceDlg sourceDlg = new TowerFlyAltitudeSourceDlg();
        sourceDlg.show(getFragmentManager());
        sourceDlg.setBottomClickListener(new OnDialogClickListener() {
            @Override
            public void onClick(int which, Object data) {
                if (which == BUTTON_POSITIVE) {
                    if ((Boolean) data) {
                        checkDemValid();
                    } else {
                        checkKmlValid();
                    }
                }
            }
        });

    }

    private void setTvHomeSeaLevel() {
        mTvHomeSeaLevel.setText("起飞点海拔:" + homeAltitude + "m");
    }

    private void changeAltitude() {

        /*List<Tower> resultList = adapter.getResult();
        for (int i = 0; i < resultList.size(); i++) {
            int altitude = resultList.get(i).getFlyAltitude();
            selectedTowerList.get(i).setFlyAltitude(altitude);
        }*/
        selectedTowerList = adapter.getResult();

 /*       StringBuilder stringBuilder = new StringBuilder();
        for (Tower tower : selectedTowerList) {
            stringBuilder.append(tower.getTowerNo() + ":" + tower.getFlyAltitude() + "  ");
        }
        Log.e("changeAltitude--", stringBuilder.toString());*/
    }

    private boolean checkAltitudeValid() {
        List<Tower> result = adapter.getResult();
        for (Tower tower : result) {
            if (tower.getFlyAltitude() == Tower.ALTITUDE_NO_VALUE
                    || tower.getFlyAltitude() == Tower.ALTITUDE_INPUT_ERROR) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查dem数据是否正确
     *
     * @return
     */
    private void checkDemValid() {
        if (getHomeAltitude(true)) {

          
            for (Tower tower : selectedTowerList) {
                double towerPositionAltitude = demReaderUtils.getZValue(new LatLngInfo(tower.getLatitude(), tower.getLongitude()));
                if (!demReaderUtils.checkZValue(towerPositionAltitude)) {
                    showNotice("读取杆塔位置高程数据失败，请先下载地形数据或者检查是否所选杆塔有高程数据");
                    return;
                }
            }

            showTowerFlyAltitudeInputDialog(true);
        }
    }

    private void checkKmlValid() {
      
        for (Tower tower : selectedTowerList) {
            if (tower.getTopAltitude() == Tower.ALTITUDE_NO_VALUE) {
                showNotice("无塔顶海拔数据");
                return;
            }
        }

        if (!LocationCoordinateUtils.checkGpsCoordinate(mPlaneLoc)) {
            showNotice("未定位到起飞点，请先连接飞机获取起飞点");
            return;
        }

        if (!AircraftManager.isRtkAircraft()) {
            showNotice("当前不是RTK飞机");
            return;
        }

        showTowerFlyAltitudeInputDialog(false);
    }

    private void showTowerFlyAltitudeInputDialog(boolean isAltitudeDemSource) {
        TowerFlyAltitudeInputDialog dialog = new TowerFlyAltitudeInputDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean(PARAMS_IS_SET_MAX_TOWER_ALTITUDE, isAltitudeDemSource);
        dialog.setArguments(bundle);
        dialog.setOnConfirmListener(new IConfirmListener() {
            @Override
            public void onConfirm(String tag, Object object) {
                int altitude = (int) object;
                if (isAltitudeDemSource) {
                    quickSetAltitudeByDem(altitude);
                } else {
                    quickSetAltitudeByKml(altitude);
                }
            }
        });
        dialog.show(getFragmentManager());
    }

    private void quickSetAltitudeByDem(int towerAltitude) {
        for (Tower tower : selectedTowerList) {
            double towerPositionAltitude = demReaderUtils.getZValue(new LatLngInfo(tower.getLatitude(), tower.getLongitude()));
            tower.setFlyAltitude((int) (towerPositionAltitude - homeAltitude) + towerAltitude);
        }
        setTvHomeSeaLevel();
        adapter.notifyDataSetChanged();
    }

    private void quickSetAltitudeByKml(int towerAltitude) {

        if (!isRtkCoordinate) {
            showNotice("当前RTK信号为非固定解");
            return;
        }

        float homeAltitude = mPlaneLoc.altitude;

        for (Tower tower : selectedTowerList) {

            tower.setFlyAltitude((int) (tower.getTopAltitude() - homeAltitude));
        }
        this.homeAltitude = homeAltitude;
        setTvHomeSeaLevel();
        adapter.notifyDataSetChanged();
    }

    private boolean getHomeAltitude(boolean showToast) {

        if (!LocationCoordinateUtils.checkGpsCoordinate(mPlaneLoc)) {
            if (showToast) {
                showNotice("未定位到起飞点，请先连接飞机获取起飞点");
            }
            return false;
        }
        final double homeAltitude = demReaderUtils.getZValue(new LatLngInfo(mPlaneLoc.getLatitude(), mPlaneLoc.getLongitude()));
        if (!demReaderUtils.checkZValue(homeAltitude)) {
            if (showToast) {
                showNotice("当前起飞点位置无地形数据，请先下载地形数据");
            }
            return false;
        }
        this.homeAltitude = (int) homeAltitude;
        setTvHomeSeaLevel();
        return true;
    }

    private void showNotice(String message) {
        CustomDialog.Builder dialog = new CustomDialog.Builder(getActivity());
        dialog.setTitle(getResources().getString(R.string.notice))
                .setMessage(message)
                .setPositiveButton(R.string.sure, null)
                .create()
                .show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationStateEvent(LocationStateEvent locationStateEvent) {

        mPlaneLoc = locationStateEvent.getAircraftCoordinate();
        isRtkCoordinate = locationStateEvent.isRtkCoordinate();

    }

    public class WayPointGridAdapter extends BaseAdapter {
        private Context mContext;
        private List<Tower> _list;

        public WayPointGridAdapter(Context context, List<Tower> list) {
            this.mContext = context;
            _list = new ArrayList<>(list);
          /*  try {
                this._list = CollectionsUtil.deepCopy(list);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }*/
        }

        public List<Tower> getResult() {
            return _list;
        }

        @Override
        public int getCount() {
            return this._list.size();
        }

        @Override
        public Object getItem(int position) {
            return this._list.size() == 0 ? null : this._list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_waypoint_altitude_edit, parent, false);
                holder = new ViewHolder(convertView);
                holder.updatePosition(position);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.updatePosition(position);
            }
            holder.tvNo.setText("杆塔" + _list.get(position).getTowerNo());
            holder.tvNo.setSelected(true);
            int altitude = _list.get(position).getFlyAltitude();
           /* if (position == _list.size() - 1) {
                holder.etAltitude.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            }*/
            Tower tower = _list.get(position);
            double demAltitude = demReaderUtils.getZValue(new LatLngInfo(tower.getLatitude(), tower.getLongitude()));

            holder.tvAltitude.setText(demReaderUtils.checkZValue(demAltitude) ? String.format("地面海拔：%sm", demAltitude) : "");
            /*float topAltitude = tower.getTopAltitude();
            holder.tvTopAltitude.setText(topAltitude == Tower.ALTITUDE_NO_VALUE ? "" : String.format("塔顶海拔：%sm", topAltitude));*/
            if (altitude != Tower.ALTITUDE_INPUT_ERROR) {
                holder.etAltitude.setText(altitude == Tower.ALTITUDE_NO_VALUE ? String.valueOf(mDefaultAltitude) : String.valueOf(altitude));
            }
            Editable text = holder.etAltitude.getText();
            if (text != null) {
                holder.etAltitude.setSelection(text.length());
            }
            holder.etAltitude.clearFocus();
            return convertView;
        }

        class ViewHolder {
            View rootView;
            TextView tvNo;
            TextView tvAltitude;
            EditText etAltitude;
            TextView tvTopAltitude;

            public ViewHolder(View rootView) {
                this.rootView = rootView;
                this.tvNo = (TextView) rootView.findViewById(R.id.tv_point_no);
                this.tvAltitude = (TextView) rootView.findViewById(R.id.tv_waypoint_altitude);
                this.etAltitude = (EditText) rootView.findViewById(R.id.et_altitude);
                this.tvTopAltitude = rootView.findViewById(R.id.tv_tower_top_altitude);
                this.editTextWatcher = new EditTextWatcher(this.etAltitude);
                this.etAltitude.addTextChangedListener(this.editTextWatcher);
            }

            EditTextWatcher editTextWatcher;

            public void updatePosition(int position) {
                editTextWatcher.updatePosition(position);
            }
        }

        class EditTextWatcher implements TextWatcher {
          

            private int position;
            private EditText editText;

            public EditTextWatcher(EditText editText) {
                this.editText = editText;
            }

            public void updatePosition(int position) {
                this.position = position;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                int altitude = (int) _list.get(position).getFlyAltitude();

                try {

                    if (Float.parseFloat(str) > AppConstant.TOWER_MAX_ALTITUDE) {
                        editText.setText(String.valueOf(AppConstant.TOWER_MAX_ALTITUDE));
                        altitude = AppConstant.TOWER_MAX_ALTITUDE;
                    } else if (Integer.parseInt(str) < AppConstant.TOWER_MIN_ALTITUDE) {
                        editText.setText(String.valueOf(AppConstant.TOWER_MIN_ALTITUDE));
                        altitude = AppConstant.TOWER_MIN_ALTITUDE;
                    } else {
                        altitude = Integer.parseInt(str);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    altitude = Tower.ALTITUDE_INPUT_ERROR;
                }

                _list.get(position).setFlyAltitude(altitude);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
