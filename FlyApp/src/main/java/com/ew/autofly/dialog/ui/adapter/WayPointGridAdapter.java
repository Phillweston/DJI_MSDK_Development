package com.ew.autofly.dialog.ui.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import com.ew.autofly.R;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.entity.Tower;

import java.util.ArrayList;
import java.util.List;



public class WayPointGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<Integer> _list = new ArrayList<>();
    private String[] altitudeList;
    private List<Tower> towers;

    public WayPointGridAdapter(Context context, List<LatLngInfo> list, String altitudeList) {
        this.mContext = context;
        this.altitudeList = altitudeList.split(",");
        if (this.altitudeList.length == list.size()) {
            for (int i = 0; i < this.altitudeList.length; i++) {
                _list.add(Integer.parseInt(this.altitudeList[i]));
            }
        } else {
            for (int i = 0; i < list.size(); i++) {
                _list.add(115);
            }
        }
        towers = null;
    }

    public WayPointGridAdapter(Context context, List<LatLngInfo> list, String altitudeList, List<Tower> towers) {
        this.mContext = context;
        this.altitudeList = altitudeList.split(",");
        if (this.altitudeList.length == list.size()) {
            for (int i = 0; i < this.altitudeList.length; i++) {
                _list.add(Integer.parseInt(this.altitudeList[i]));
            }
        } else {
            for (int i = 0; i < list.size(); i++) {
                _list.add(115);
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
            holder = new ViewHolder();
            holder.tvNo = (TextView) convertView.findViewById(R.id.tv_point_no);
            holder.etAltitude = (EditText) convertView.findViewById(R.id.et_altitude);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        if (towers == null) {
            holder.tvNo.setText(String.format("航点%d:", position + 1));
        } else if (towers.size() != 0){
            holder.tvNo.setText("杆塔" + towers.get(position).getTowerNo());
        }
        int altitude = _list.get(position);
        if (position == _list.size() - 1) {
            holder.etAltitude.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        }
        holder.etAltitude.setText(String.valueOf(altitude));
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
                if (str.equals("")) {
                    _list.set(position, 0);
                    return;
                } else if (Integer.parseInt(str) > 495) {
                    str = "495";
                    holder.etAltitude.setText(str);
                } else if (Integer.parseInt(str) < 20) {
                    str = "20";
                }
                _list.set(position, Integer.parseInt(str));
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView tvNo;
        EditText etAltitude;
    }
}