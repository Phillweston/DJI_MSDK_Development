package com.ew.autofly.mode.linepatrol.widget.dialog

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.ew.autofly.R
import com.ew.autofly.constant.AppConstant
import com.ew.autofly.dialog.common.AltitudeSettingDlgFragment
import com.ew.autofly.entity.AirRouteParameter
import com.ew.autofly.entity.WayPointTask
import com.ew.autofly.interfaces.OnSetAltitudeListener
import com.ew.autofly.interfaces.OnSettingDialogClickListener
import com.ew.autofly.mode.linepatrol.point.ui.base.BaseFragment
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyTask
import com.ew.autofly.widgets.CustomBar
import com.ew.autofly.widgets.CustomSeekbar.BubbleSeekBar.OnProgressChangedListener
import java.text.DecimalFormat
import java.util.*

class LinePatrolSettingFragment : BaseFragment(), View.OnClickListener, OnSetAltitudeListener {

    private var rgActionMode: RadioGroup? = null
    private var rgReturnMode: RadioGroup? = null

    //分辨率
    private var tvResolution: TextView? = null

    //固定航高
    private var chbAltitude: CheckBox? = null

    //航线高度值
    private var llAltitude: LinearLayout? = null
    private var cbAltitude: CustomBar? = null
    private var tvAltitude: TextView? = null

    //云台角度值
    private var cbGimbal: CustomBar? = null
    private var tvGimbalAngle: TextView? = null
    private var llFlySpeed: LinearLayout? = null

    //飞行速度值
    private var cbFlySpeed: CustomBar? = null
    private var tvFlySpeed: TextView? = null
    private var tvSpeed: TextView? = null

    //飞行转向
    private var tvPlaneYaw: TextView? = null
    private var cbPlaneYaw: CustomBar? = null

    //航向重叠值
    private var llForwardOverlap: LinearLayout? = null
    private var tvForwardOverlap: TextView? = null
    private var cbForwardOverLap: CustomBar? = null

    //进入测区航高
    private var tvEntryHeight: TextView? = null
    private var cbEntryHeight: CustomBar? = null
    private var chbEntryHeight: CheckBox? = null

    //起飞录制
    private var chbRecord: CheckBox? = null
    private var airRouteParameter: AirRouteParameter? = null
    private var actionMode = AppConstant.ACTION_MODE_VIDEO //0:视频拍摄    1.定时拍照
    private var returnMode = AppConstant.RETURN_MODE_STRAIGHT //0:直线返航    1.原路返回
    private var recordMode = AppConstant.MIDWAY_RECODE_VIDEO //0.2/3开始录制   1.起飞录制
    private val onRadioChangeListener = RadioGroup.OnCheckedChangeListener { group, checkedId ->
        when (checkedId) {
            R.id.rb_video_shot -> {
                actionMode = AppConstant.ACTION_MODE_VIDEO
                llFlySpeed!!.visibility = View.VISIBLE
                tvSpeed!!.visibility = View.GONE
                llForwardOverlap!!.visibility = View.GONE
                chbRecord!!.visibility = View.VISIBLE
                saveTask()
            }
            R.id.rb_schedule_shot -> {
                llFlySpeed!!.visibility = View.GONE
                tvSpeed!!.visibility = View.VISIBLE
                llForwardOverlap!!.visibility = View.VISIBLE
                actionMode = AppConstant.ACTION_MODE_PHOTO
                chbRecord!!.visibility = View.GONE
                saveTask()
            }
            R.id.rb_straight_back -> {
                returnMode = AppConstant.RETURN_MODE_STRAIGHT
                saveTask()
            }
            R.id.rb_original_back -> {
                returnMode = AppConstant.RETURN_MODE_ORIGIN
                saveTask()
            }
        }
    }
    private val onCheckedChangeListener =
        CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            when (buttonView.id) {
                R.id.cb_altitude -> if (isChecked) {
                    val altitudeSetDlgFrag = AltitudeSettingDlgFragment()
                    val bundle = Bundle()
                    bundle.putSerializable(
                        "routeInfoList",
                        arguments!!.getSerializable("routeInfoList")
                    )
                    bundle.putSerializable(
                        "aircraftLocation",
                        arguments!!.getSerializable("aircraftLocation")
                    )
                    bundle.putSerializable("airRoutePara", airRouteParameter)
                    altitudeSetDlgFrag.arguments = bundle
                    altitudeSetDlgFrag.show(fragmentManager!!, "line_altitude_setting")
                    llAltitude!!.visibility = View.GONE
                    saveTask()
                } else {
                    llAltitude!!.visibility = View.VISIBLE
                    if (cbEntryHeight != null && cbAltitude != null) {
                        val altitude = tvAltitude!!.text.toString().replace("m", "").toInt()
                        cbEntryHeight!!.bubbleSeekBar.configBuilder.min(altitude.toFloat())
                            .hideBubble().sectionCount(
                                500 - altitude
                            ).build()
                        cbAltitude!!.bubbleSeekBar.configBuilder.min(AppConstant.MIN_ALTITUDE.toFloat())
                            .max((AppConstant.MAX_ALTITUDE - 5).toFloat()).progress(
                                airRouteParameter!!.altitude.toFloat()
                            ).build()
                    }
                    saveTask()
                }
                R.id.cb_record -> {
                    recordMode =
                        if (isChecked) AppConstant.START_RECODE_VIDEO else AppConstant.MIDWAY_RECODE_VIDEO
                    saveTask()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val style = DialogFragment.STYLE_NO_TITLE
        val theme = 0
        //        setStyle(style, theme);
        airRouteParameter = arguments!!.getSerializable("params") as AirRouteParameter
        actionMode = arguments!!.getInt("actionMode")
        returnMode = arguments!!.getInt("returnMode")
        recordMode = arguments!!.getInt("recordMode")
    }

    private fun initView(view: View?) {
        rgActionMode = view!!.findViewById<View>(R.id.rg_action_mode) as RadioGroup
        rgReturnMode = view.findViewById<View>(R.id.rg_return_mode) as RadioGroup
        tvResolution = view.findViewById<View>(R.id.tv_resolution) as TextView
        tvSpeed = view.findViewById<View>(R.id.tv_speed) as TextView
        chbEntryHeight = view.findViewById<View>(R.id.cb_entry_height) as CheckBox
        chbAltitude = view.findViewById<View>(R.id.cb_altitude) as CheckBox
        chbRecord = view.findViewById<View>(R.id.cb_record) as CheckBox
        cbEntryHeight = view.findViewById<View>(R.id.sb_entry_height) as CustomBar
        tvEntryHeight = view.findViewById<View>(R.id.tv_entry_height) as TextView
        llFlySpeed = view.findViewById<View>(R.id.ll_fly_speed) as LinearLayout
        cbFlySpeed = view.findViewById<View>(R.id.sb_fly_speed) as CustomBar
        tvFlySpeed = view.findViewById<View>(R.id.tv_fly_speed) as TextView
        llAltitude = view.findViewById<View>(R.id.ll_altitude) as LinearLayout
        cbAltitude = view.findViewById<View>(R.id.sb_altitude) as CustomBar
        tvAltitude = view.findViewById<View>(R.id.tv_altitude) as TextView
        llForwardOverlap = view.findViewById<View>(R.id.ll_forward_overlap) as LinearLayout
        tvForwardOverlap = view.findViewById<View>(R.id.tv_forward_overlap) as TextView
        cbForwardOverLap = view.findViewById<View>(R.id.sb_forward_overlap) as CustomBar
        cbGimbal = view.findViewById<View>(R.id.sb_gimbal_angle) as CustomBar
        tvGimbalAngle = view.findViewById<View>(R.id.tv_gimbal_angle) as TextView
        cbPlaneYaw = view.findViewById<View>(R.id.sb_plane_yaw) as CustomBar
        tvPlaneYaw = view.findViewById<View>(R.id.tv_plane_yaw) as TextView
        if (arguments!!.getSerializable("routeInfoList") == null) {
            chbAltitude!!.visibility = View.GONE
        }
    }

    private fun initData() {
        rgActionMode!!.check(if (actionMode == AppConstant.ACTION_MODE_VIDEO) R.id.rb_video_shot else R.id.rb_schedule_shot)
        rgReturnMode!!.check(if (returnMode == AppConstant.RETURN_MODE_STRAIGHT) R.id.rb_straight_back else R.id.rb_original_back)
        chbRecord!!.isChecked = recordMode == AppConstant.START_RECODE_VIDEO
        chbAltitude!!.isChecked = !airRouteParameter!!.isFixedAltitude
        if (!airRouteParameter!!.isFixedAltitude) {
            llAltitude!!.visibility = View.GONE
        }
        if (actionMode == AppConstant.ACTION_MODE_VIDEO) {
            llFlySpeed!!.visibility = View.VISIBLE
            llForwardOverlap!!.visibility = View.GONE
            chbRecord!!.visibility = View.VISIBLE
        } else {
            tvSpeed!!.visibility = View.VISIBLE
            llFlySpeed!!.visibility = View.GONE
            llForwardOverlap!!.visibility = View.VISIBLE
            chbRecord!!.visibility = View.GONE
        }
        tvResolution!!.text = DecimalFormat("#0.00")
            .format(airRouteParameter!!.resolutionRateByAltitude) + "cm"
        tvForwardOverlap!!.text =
            String.format("%.0f", airRouteParameter!!.routeOverlap * 100) + "%"
        tvSpeed!!.text = "速度：" + airRouteParameter!!.actualSpeed + "m/s"
        tvAltitude!!.text = airRouteParameter!!.altitude.toString() + "m"
        tvGimbalAngle!!.text = (airRouteParameter!!.gimbalAngle + 90).toString() + "°"
        tvFlySpeed!!.setText(airRouteParameter!!.flySpeed.toInt().toString() + "m/s")
        tvForwardOverlap!!.text =
            String.format("%.0f", airRouteParameter!!.routeOverlap * 100) + "%"
        tvEntryHeight!!.text = airRouteParameter!!.entryHeight.toString() + "m"
        chbEntryHeight!!.isChecked = airRouteParameter!!.isCheckEntryHeight
    }

    private fun bindField(view: View?) {
        rgActionMode!!.setOnCheckedChangeListener(onRadioChangeListener)
        rgReturnMode!!.setOnCheckedChangeListener(onRadioChangeListener)
        chbRecord!!.setOnCheckedChangeListener(onCheckedChangeListener)
        view?.findViewById<View>(R.id.tv_cancel)?.setOnClickListener(this)
        view?.findViewById<View>(R.id.tv_ok)?.setOnClickListener(this)
        chbAltitude!!.setOnCheckedChangeListener(onCheckedChangeListener)
        initAltitude()
        initGimbal()
        initFlySpeed()
        initPlaneYaw()
        initOverlap()
        initEntryHeightBar()
    }

    private fun initEntryHeightBar() {
        val max = AppConstant.MAX_ALTITUDE
        val min = tvAltitude!!.text.toString().replace("m", "").toInt()
        cbEntryHeight!!.bubbleSeekBar.configBuilder
            .max(max.toFloat())
            .min(min.toFloat())
            .progress(airRouteParameter!!.entryHeight.toFloat())
            .sectionCount(max - min)
            .hideBubble()
            .build()
        cbEntryHeight!!.bubbleSeekBar.onProgressChangedListener =
            object : OnProgressChangedListener {
                override fun onProgressChanged(progress: Int, progressFloat: Float) {
                    tvEntryHeight!!.text = progress.toString() + "m"
                    airRouteParameter?.altitude = progress
                }

                override fun getProgressOnActionUp(progress: Int, progressFloat: Float) {}
                override fun getProgressOnFinally(progress: Int, progressFloat: Float) {
                    saveTask()
                }
            }
    }

    private fun initOverlap() {
        val max = 18
        val min = 3
        val progress = (airRouteParameter!!.routeOverlap * 100).toInt() / 5
        cbForwardOverLap!!.bubbleSeekBar.configBuilder
            .max(max.toFloat())
            .min(min.toFloat())
            .progress(progress.toFloat())
            .sectionCount(max - min)
            .hideBubble()
            .build()
        cbForwardOverLap!!.bubbleSeekBar.onProgressChangedListener =
            object : OnProgressChangedListener {
                override fun onProgressChanged(progress: Int, progressFloat: Float) {
                    tvForwardOverlap?.setText((progress * 5).toString() + "%")
                    var iSpeed = java.lang.Double.valueOf(
                        (tvAltitude!!.text.toString().replace("m", "")
                            .toInt() - airRouteParameter!!.baseLineHeight) * airRouteParameter!!.sensorHeight / airRouteParameter!!.focalLength * (1.0 - progress * 5 / 100.0) / 2.0
                    ).toInt()
                    if (iSpeed > 15) iSpeed = 15
                    if (iSpeed < 3) iSpeed = 3
                    tvSpeed!!.text = "速度：" + iSpeed + "m/s"
                }

                override fun getProgressOnActionUp(progress: Int, progressFloat: Float) {}
                override fun getProgressOnFinally(progress: Int, progressFloat: Float) {
                    saveTask()
                }
            }
    }

    private fun initAltitude() {
        val max = AppConstant.MAX_ALTITUDE - 5
        val min = AppConstant.MIN_ALTITUDE
        val progress = airRouteParameter!!.altitude
        cbAltitude!!.bubbleSeekBar.configBuilder
            .max(max.toFloat())
            .min(min.toFloat())
            .progress(progress.toFloat())
            .sectionCount(max - min)
            .hideBubble()
            .build()
        cbAltitude!!.bubbleSeekBar.onProgressChangedListener = object : OnProgressChangedListener {
            override fun onProgressChanged(progress: Int, progressFloat: Float) {
                tvAltitude!!.text = progress.toString() + "m"
                val Resolution = java.lang.Double.valueOf(
                    String.format(
                        "%.4f",
                        *arrayOf<Any>(
                            java.lang.Double.valueOf(
                                (tvAltitude!!.text.toString().replace("m", "")
                                    .toInt() - airRouteParameter!!.baseLineHeight).toDouble() * airRouteParameter!!.pixelSizeWidth / airRouteParameter!!.focalLength / 1000.0
                            )
                        )
                    )
                ).toDouble()
                tvResolution!!.text = DecimalFormat("#0.00").format(Resolution * 100) + "cm"
                cbEntryHeight!!.bubbleSeekBar.configBuilder.min(if (progress < 0f) 0f else progress.toFloat())
                    .hideBubble().sectionCount(
                        (cbEntryHeight!!.bubbleSeekBar.max - progress).toInt()
                    ).build()
                var iSpeed = java.lang.Double.valueOf(
                    (tvAltitude!!.text.toString().replace("m", "")
                        .toInt() - airRouteParameter!!.baseLineHeight) * airRouteParameter!!.sensorHeight / airRouteParameter!!.focalLength * (1.0 - tvForwardOverlap!!.text.toString()
                        .replace("%", "").toInt() / 100.0) / 2.0
                ).toInt()
                if (iSpeed > 15) iSpeed = 15
                if (iSpeed < 3) iSpeed = 3
                tvSpeed!!.text = "速度：" + iSpeed + "m/s"
                airRouteParameter?.altitude = progress
            }

            override fun getProgressOnActionUp(progress: Int, progressFloat: Float) {

            }
            override fun getProgressOnFinally(progress: Int, progressFloat: Float) {
                saveTask()
            }
        }
    }

    private fun initGimbal() {
        val max = 18
        val min = 0
        val progress = (airRouteParameter!!.gimbalAngle + 90) / 5
        cbGimbal!!.bubbleSeekBar.configBuilder
            .max(max.toFloat())
            .min(min.toFloat())
            .progress(progress.toFloat())
            .hideBubble()
            .sectionCount(max - min)
            .build()
        cbGimbal!!.bubbleSeekBar.onProgressChangedListener = object : OnProgressChangedListener {
            override fun onProgressChanged(progress: Int, progressFloat: Float) {
                tvGimbalAngle!!.text = (progress * 5).toString() + "°"
            }

            override fun getProgressOnActionUp(progress: Int, progressFloat: Float) {

            }

            override fun getProgressOnFinally(progress: Int, progressFloat: Float) {

            }

        }
    }

    private fun initFlySpeed() {
        val max = 15
        val min = 3
        val progress = airRouteParameter!!.flySpeed.toInt()
        cbFlySpeed!!.bubbleSeekBar.configBuilder
            .max(max.toFloat())
            .min(min.toFloat())
            .progress(progress.toFloat())
            .hideBubble()
            .sectionCount(max - min)
            .build()
        cbFlySpeed!!.bubbleSeekBar.onProgressChangedListener = object : OnProgressChangedListener {
            override fun onProgressChanged(progress: Int, progressFloat: Float) {
                tvFlySpeed!!.text = progress.toString() + "m/s"
            }

            override fun getProgressOnActionUp(progress: Int, progressFloat: Float) {

            }

            override fun getProgressOnFinally(progress: Int, progressFloat: Float) {
                saveTask()
            }

        }
    }

    private fun initPlaneYaw() {
        val max = 4
        val min = 0
        val progress = airRouteParameter!!.planeYaw / 90
        tvPlaneYaw!!.text = airRouteParameter!!.planeYaw.toString() + "°"
        cbPlaneYaw!!.bubbleSeekBar.configBuilder
            .max(max.toFloat())
            .min(min.toFloat())
            .progress(progress.toFloat())
            .hideBubble()
            .sectionCount(max - min)
            .build()
        cbPlaneYaw!!.bubbleSeekBar.onProgressChangedListener = object : OnProgressChangedListener {
            override fun onProgressChanged(progress: Int, progressFloat: Float) {
                tvPlaneYaw?.setText((progress * 90).toString() + "°")
            }

            override fun getProgressOnActionUp(progress: Int, progressFloat: Float) {

            }

            override fun getProgressOnFinally(progress: Int, progressFloat: Float) {
                saveTask()
            }
        }
        cbPlaneYaw!!.bubbleSeekBar.progress
    }

    lateinit var act:OnSettingDialogClickListener

    override fun onClick(v: View) {
//        val act: OnSettingDialogClickListener? =
//            if (tag == "line_patrol") activity!!.supportFragmentManager.findFragmentByTag("line") as OnSettingDialogClickListener?
//            else if (tag == "pointInfoBasicFragment") activity!!.supportFragmentManager.findFragmentByTag(
//                "pointInfoBasicFragment"
//            ) as OnSettingDialogClickListener?
//            else activity!!.supportFragmentManager.findFragmentByTag("river") as OnSettingDialogClickListener?
        when (v.id) {
            R.id.tv_ok -> {
                saveTask()
            }
            R.id.tv_cancel -> {
            }
        }
    }

    private fun saveTask(){
        airRouteParameter!!.altitude = tvAltitude!!.text.toString().replace("m", "").toInt()
        airRouteParameter!!.entryHeight =
            tvEntryHeight!!.text.toString().replace("m", "").toInt()
        airRouteParameter!!.routeOverlap =
            tvForwardOverlap!!.text.toString().replace("%", "").toDouble() / 100
        airRouteParameter!!.isFixedAltitude = !chbAltitude!!.isChecked
        airRouteParameter!!.planeYaw = cbPlaneYaw!!.bubbleSeekBar.progress * 90
        airRouteParameter!!.gimbalAngle =
            tvGimbalAngle!!.text.toString().replace("°", "").toInt() - 90
        if (actionMode == AppConstant.ACTION_MODE_VIDEO) {
            airRouteParameter!!.flySpeed =
                tvFlySpeed!!.text.toString().replace("m/s", "").toInt().toDouble()
        } else {
            airRouteParameter!!.flySpeed = airRouteParameter!!.actualSpeed.toDouble()
        }
        val hashMap = HashMap<String, Int>()
        hashMap["actionMode"] = actionMode
        hashMap["returnMode"] = returnMode
        hashMap["recordMode"] = recordMode
        act?.onSettingDialogConfirm(tag, hashMap, airRouteParameter)
    }

    override fun onSetAltitudeComplete(result: Boolean, taskList: List<WayPointTask>) {
        if (result) {
            val altitudeList =
                airRouteParameter!!.fixedAltitudeList.split(",".toRegex()).toTypedArray()
            var maxAltitude = AppConstant.MIN_ALTITUDE
            var minAltitude = AppConstant.MAX_ALTITUDE
            for (s in altitudeList) {
                if (maxAltitude < s.toInt()) {
                    maxAltitude = s.toInt()
                }
                if (minAltitude > s.toInt()) {
                    minAltitude = s.toInt()
                }
            }
            airRouteParameter!!.entryHeight = maxAltitude
            airRouteParameter!!.altitude = maxAltitude
            cbAltitude!!.bubbleSeekBar.configBuilder.max(maxAltitude.toFloat())
                .min(AppConstant.MIN_ALTITUDE.toFloat()).progress(maxAltitude.toFloat()).build()
            cbEntryHeight!!.bubbleSeekBar.configBuilder.max(AppConstant.MAX_ALTITUDE.toFloat())
                .min(maxAltitude.toFloat()).progress(maxAltitude.toFloat()).build()
        } else {
            chbAltitude!!.isChecked = false
            llAltitude!!.visibility = View.VISIBLE
        }
    }

    override var layoutId = R.layout.dialog_line_patrol_setting

    override fun initializeUI(savedInstanceState: Bundle?) {
        initView(view)
        initData()
        bindField(view)
    }

    override fun registerListeners() {}

    companion object {

        fun newInstance(arguments: Bundle?,act:OnSettingDialogClickListener): LinePatrolSettingFragment {
            val linePatrolSettingFragment = LinePatrolSettingFragment()
            linePatrolSettingFragment.act = act
            linePatrolSettingFragment.arguments = arguments
            return linePatrolSettingFragment
        }

    }

}