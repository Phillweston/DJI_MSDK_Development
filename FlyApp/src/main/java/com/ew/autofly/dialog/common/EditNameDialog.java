package com.ew.autofly.dialog.common;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.flycloud.autofly.ux.view.ClearEditText;

import static android.text.InputType.TYPE_CLASS_TEXT;


public class EditNameDialog extends BaseDialogFragment implements View.OnClickListener {

    private ImageButton mTvCancel;
    private Button mTvOk;
    private TextView mTvTitle;
    private ClearEditText mEtContent;

    private String title;
    private String content;
    private int contentInputType = TYPE_CLASS_TEXT;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_name, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        mTvCancel = view.findViewById(R.id.tv_cancel);
        mTvOk = view.findViewById(R.id.tv_ok);
        mTvTitle = view.findViewById(R.id.tv_title);
        mEtContent = view.findViewById(R.id.et_content);

        mTvCancel.setOnClickListener(this);
        mTvOk.setOnClickListener(this);

        mTvTitle.setText(this.title);

        if (!TextUtils.isEmpty(this.content)) {
            mEtContent.setText(this.content);
            mEtContent.setSelection(this.content.length());
        }

        mEtContent.setInputType(this.contentInputType);

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @see android.text.InputType
     * @param inputType
     */
    public void setContentInputType(int inputType) {
        this.contentInputType = inputType;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                if (TextUtils.isEmpty(mEtContent.getText())) {
                    showToast("输入内容不能为空");
                    return;
                }
                if (mConfirmListener != null) {
                    mConfirmListener.onConfirm(null, mEtContent.getText().toString());
                }
              
                break;
            case R.id.tv_cancel:
                dismiss();
                break;

        }
    }
}
