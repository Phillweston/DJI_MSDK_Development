package com.ew.autofly.dialog.tower;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.utils.ToastUtil;

import java.lang.reflect.Field;

public class SaveTowerGroupDialogFragment extends DialogFragment implements View.OnClickListener {

    private EditText etLineName, etVoltage, etGroupName;

    private SaveTowerGroupListener mListener;
    private View view;
    private Spinner mSpVoltage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_save_tower_group, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_close:
                dismiss();
                break;
            case R.id.btn_confirm:
                if (TextUtils.isEmpty(etLineName.getText())) {
                    ToastUtil.show(EWApplication.getInstance(), "线路名称不能为空！");
                    return;
                }
                if (TextUtils.isEmpty(etVoltage.getText())) {
                    ToastUtil.show(EWApplication.getInstance(), "电压等级不能为空！");
                    return;
                }
                if (TextUtils.isEmpty(etGroupName.getText())) {
                    ToastUtil.show(EWApplication.getInstance(), "管理班组不能为空！");
                    return;
                }


                if (mListener != null) {
                    mListener.onSaveTowerGroupConfirm(etLineName.getText().toString(),
                            etVoltage.getText().toString(), etGroupName.getText().toString());
                }
                dismiss();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            if (dm.widthPixels > dm.heightPixels)
                dialog.getWindow().setLayout((int) (dm.widthPixels * 0.5), ViewGroup.LayoutParams.WRAP_CONTENT);
            else
                dialog.getWindow().setLayout((int) (dm.heightPixels * 0.5), ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    public void setListener(SaveTowerGroupListener mListener) {
        this.mListener = mListener;
    }

    private void initView(View view) {
        etLineName = (EditText) view.findViewById(R.id.et_line_name);
        etVoltage = (EditText) view.findViewById(R.id.et_voltage);
        etGroupName = (EditText) view.findViewById(R.id.et_group_name);
        view.findViewById(R.id.btn_confirm).setOnClickListener(this);
        view.findViewById(R.id.ib_close).setOnClickListener(this);
        mSpVoltage = (Spinner) view.findViewById(R.id.sp_voltage);
        mSpVoltage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String[] voltageArray = getResources().getStringArray(R.array.spinner_voltage);
                String voltage=voltageArray[position];
                etVoltage.setText(voltage);

                try {

                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);
                    field.setInt(mSpVoltage, AdapterView.INVALID_POSITION);
                    TextView tv = (TextView)view;
                    tv.setTextColor(getResources().getColor(R.color.transparent));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public interface SaveTowerGroupListener {

        void onSaveTowerGroupConfirm(String lineName, String voltage, String groupName);
    }
}