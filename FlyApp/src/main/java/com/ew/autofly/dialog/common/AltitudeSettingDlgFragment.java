package com.ew.autofly.dialog.common;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.entity.AirRouteParameter;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.entity.Tower;
import com.ew.autofly.interfaces.OnSetAltitudeListener;
import com.ew.autofly.utils.DemReaderUtils;
import com.ew.autofly.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;



public class AltitudeSettingDlgFragment extends BaseDialogFragment implements View.OnClickListener {
    private TextView tvConfirm;
    private ImageButton ibCancel;
    private TextView tvBaseAltitude;
    private List<LatLngInfo> latLngInfoList = new ArrayList<>();
    private List<Tower> selectedTowerList = new ArrayList<>();
    private AirRouteParameter airRouteParameter;
    private GridView gvEdit;
    private WayPointGridAdapter adapter;
    private LatLngInfo aircraftLocation;
    private DemReaderUtils demReaderUtils = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_waypoint_altitude_setting, container, false);
        bindField(view);
        initData();
        return view;
    }

    @Override
    protected void onCreateSize() {
        setSize(0.8f, 0.9f);
    }

    private void bindField(View view) {
        gvEdit = (GridView) view.findViewById(R.id.gv_edit);
        ibCancel = (ImageButton) view.findViewById(R.id.ib_cancel);
        tvConfirm = (TextView) view.findViewById(R.id.tv_ok);
        tvBaseAltitude = (TextView) view.findViewById(R.id.txt_base_altitude);
        ibCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
    }

    private void initData() {
        demReaderUtils = DemReaderUtils.getInstance(getContext());

        if (getArguments() != null) {
            latLngInfoList = (List<LatLngInfo>) getArguments().getSerializable("routeInfoList");
            airRouteParameter = (AirRouteParameter) getArguments().getSerializable("airRoutePara");
            selectedTowerList = (List<Tower>) getArguments().getSerializable("selectedTowerList");
            aircraftLocation = (LatLngInfo) getArguments().getSerializable("aircraftLocation");
        }
        if (selectedTowerList == null || selectedTowerList.size() == 0) {
            adapter = new WayPointGridAdapter(getContext(), latLngInfoList, airRouteParameter.getFixedAltitudeList());
        } else {
            adapter = new WayPointGridAdapter(getContext(), latLngInfoList, airRouteParameter.getFixedAltitudeList(), selectedTowerList);
        }
        gvEdit.setAdapter(adapter);

        double demAltitude = demReaderUtils.getZValue(aircraftLocation);
        tvBaseAltitude.setText(demReaderUtils.checkZValue(demAltitude) ? String.format("基准面海拔:\n%s m", demAltitude) : "");

    }

    @Override
    public void onClick(View v) {
        OnSetAltitudeListener act = (OnSetAltitudeListener) getFragmentManager().findFragmentByTag(getTag().replace("_altitude_setting", "_patrol"));
        switch (v.getId()) {
            case R.id.tv_ok:
                if (isValid()) {
                    changeAltitude();
                    act.onSetAltitudeComplete(true, null);
                    dismiss();
                }
                break;
            case R.id.ib_cancel:
                act.onSetAltitudeComplete(false, null);
                dismiss();
                break;
        }
    }

    private boolean isValid() {
        for (int i = 0; i < gvEdit.getChildCount(); i++) {
            LinearLayout layout = (LinearLayout) gvEdit.getChildAt(i);
            EditText et = (EditText) layout.findViewById(R.id.et_altitude);
            String str = et.getText().toString().trim();
            if (str.equals("")) {
                ToastUtil.show(getActivity(), "航高不能为空!");
                return false;
            }
            if (Integer.parseInt(str) > 495 || Integer.parseInt(str) < 20) {
                ToastUtil.show(getActivity(), "航高范围应设置在20m~495m之间");
                return false;
            }
        }
        return true;
    }

    private void changeAltitude() {

        String listAltitude = "";
        int listSize = latLngInfoList.size() == 0 ? selectedTowerList.size() : latLngInfoList.size();
        for (int i = 0; i < listSize; i++) {
            listAltitude += String.valueOf((adapter.getItem(i))) + ",";
            if (selectedTowerList != null && i < selectedTowerList.size()) {
                int altitude = (int) adapter.getItem(i);
                selectedTowerList.get(i).setFlyAltitude(altitude);
            }
        }
        airRouteParameter.setFixedAltitude(false);
        airRouteParameter.setFixedAltitudeList(listAltitude);
    }

    class WayPointGridAdapter extends BaseAdapter implements TextView.OnEditorActionListener {
        private Context mContext;
        private List<ItemEntity> _list = new ArrayList<>();
        private String[] altitudeList;
        private List<Tower> towers;

        public WayPointGridAdapter(Context context, List<LatLngInfo> list, String altitudeList) {
            this.mContext = context;
            this.altitudeList = altitudeList.split(",");
            if (this.altitudeList.length == list.size()) {
                for (int i = 0; i < this.altitudeList.length; i++) {
                    _list.add(new ItemEntity(Integer.parseInt(this.altitudeList[i])));
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    _list.add(new ItemEntity(115));
                }
            }
            towers = null;
        }

        public WayPointGridAdapter(Context context, List<LatLngInfo> list, String altitudeList, List<Tower> towers) {
            this.mContext = context;
            this.altitudeList = altitudeList.split(",");
            if (this.altitudeList.length == towers.size()) {
                for (int i = 0; i < this.altitudeList.length; i++) {
                    _list.add(new ItemEntity(Integer.parseInt(this.altitudeList[i])));
                }
            } else {
                for (int i = 0; i < towers.size(); i++) {
                    _list.add(new ItemEntity(115));
                }
            }
            this.towers = towers;
        }

        @Override
        public int getCount() {
            return this._list.size();
        }

        @Override
        public Object getItem(int position) {
            return this._list.size() == 0 ? null : this._list.get(position).getAltitude();
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
                holder = new ViewHolder();
                holder.tvNo = (TextView) convertView.findViewById(R.id.tv_point_no);
                holder.tvAltitude = (TextView) convertView.findViewById(R.id.tv_waypoint_altitude);
                holder.etAltitude = (EditText) convertView.findViewById(R.id.et_altitude);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.etAltitude.setTag(_list.get(position));

            if (towers == null) {
                holder.tvNo.setText(String.format("航点%d:", position + 1));
            } else if (towers.size() != 0) {
                holder.tvNo.setText("杆塔" + towers.get(position).getTowerNo());
            }
            ItemEntity altitude = _list.get(position);
            if (position == _list.size() - 1) {
                holder.etAltitude.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            }
            if (towers == null) {
                double demAltitude = demReaderUtils.getZValue(latLngInfoList.get(position));
                holder.tvAltitude.setText(demReaderUtils.checkZValue(demAltitude) ? String.format("海拔：%sm", demAltitude) : "");
            } else {
                Tower tower = towers.get(position);
                double demAltitude = demReaderUtils.getZValue(new LatLngInfo(tower.getLatitude(), tower.getLongitude()));
                holder.tvAltitude.setText(demReaderUtils.checkZValue(demAltitude) ? String.format("海拔：%sm", demAltitude) : "");
            }
            if (position == 0) {
                holder.etAltitude.setOnEditorActionListener(this);
            }
            holder.etAltitude.setText(String.valueOf(altitude.getAltitude()));
            holder.etAltitude.setSelection(holder.etAltitude.getText().length());
            holder.etAltitude.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String str = s.toString();
                    try {
                        if (str.equals("")) {
                            ItemEntity altitude = (ItemEntity)holder.etAltitude.getTag();
                            altitude.setAltitude(0);
                        } else if (Integer.parseInt(str) > 495) {
                            ItemEntity altitude = (ItemEntity)holder.etAltitude.getTag();
                            altitude.setAltitude(495);
                            str = "495";
                            holder.etAltitude.setText(str);
                        } else {
                            ItemEntity altitude = (ItemEntity)holder.etAltitude.getTag();
                            altitude.setAltitude(Integer.parseInt(str));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
            return convertView;
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                double demAltitude;
                int altitude;
                if (towers == null) {
                    demAltitude = demReaderUtils.getZValue(latLngInfoList.get(0));
                } else {
                    Tower tower = towers.get(0);
                    demAltitude = demReaderUtils.getZValue(new LatLngInfo(tower.getLatitude(), tower.getLongitude()));
                }
                altitude = _list.get(0).getAltitude();
                List<ItemEntity> _listTemp = new ArrayList<>();

                for (int i = 0; i < _list.size(); i++) {
                    if (i == 0) {
                        _listTemp.add(new ItemEntity(_list.get(0).getAltitude()));
                    } else {
                        LatLngInfo latLngInfo = towers == null ?
                                latLngInfoList.get(i) : new LatLngInfo(towers.get(i).getLatitude(), towers.get(i).getLongitude());
                        double demAltitudeTemp = demReaderUtils.getZValue(latLngInfo);
                        _listTemp.add(new ItemEntity((int) (((demReaderUtils.checkZValue(demAltitudeTemp) ? demAltitudeTemp : 0))
                                + (altitude - (demReaderUtils.checkZValue(demAltitude) ? demAltitude : 0)))));
                    }
                }
                _list = _listTemp;
                notifyDataSetChanged();
            }
            return false;
        }

        class ViewHolder {
            TextView tvNo;
            TextView tvAltitude;
            EditText etAltitude;
        }
    }

    class ItemEntity {

        public ItemEntity(int altitude) {
            this.altitude = altitude;
        }

        private int altitude;

        public int getAltitude() {
            return altitude;
        }

        public void setAltitude(int altitude) {
            this.altitude = altitude;
        }
    }
}