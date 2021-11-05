package com.ew.autofly.dialog;

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
import com.ew.autofly.interfaces.IConfirmListener;
import com.flycloud.autofly.base.util.ToastUtil;



public class CustomEditDialogFragment extends DialogFragment implements View.OnClickListener {

    private TextView mTitle;
    private EditText mEdit;

    private IConfirmListener confirmClickListener;

    private String mTitleStr;
    private String mEditHint;
    private String mEditContent;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;

        setStyle(style, theme);
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
        View view = inflater.inflate(R.layout.dialog_custom_edit, container, false);
        initData();
        initView(view);
        return view;
    }

    private void initData(){
        mTitleStr=getArguments().getString("title");
        mEditHint=getArguments().getString("edit_hint");
        mEditContent=getArguments().getString("edit_content");
    }

    private void initView(View view){
        view.findViewById(R.id.btn_confirm).setOnClickListener(this);
        view.findViewById(R.id.ib_close).setOnClickListener(this);
        mTitle=(TextView) view.findViewById(R.id.tv_title);
        mEdit = (EditText) view.findViewById(R.id.et_edit);

        mTitle.setText(mTitleStr);
        mEdit.setHint(mEditHint);
        if (!TextUtils.isEmpty(mEditContent)){
            mEdit.setText(mEditContent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_close:
                dismiss();
                break;
            case R.id.btn_confirm:
                if (TextUtils.isEmpty(mEdit.getText())){
                    ToastUtil.show(getActivity(), getString(R.string.error_input_empty));
                    return;
                }
                if (confirmClickListener != null) {
                    confirmClickListener.onConfirm("", mEdit.getText().toString());
                }
                dismiss();
                break;
        }
    }

    public void setOnConfirmListener(IConfirmListener confirmClickListener) {
        this.confirmClickListener = confirmClickListener;
    }

}

