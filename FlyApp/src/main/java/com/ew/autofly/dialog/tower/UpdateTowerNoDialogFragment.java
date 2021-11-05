package com.ew.autofly.dialog.tower;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.entity.Tower;
import com.ew.autofly.interfaces.IConfirmListener;
import com.ew.autofly.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;



public class UpdateTowerNoDialogFragment extends DialogFragment implements View.OnClickListener {

    private EditText etTowerNo;

    private IConfirmListener confirmClickListener;

    private List<Tower> towerList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;

        setStyle(style, theme);

        towerList = (List<Tower>) getArguments().getSerializable("tower_list");
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_new_tower, container, false);
        etTowerNo = (EditText) view.findViewById(R.id.et_tower_no);
        view.findViewById(R.id.btn_confirm).setOnClickListener(this);
        view.findViewById(R.id.ib_close).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.tv_title)).setText("修改杆塔编号");
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_close:
                dismiss();
                break;
            case R.id.btn_confirm:
                if (TextUtils.isEmpty(etTowerNo.getText())) {
                    ToastUtil.show(EWApplication.getInstance(), "杆塔编号不能为空！");
                    return;
                }

                for (Tower t:towerList){
                    if(t.getTowerNo().equals(etTowerNo.getText().toString())){
                        ToastUtil.show(EWApplication.getInstance(), "存在相同的杆塔编号，请重新输入");
                        return;
                    }
                }

                if (confirmClickListener != null) {
                    confirmClickListener.onConfirm("", etTowerNo.getText().toString());
                }
                dismiss();
                break;
        }
    }

    public void setOnConfirmListener(IConfirmListener confirmClickListener) {
        this.confirmClickListener = confirmClickListener;
    }

}
