package com.ew.autofly.utils

//import com.amap.api.maps.model.LatLng
import com.amap.api.maps.AMapException
import com.amap.api.maps.model.LatLng
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyAreaPoint
import kotlin.math.PI
import kotlin.math.cos

//计算航向角
object Bearing {

    //https://blog.csdn.net/u010175314/article/details/76093934
    // 计算方位角,正北向为0度，以顺时针方向递增
    fun computeAzimuth(la1: FlyAreaPoint, la2: FlyAreaPoint): Double {
        var lat1 = la1.latitude
        var lon1 = la1.longitude
        var lat2 = la2.latitude
        var lon2 = la2.longitude
        var result = 0.0
        val ilat1 = (0.50 + lat1 * 360000.0).toInt()
        val ilat2 = (0.50 + lat2 * 360000.0).toInt()
        val ilon1 = (0.50 + lon1 * 360000.0).toInt()
        val ilon2 = (0.50 + lon2 * 360000.0).toInt()
        lat1 = Math.toRadians(lat1)
        lon1 = Math.toRadians(lon1)
        lat2 = Math.toRadians(lat2)
        lon2 = Math.toRadians(lon2)
        if (ilat1 == ilat2 && ilon1 == ilon2) {
            return result
        } else if (ilon1 == ilon2) {
            if (ilat1 > ilat2) result = 180.0
        } else {
            val c = Math
                    .acos(Math.sin(lat2) * Math.sin(lat1) + (Math.cos(lat2)
                            * Math.cos(lat1) * Math.cos(lon2 - lon1)))
            val A = Math.asin(Math.cos(lat2) * Math.sin(lon2 - lon1)
                    / Math.sin(c))
            result = Math.toDegrees(A)
            if (ilat2 > ilat1 && ilon2 > ilon1) {
            } else if (ilat2 < ilat1 && ilon2 < ilon1) {
                result = 180.0 - result
            } else if (ilat2 < ilat1 && ilon2 > ilon1) {
                result = 180.0 - result
            } else if (ilat2 > ilat1 && ilon2 < ilon1) {
                result += 360.0
            }
        }

        if (result < 0) {
            result += 180.0
        } else {
            result -= 180.0
        }
        return result
    }

    fun calculateLineDistance(var0: FlyAreaPoint?, var1: FlyAreaPoint?): Float {
        return if (var0 != null && var1 != null) {
            try {
                var var2 = var0.longitude
                var var4 = var0.latitude
                var var6 = var1.longitude
                var var8 = var1.latitude
                var2 *= 0.01745329251994329
                var4 *= 0.01745329251994329
                var6 *= 0.01745329251994329
                var8 *= 0.01745329251994329
                val var10 = Math.sin(var2)
                val var12 = Math.sin(var4)
                val var14 = Math.cos(var2)
                val var16 = Math.cos(var4)
                val var18 = Math.sin(var6)
                val var20 = Math.sin(var8)
                val var22 = Math.cos(var6)
                val var24 = Math.cos(var8)
                val var26 = DoubleArray(3)
                val var27 = DoubleArray(3)
                var26[0] = var16 * var14
                var26[1] = var16 * var10
                var26[2] = var12
                var27[0] = var24 * var22
                var27[1] = var24 * var18
                var27[2] = var20
                val var28 = Math.sqrt((var26[0] - var27[0]) * (var26[0] - var27[0]) + (var26[1] - var27[1]) * (var26[1] - var27[1]) + (var26[2] - var27[2]) * (var26[2] - var27[2]))
                (Math.asin(var28 / 2.0) * 1.27420015798544E7).toFloat()
            } catch (var30: Throwable) {
                var30.printStackTrace()
                0.0f
            }
        } else {
            try {
                throw AMapException("非法坐标值")
            } catch (var31: AMapException) {
                var31.printStackTrace()
                0.0f
            }
        }
    }

    fun calculateArea(var0: FlyAreaPoint?, var1: FlyAreaPoint?): Float {
        return if (var0 != null && var1 != null) {
            try {
                val var2 = Math.sin(var0.latitude * 3.141592653589793 / 180.0) - Math.sin(var1.latitude * 3.141592653589793 / 180.0)
                var var4 = (var1.longitude - var0.longitude) / 360.0
                if (var4 < 0.0) {
                    ++var4
                }
                (2.5560394669790553E14 * var2 * var4).toFloat()
            } catch (var6: Throwable) {
                var6.printStackTrace()
                0.0f
            }
        } else {
            try {
                throw AMapException("非法坐标值")
            } catch (var7: AMapException) {
                var7.printStackTrace()
                0.0f
            }
        }
    }

    fun calculateArea(var0: List<FlyAreaPoint>?): Float {
        val var1: Byte = 3
        return if (var0 != null && var0.size >= var1) {
            var var2 = 0.0
            val var4 = 111319.49079327357
            val var6 = var0.size
            for (var7 in 0 until var6) {
                val var8 = var0[var7]
                val var9 = var0[(var7 + 1) % var6]
                val var10 = var8.longitude * var4 * Math.cos(var8.latitude * 0.017453292519943295)
                val var12 = var8.latitude * var4
                val var14 = var9.longitude * var4 * Math.cos(var9.latitude * 0.017453292519943295)
                val var16 = var9.latitude * var4
                var2 += var10 * var16 - var14 * var12
            }
            Math.abs(var2 / 2.0).toFloat()
        } else {
            0.0f
        }
    }
}