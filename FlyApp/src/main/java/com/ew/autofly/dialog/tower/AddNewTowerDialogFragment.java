package com.ew.autofly.dialog.tower;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.entity.Tower;
import com.ew.autofly.utils.ToastUtil;
import com.ew.autofly.utils.business.TowerUtils;

import java.util.ArrayList;
import java.util.List;



public class AddNewTowerDialogFragment extends DialogFragment implements View.OnClickListener {
    private View view;
    private EditText etTowerNo;
    private List<Tower> towerList = new ArrayList<>();
    private Tower tower;
    private boolean dismiss = true;
    private int currentOverrideTowerIndex = 0;

    private AddNewTowerListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
        towerList = (List<Tower>) getArguments().getSerializable("tower_list");
        tower = new Tower();
        tower.setLatitude(getArguments().getDouble("latitude"));
        tower.setLongitude(getArguments().getDouble("longitude"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_add_new_tower, container, false);
        etTowerNo = (EditText) view.findViewById(R.id.et_tower_no);
        String previousTowerNo = getLastTowerNo();
        if (TextUtils.isEmpty(previousTowerNo)) {
            etTowerNo.setText("01");
        } else {
            etTowerNo.setText(TowerUtils.getAutoIncreaseTowerNo(previousTowerNo));
        }
        int length = etTowerNo.getText() == null ? 0 : etTowerNo.getText().length();
        etTowerNo.setSelection(length);
        view.findViewById(R.id.btn_confirm).setOnClickListener(this);
        view.findViewById(R.id.ib_close).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_close:
                dismiss();
                break;
            case R.id.btn_confirm:
                if (TextUtils.isEmpty(etTowerNo.getText())) {
                    ToastUtil.show(EWApplication.getInstance(), "杆塔编号不能为空！");
                    return;
                }
                dismiss = true;
                for (Tower tower : towerList) {
                    if (tower.getTowerNo().equals(etTowerNo.getText().toString())) {
                        dismiss = false;
                        showAlertDialog(mListener);
                        break;
                    }
                    currentOverrideTowerIndex++;
                }

                if (dismiss) {
                    if (mListener != null) {
                        tower.setTowerNo(etTowerNo.getText().toString().trim());
                        mListener.OnAddNewTowerConfirm(false, tower, -1);
                        dismiss();
                    }
                }
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
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.5), ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    public void setListener(AddNewTowerListener mListener) {
        this.mListener = mListener;
    }

    public interface AddNewTowerListener {
        void OnAddNewTowerConfirm(boolean replace, Tower tower, int towerIndex);
    }

    private void showAlertDialog(final AddNewTowerListener listener) {
        CustomDialog.Builder deleteDialog = new CustomDialog.Builder(getActivity());
        deleteDialog.setTitle(getActivity().getString(R.string.notice))
                .setMessage(getActivity().getString(R.string.txt_override_exist_tower_number))
                .setPositiveButton(getActivity().getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            tower.setTowerNo(etTowerNo.getText().toString().trim());
                            mListener.OnAddNewTowerConfirm(true, tower, currentOverrideTowerIndex);
                            dismiss();
                        }
                    }
                })
                .setNegativeButton(getActivity().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    /**
     * 获取最后一个杆塔编号
     *
     * @return
     */
    private String getLastTowerNo() {
        if (towerList != null && !towerList.isEmpty()) {
            return towerList.get(towerList.size() - 1).getTowerNo();
        }
        return "";
    }

}