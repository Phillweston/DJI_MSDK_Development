package com.ew.autofly.widgets.dji;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.Keep;
import androidx.annotation.MainThread;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ew.autofly.R;
import com.ew.autofly.model.AircraftManager;

import dji.common.bus.LogicEventBus;
import dji.common.bus.UXSDKEventBus;
import dji.common.product.Model;
import dji.internal.logics.FPVTipLogic;
import dji.internal.logics.LogicManager;
import dji.internal.logics.Message;
import dji.log.DJILog;
import dji.thirdparty.rx.android.schedulers.AndroidSchedulers;
import dji.thirdparty.rx.functions.Action1;
import dji.thirdparty.rx.subscriptions.CompositeSubscription;
import dji.ux.base.MarqueeTextView;
import dji.ux.internal.Events;
import dji.ux.widget.PreFlightStatusWidget;


public class DjiFlightStatusWidget extends FrameLayout {

    protected CompositeSubscription subscription = new CompositeSubscription();

    private MarqueeTextView statusText;
    private ImageView statusBackground;
    private PreFlightStatusWidget.StatusType backGroundType;

    public DjiFlightStatusWidget(Context var1) {
        this(var1, (AttributeSet) null, 0);
    }

    public DjiFlightStatusWidget(Context var1, AttributeSet var2) {
        this(var1, var2, 0);
    }

    public DjiFlightStatusWidget(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
        initView(var1, var2, var3);
    }


    public void initView(Context var1, AttributeSet var2, int var3) {
        LayoutInflater.from(var1).inflate(R.layout.layout_widget_preflight_status, this, true);
        this.statusText = (MarqueeTextView) this.findViewById(dji.ux.R.id.textview_preflight_status);
        this.statusText.setDelay(0);
        this.statusBackground = (ImageView) this.findViewById(dji.ux.R.id.imageview_preflight_color_indicator);
        this.statusBackground.setVisibility(GONE);
        this.setOnClickListener(new OnClickListener() {
            public void onClick(View var1) {
                UXSDKEventBus.getInstance().post(new Events.PreFlightCheckListControlEvent());
            }
        });
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.isInEditMode()) {
            LogicManager.getInstance().startFPVTipLogic();
            this.subscription.add(LogicEventBus.getInstance().register(FPVTipLogic.FPVTipEvent.class).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<FPVTipLogic.FPVTipEvent>() {
                public void call(FPVTipLogic.FPVTipEvent var1) {
                    Message var2 = var1.getMessage();
                    updateView(var2);
                }
            }, new Action1<Throwable>() {
                public void call(Throwable var1) {
                    DJILog.e("BaseFrameLayout", new Object[]{var1});
                }
            }));
        }
    }

    protected void onDetachedFromWindow() {
        LogicManager.getInstance().stopFPVTipLogic();
        super.onDetachedFromWindow();
    }

    @MainThread
    @Keep
    public void onStatusChange(String var1, PreFlightStatusWidget.StatusType var2, boolean var3) {
        if (this.backGroundType != var2) {
            this.backGroundType = var2;
            if (var2 == PreFlightStatusWidget.StatusType.GOOD) {
                this.statusText.setTextColor(Color.GREEN);
              
            } else if (var2 == PreFlightStatusWidget.StatusType.WARNING) {
                this.statusText.setTextColor(Color.YELLOW);
              
            } else if (var2 == PreFlightStatusWidget.StatusType.ERROR) {
                this.statusText.setTextColor(Color.RED);
              
            } else {
              
                this.statusText.setTextColor(Color.WHITE);

            }
        }

        this.statusText.setText(new StringBuilder().append(getModelName(var2)).append(var1).toString());
    }

    private PreFlightStatusWidget.StatusType convertFromMessageType(Message.Type var1) {
        if (var1.getValue() == Message.Type.OFFLINE.getValue()) {
            return PreFlightStatusWidget.StatusType.OFFLINE;
        } else if (var1.getValue() == Message.Type.GOOD.getValue()) {
            return PreFlightStatusWidget.StatusType.GOOD;
        } else {
            return var1.getValue() == Message.Type.WARNING.getValue() ? PreFlightStatusWidget.StatusType.WARNING : PreFlightStatusWidget.StatusType.ERROR;
        }
    }

    @MainThread
    private void updateView(Message var1) {
        if (var1 != null) {
            PreFlightStatusWidget.StatusType var2 = this.convertFromMessageType(var1.getType());
            boolean var3 = var1.shouldBlink();
            String var4 = var1.getTitle();
            this.onStatusChange(var4, var2, var3);
        }
    }

    private String getModelName(PreFlightStatusWidget.StatusType statusType) {
        if (statusType != PreFlightStatusWidget.StatusType.OFFLINE) {
            Model model = AircraftManager.getModel();
            if (model != null && model != Model.UNKNOWN_AIRCRAFT) {
                String displayName = model.getDisplayName();
                if (!TextUtils.isEmpty(displayName)) {
                    return displayName + " ";
                }
            }
        }

        return "";
    }

}

