package com.ew.autofly.dialog;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.model.UserManager;
import com.ew.autofly.utils.ToastUtil;

import dji.common.error.DJIError;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.sdk.realname.AppActivationManager;
import dji.sdk.useraccount.UserAccountManager;



public class DJIAccountStatusDialogFragment extends BaseDialogFragment implements View.OnClickListener {
    private View view;
    private TextView tvActiveStatus, tvBindingStatus, tvLoginStatus;
    private TextView tvLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_dji_account_status, container, false);
        tvActiveStatus = (TextView) view.findViewById(R.id.tv_active_status);
        tvBindingStatus = (TextView) view.findViewById(R.id.tv_aircraft_binding_status);
        tvLoginStatus = (TextView) view.findViewById(R.id.tv_dji_account_login_status);
        tvLogin = (TextView) view.findViewById(R.id.tv_login);
        tvLogin.setOnClickListener(this);
        view.findViewById(R.id.ib_close).setOnClickListener(this);

        initData();
        return view;
    }

    private void initData() {
        switch (AppActivationManager.getInstance().getAppActivationState()) {
            case UNKNOWN:
                tvActiveStatus.setText("未知");
                break;
            case NOT_SUPPORTED:
                tvActiveStatus.setText("固件不支持激活");
                break;
            case LOGIN_REQUIRED:
                tvActiveStatus.setText("需登录DJI账号进行激活");
                tvLogin.setVisibility(View.VISIBLE);
                tvLogin.setText("登录");
                break;
            case ACTIVATED:
                tvActiveStatus.setTextColor(Color.WHITE);
                tvActiveStatus.setText("已激活");
                break;
        }

        switch (AppActivationManager.getInstance().getAircraftBindingState()) {
            case NOT_SUPPORTED:
                tvBindingStatus.setText("固件不支持绑定");
                break;
            case UNKNOWN:
                tvBindingStatus.setText("未知");
                break;
            case INITIAL:
                tvBindingStatus.setText("绑定状态待确定");
                break;
            case UNBOUND:
                tvBindingStatus.setText("未绑定");
                break;
            case NOT_REQUIRED:
                tvBindingStatus.setText("无需绑定");
                break;
            case UNBOUND_BUT_CANNOT_SYNC:
                tvBindingStatus.setText("需联网同步绑定状态");
                break;
            case BOUND:
                tvBindingStatus.setTextColor(Color.WHITE);
                tvBindingStatus.setText("已绑定");
                break;
        }

        switch (UserAccountManager.getInstance().getUserAccountState()) {
            case UNKNOWN:
                tvLoginStatus.setText("未知");
                break;
            case AUTHORIZED:
                tvLoginStatus.setTextColor(Color.WHITE);
                tvLoginStatus.setText("已登录");
                tvLogin.setText("退出");
                break;
          /*  case INVALID_TOKEN:
                tvLoginStatus.setText("认证令牌无效");
                break;*/
            case NOT_LOGGED_IN:
                tvLoginStatus.setText("未登录");
                tvLogin.setText("登录");
                tvLogin.setVisibility(View.VISIBLE);
                break;
            case NOT_AUTHORIZED:
                tvLoginStatus.setText("已登录但未解锁授权区域");
                tvLoginStatus.setTextColor(Color.WHITE);
                tvLogin.setVisibility(View.VISIBLE);
                tvLogin.setText("退出");
                break;
            case TOKEN_OUT_OF_DATE:
                tvLoginStatus.setText("认证令牌已过期");
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_close:
                dismiss();
                break;
            case R.id.tv_login:
                if (tvLogin.getText().equals("登录")) {
                    loginDJIAccount();
                } else {
                    logoutDJIAccount();
                }
                break;
        }
    }

    private void loginDJIAccount() {
        UserManager.getInstance().logIntoDJIUserAccount(getActivity(), new CommonCallbacks.CompletionCallbackWith<UserAccountState>() {
            @Override
            public void onSuccess(final UserAccountState state) {
                if (isAdded())
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initData();
                        }
                    });
            }

            @Override
            public void onFailure(DJIError djiError) {
                if (djiError != null && isAdded())
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.show(EWApplication.getInstance(), "登录失败！");
                        }
                    });
            }
        },true);
    }

    private void logoutDJIAccount() {
        UserManager.getInstance().logoutOfDJIUserAccount(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError == null && isAdded()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initData();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}