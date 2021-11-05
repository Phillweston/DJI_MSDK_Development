package com.ew.autofly.dialog.tool;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ew.autofly.R;

import java.util.List;



public class BluetoothScanDialog extends DialogFragment implements View.OnClickListener {

    public final static String DEVICE_LIST = "DEVICE_LIST";

    private Context mContext;

    private ListView mDeviceLV;

    private List<BluetoothDevice> mDeviceList;

    private DeviceAdapter mDeviceAdapter;

    private onScanListener mListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mDeviceList = getArguments().getParcelableArrayList(DEVICE_LIST);
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_scan_list, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                DisplayMetrics dm = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
                if (dm.widthPixels > dm.heightPixels)
                    window.setLayout((int) (dm.widthPixels * 0.6), (int) (dm.heightPixels * 0.8));
                else
                    window.setLayout((int) (dm.heightPixels * 0.8), (int) (dm.widthPixels * 0.6));
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                window.setBackgroundDrawableResource(android.R.color.transparent);
            }
            dialog.setCanceledOnTouchOutside(true);
        }
    }

    private void initView(View view) {
        view.findViewById(R.id.ib_close).setOnClickListener(this);
        view.findViewById(R.id.tv_cancel).setOnClickListener(this);
        ((TextView)view.findViewById(R.id.tv_title)).setText("搜索蓝牙设备");
        mDeviceLV = (ListView) view.findViewById(R.id.lv_result_list);
        mDeviceAdapter = new DeviceAdapter();
        mDeviceLV.setAdapter(mDeviceAdapter);
        mDeviceLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = (BluetoothDevice) mDeviceAdapter.getItem(position);
                if (mListener != null) {
                    mListener.onSelectDevice(device);
                }
                dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_close:
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (mListener != null) {
            mListener.onCancel();
        }
        super.onDestroy();
    }


    public void refreshDeviceList() {
        if (mDeviceAdapter != null) {
            mDeviceAdapter.notifyDataSetChanged();
        }
    }

    private class DeviceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDeviceList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDeviceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_bluetooth_device, null);
            }

            TextView name = (TextView) convertView.findViewById(R.id.tv_name);
            TextView address = (TextView) convertView.findViewById(R.id.tv_address);
            ImageView bond = (ImageView) convertView.findViewById(R.id.iv_bond);

            BluetoothDevice device = mDeviceList.get(position);
            name.setText(device.getName());
            address.setText(device.getAddress());
            bond.setVisibility(device.getBondState() == BluetoothDevice.BOND_BONDED ? View.VISIBLE : View.GONE);

            return convertView;
        }
    }

    public void setOnScanListener(onScanListener listener) {
        this.mListener = listener;
    }

    public interface onScanListener {
        void onSelectDevice(BluetoothDevice device);

        void onCancel();
    }

}
