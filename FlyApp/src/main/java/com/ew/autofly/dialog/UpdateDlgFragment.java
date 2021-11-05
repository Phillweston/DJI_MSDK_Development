package com.ew.autofly.dialog;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.ew.autofly.R;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.interfaces.OnUpdateDialogClickListener;

public class UpdateDlgFragment extends BaseDialogFragment implements OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_update, container, false);
        bindField(view);
        return view;
    }

    private void bindField(View view) {
        if (null != getArguments() && getArguments().getBoolean("force"))
            view.findViewById(R.id.tv_cancel).setVisibility(View.GONE);
        view.findViewById(R.id.tv_cancel).setOnClickListener(this);
        view.findViewById(R.id.tv_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        OnUpdateDialogClickListener act = (OnUpdateDialogClickListener) getActivity().getSupportFragmentManager().findFragmentByTag("sys");
        switch (v.getId()) {
            case R.id.tv_ok:
                act.onUpdateConfirm();
                dismiss();
                break;
            case R.id.tv_cancel:
                act.onUpdateCancel();
                dismiss();
                break;
        }
    }
}