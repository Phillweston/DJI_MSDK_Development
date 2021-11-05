package com.ew.autofly.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.ew.autofly.R;
import com.ew.autofly.utils.ToastUtil;

public class EditMissionNameDialogFragment extends DialogFragment {

    private AlertDialog dialog;
    private EditText edit_mission_name;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getTag());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_mission_name, null);
        edit_mission_name = (EditText) view.findViewById(R.id.edit_mission_name);
        builder.setView(view)
                .setPositiveButton("确定",null
        ).setNegativeButton("取消", null);
        dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(edit_mission_name.getText())) {
                    ToastUtil.show(activity, "输入内容不能为空");
                    return;
                }
                if(onSureButtonClickListener!=null){
                    onSureButtonClickListener.sure(edit_mission_name.getText().toString().trim());
                }
                dialog.dismiss();
            }

        });
        return dialog;
    }

    private onSureButtonClickListener onSureButtonClickListener;

    public interface onSureButtonClickListener{
        void sure(String content);
    }

    public void setOnSureButtonClickListener(EditMissionNameDialogFragment.onSureButtonClickListener onSureButtonClickListener) {
        this.onSureButtonClickListener = onSureButtonClickListener;
    }
}
