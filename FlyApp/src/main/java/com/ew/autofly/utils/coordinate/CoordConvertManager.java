package com.ew.autofly.utils.coordinate;

import com.ew.autofly.utils.MatrixTool;

import java.math.BigDecimal;

/**
 * 坐标转换
 * Created by TonyChen on 2014/11/8.
 */
public class CoordConvertManager {




    protected ZoneType m_zoneType;



    protected CoordTrans7Param m_CoordTrans7Param;



    protected int m_centerLine = 0;







//







    protected int m_centerMove = 0;




    public void setZoneType(ZoneType type) {
        m_zoneType = type;
    }

    public ZoneType getZoneType() {
        return m_zoneType;
    }




    public boolean IsWithBandNo = true;

    public CoordConvertManager() {
        m_CoordTrans7Param = new CoordTrans7Param();
        m_CoordTrans7Param.setDx(0);
        m_CoordTrans7Param.setDy(0);
        m_CoordTrans7Param.setDz(0);
        m_CoordTrans7Param.setRx(0);
        m_CoordTrans7Param.setRy(0);
        m_CoordTrans7Param.setRz(0);
        m_CoordTrans7Param.setK(0);
    }

    public CoordConvertManager(ZoneType mZoneType, CoordTrans7Param mCoordTrans7Param) {
        this.m_zoneType = mZoneType;
        this.m_CoordTrans7Param = mCoordTrans7Param;
    }







    public synchronized double[] WGS84ToBJ54(double B, double L, double Z, int centerLine) throws Exception {
        double wgsa = 6378137;
        double wgsb = 6356752.3142;
        double bja = 6378245;
        double bjb = 6356863.0188;
        m_centerLine = centerLine;

        double wgsFirstE = (wgsa * wgsa - wgsb * wgsb) / (wgsa * wgsa);
        double v = wgsa / Math.sqrt(1 - wgsFirstE * Math.sin(B * Math.PI / 180) * Math.sin(B * Math.PI / 180));
        double wgsX = (v + Z) * Math.cos(B * Math.PI / 180) * Math.cos(L * Math.PI / 180);
        double wgsY = (v + Z) * Math.cos(B * Math.PI / 180) * Math.sin(L * Math.PI / 180);
        double wgsZ = (v * (1 - wgsFirstE) + Z) * Math.sin(B * Math.PI / 180);



        this.m_CoordTrans7Param.Set4Param(this.m_CoordTrans7Param.getDx(), this.m_CoordTrans7Param.getDy(), this.m_CoordTrans7Param.getDz(), this.m_CoordTrans7Param.getK());
        this.m_CoordTrans7Param.SetRotationParamMM(this.m_CoordTrans7Param.getRx(), this.m_CoordTrans7Param.getRy(), this.m_CoordTrans7Param.getRz());

        double[] xyz = this.m_CoordTrans7Param.TransCoord(wgsX, wgsY, wgsZ);
        double xt = xyz[0];
        double yt = xyz[1];
        double zt = xyz[2];

        double bjFirstE = (bja * bja - bjb * bjb) / (bja * bja);
        double bjSecondE = (bja * bja - bjb * bjb) / (bjb * bjb);
        double bjL = 180 + Math.atan(yt / xt) * 180 / Math.PI;
        double R = Math.sqrt(xt * xt + yt * yt);
        double RR = Math.sqrt(R * R + zt * zt);
        double u = Math.atan(bjb * zt * (1.0 + bjSecondE * bjb / RR) / bja / R);
        double b = Math.atan((zt + bjSecondE * bjb * (Math.sin(u)) * (Math.sin(u)) * (Math.sin(u)))
                / (R - bjFirstE * bja * (Math.cos(u)) * (Math.cos(u)) * (Math.cos(u))));
        double bjB = b * 180 / Math.PI;
        double bjZ = R * Math.cos(b) + zt * Math.sin(b) - bja * Math.sqrt(1 - (bjFirstE * Math.sin(b) * Math.sin(b)));


        int ProjNo = 0;
        double L0 = 0;
        double n = bja / Math.sqrt(1 - bjFirstE * Math.sin(bjB * Math.PI / 180) * Math.sin(bjB * Math.PI / 180));
        if (this.m_zoneType == ZoneType.Zone3) {
            double cc = bjL % 3;

            if (cc / 3 >= 0.5) {
                ProjNo = (int) (bjL / 3 + 1);
            } else {
                ProjNo = (int) (bjL / 3);
            }
            L0 = m_centerLine + this.m_centerMove;

        } else if (this.m_zoneType == ZoneType.Zone6) {
            double bb = bjL % 6;
            if (bb == 0) {
                ProjNo = (int) (bjL / 6);
            } else {
                ProjNo = (int) (bjL / 6 + 1);
            }
            L0 = m_centerLine + this.m_centerMove;
        }

        double B1 = bjB * Math.PI / 180;
        double e2 = bjFirstE;
        double ee = e2 * (1.0 - e2);
        double sinB = Math.sin(B1);
        double tanB = Math.tan(B1);
        double cosB = Math.cos(B1);
        double T = tanB * tanB;
        double C = ee * cosB * cosB;
        double A = (bjL - L0) * cosB * Math.PI / 180;
        double a0 = 1 - e2 / 4 - 3 * e2 * e2 / 64 - 5 * e2 * e2 * e2 / 256;
        double a2 = 3 * e2 / 8 + 3 * e2 * e2 / 32 + 45 * e2 * e2 * e2 / 1024;
        double a4 = 15 * e2 * e2 / 256 + 45 * e2 * e2 * e2 / 1024;
        double a6 = 35 * e2 * e2 * e2 / 3072;
        double M = bja * (a0 * B1 - a2 * Math.sin(2 * B1) + a4 * Math.sin(4 * B1) - a6 * Math.sin(6 * B1));
        double xval = n * (A + (1 - T + C) * A * A * A / 6 + (5 - 18 * T + T * T + 72 * C - 58 * ee) * A * A * A * A * A / 120);
        double yval = M + n * Math.tan(B1) * (A * A / 2 + (5 - T + 9 * C + 4 * C * C) * A * A * A * A / 24 + (61 - 58 * T + T * T + 600 * C - 330 * ee) * A * A * A * A * A * A / 720);

        double X0 = 500000;
        double Y0 = 0;

        double[] result = new double[3];
        if (this.IsWithBandNo) {
            result[1] = xval + X0 + ProjNo * 1000000;
        } else {
            result[1] = xval + X0;
        }
        result[0] = yval + Y0;
        result[2] = bjZ;
        return result;
    }

    public static class CoordTrans7Param {
        public double[][] values = new double[7][1];

        public CoordTrans7Param() {
            values[0][0] = 0;
            values[1][0] = 0;
            values[2][0] = 0;
            values[3][0] = 0;
            values[4][0] = 0;
            values[5][0] = 0;
            values[6][0] = 0;
        }

        public void Set4Param(double dx, double dy, double dz, double k) {
            setDx(dx);
            setDy(dy);
            setDz(dz);
            setK(k);
        }

        public void SetRotationParamRad(double rx, double ry, double rz) {
            setRx(rx);
            setRy(ry);
            setRz(rz);
        }

        public void SetRotationParamMM(double rx, double ry, double rz) {
            SetRotationParamRad(rx * Math.PI / 648000, ry * Math.PI / 648000, rz * Math.PI / 648000);
        }

        private double[][] GetMx() {
            double[][] Mx = new double[][]
                    {
                            {
                                    1, 0, 0
                            },
                            {
                                    0, Math.cos(getRx()), Math.sin(getRx())
                            },
                            {
                                    0, -Math.sin(getRx()), Math.cos(getRx())
                            }
                    };
            return Mx;
        }

        private double[][] GetMy() {
            double[][] My = new double[][]
                    {
                            {
                                    Math.cos(getRy()), 0, -Math.sin(getRy())
                            },
                            {
                                    0, 1, 0
                            },
                            {
                                    Math.sin(getRy()), 0, Math.cos(getRy())
                            }
                    };
            return My;
        }

        private double[][] GetMz() {
            double[][] Mz = new double[][]
                    {
                            {
                                    Math.cos(getRz()), Math.sin(getRz()), 0
                            },
                            {
                                    -Math.sin(getRz()), Math.cos(getRz()), 0
                            },
                            {
                                    0, 0, 1
                            }
                    };
            return Mz;
        }

        private double[][] GetM() throws Exception //M=Mx*My*Mz? or M=Mz*My*Mx?
        {
            double[][] M = new double[3][3];
            MatrixTool.Multi(GetMz(), GetMy(), M);
            MatrixTool.Multi(M, GetMx(), M);
            return M;
        }

        private double[][] GetMdx() throws Exception {
            double[][] mt = {{0, 0, 0},
                    {0, -Math.sin(getRx()), Math.cos(getRx())},
                    {0, -Math.cos(getRx()), -Math.sin(getRx())}};

            double[][] m = new double[3][3];

            MatrixTool.Multi(GetMz(), GetMy(), m);
            MatrixTool.Multi(m, mt, m);
            return m;
        }

        private double[][] GetMdy() throws Exception {
            double[][] mt = {{-Math.sin(getRy()), 0, -Math.cos(getRy())},
                    {0, 0, 0},
                    {Math.cos(getRy()), 0, -Math.sin(getRy())}};

            double[][] m = new double[3][3];

            MatrixTool.Multi(GetMz(), mt, m);
            MatrixTool.Multi(m, GetMx(), m);
            return m;
        }

        private double[][] GetMdz() throws Exception {
            double[][] mt = {{-Math.sin(getRz()), Math.cos(getRz()), 0},
                    {-Math.cos(getRz()), -Math.sin(getRz()), 0},
                    {0, 0, 0}};

            double[][] m = new double[3][3];

            MatrixTool.Multi(mt, GetMy(), m);
            MatrixTool.Multi(m, GetMx(), m);
            return m;
        }

        private double[][] specialMulti(double[][] m, double[][] X) throws Exception {
            int rowNumM = m[0].length;
            int colNumM = m[1].length;
            int rowNumX = X[0].length;
            int colNumX = X[1].length;
            int lines = rowNumX / colNumM;
            double[][] mt = MatrixTool.Init(rowNumM, colNumX);
            double[][] subX = MatrixTool.Init(colNumM, colNumX);
            double[][] res = MatrixTool.Init(rowNumM * lines, colNumX);

            for (int i = 0; i < lines; i++) {
                MatrixTool.CopySub(X, i * colNumM, 0, colNumM, colNumX, subX, 0, 0);
                MatrixTool.Multi(m, subX, mt);
                MatrixTool.CopySub(mt, 0, 0, rowNumM, colNumX, res, i * rowNumM, 0);
            }
            return res;
        }

        private double[][] specialSub(double[][] m, double[][] X) throws Exception {
            int rowNumM = m[0].length;
            int colNumM = m[1].length;
            int rowNumX = X[0].length;
            int colNumX = X[1].length;
            int lines = rowNumX / rowNumM;
            double[][] subX = MatrixTool.Init(rowNumM, colNumX);
            double[][] res = MatrixTool.Init(rowNumX, colNumX);

            for (int i = 0; i < rowNumX; i += rowNumM) {
                MatrixTool.CopySub(X, i, 0, rowNumM, colNumX, subX, 0, 0);
                MatrixTool.Sub(m, subX, subX);
                MatrixTool.CopySub(subX, 0, 0, rowNumM, colNumX, res, i, 0);
            }
            return res;
        }

        private double[][] GetF(double[][] X, double[][] Y) throws Exception {
            double[][] f0;
            double[][] qx = MatrixTool.Init(X[0].length, 1);
            double[][] K = {{-1 * getDx()}, {-1 * getDy()}, {-1 * getDz()}};
            double[][] S = {{1 + getK()}};

            MatrixTool.Multi(X, S, qx);
            double[][] M = GetM();
            qx = specialMulti(M, qx);
            MatrixTool.Sub(qx, Y, qx);
            f0 = specialSub(K, qx);
            return f0;
        }

        private double[][] GetB(double[][] X) throws Exception {
            int rowNum = X[0].length;
            double[][] B = MatrixTool.Init(rowNum, 7);
            double[][] M = GetM();
            double[][] Mdx = GetMdx();
            double[][] Mdy = GetMdy();
            double[][] Mdz = GetMdz();
            double[][] mi = MatrixTool.Ident(3);
            double[][] MX, MY, MZ, MK;

            MK = specialMulti(M, X);
            MX = specialMulti(Mdx, X);
            MY = specialMulti(Mdy, X);
            MZ = specialMulti(Mdz, X);

            for (int i = 0; i < rowNum; i += 3)
                MatrixTool.CopySub(mi, 0, 0, 3, 3, B, i, 0);
            MatrixTool.CopySub(MX, 0, 0, rowNum, 1, B, 0, 3);
            MatrixTool.CopySub(MY, 0, 0, rowNum, 1, B, 0, 4);
            MatrixTool.CopySub(MZ, 0, 0, rowNum, 1, B, 0, 5);
            MatrixTool.CopySub(MK, 0, 0, rowNum, 1, B, 0, 6);
            return B;
        }

        private double[][] GetA() throws Exception {
            double[][] M = GetM();
            double[][] I2 = MatrixTool.Ident(3);
            double[][] A = MatrixTool.Init(3, 6);

            MatrixTool.MutliConst(I2, -1);
            MatrixTool.MutliConst(M, (1 + getK()));
            MatrixTool.CopySub(M, 0, 0, 3, 3, A, 0, 0);
            MatrixTool.CopySub(I2, 0, 0, 3, 3, A, 0, 3);
            return A;
        }

        private double[][]

        GetV(double[][] X, double[][] Y, CoordTrans7Param dpp) throws Exception {
            int rowNum = X[0].length;

            double[][] B, F, A, B2, B3, F2, V;
            double[][] AT = MatrixTool.Init(6, 3);

            A = GetA();
            MatrixTool.AT(A, AT);
            MatrixTool.MutliConst(AT, 1 / (1 + (1 + getK()) * (1 + getK())));

            F = GetF(X, Y);
            B = GetB(X);
            B2 = MatrixTool.Init(3, 7);
            B3 = MatrixTool.Init(3, 1);
            F2 = MatrixTool.Init(rowNum, 1);
            for (int i = 0; i < rowNum / 3; i++) {
                MatrixTool.CopySub(B, i * 3, 0, 3, 7, B2, 0, 0);
                MatrixTool.Multi(B2, dpp.values, B3);
                MatrixTool.CopySub(B3, 0, 0, 3, 1, F2, i * 3, 0);
            }
            MatrixTool.Sub(F, F2, F2);
            V = specialMulti(AT, F2);
            return V;
        }










        private double[] TransCoord(double x1, double y1, double z1) throws Exception {
            double[][] Xi = {{x1}, {y1}, {z1}};
            double[][] DX = {{getDx()}, {getDy()}, {getDz()}};
            double[][] tY = new double[3][1];
            double[][] K = {{1 + getK()}};

            double[][] M = GetM();
            MatrixTool.Multi(Xi, K, tY);
            MatrixTool.Multi(M, tY, tY);
            MatrixTool.Add(tY, DX, tY);
            return new double[]{
                    tY[0][0], tY[1][0], tY[2][0]
            };
        }




        public void setDx(double value) {
            values[0][0] = value;
        }

        public double getDx() {
            return values[0][0];
        }




        public void setDy(double value) {
            values[1][0] = value;
        }

        public double getDy() {
            return values[1][0];
        }




        public void setDz(double value) {
            values[2][0] = value;
        }

        public double getDz() {
            return values[2][0];
        }




        public void setRx(double value) {
            values[3][0] = value;
        }

        public double getRx() {
            return values[3][0];
        }




        public void setRy(double value) {
            values[4][0] = value;
        }

        public double getRy() {
            return values[4][0];
        }




        public void setRz(double value) {
            values[5][0] = value;
        }

        public double getRz() {
            return values[5][0];
        }




        public void setK(double value) {
            values[6][0] = value;
        }

        public double getK() {
            return values[6][0];
        }
    }




    public enum ZoneType {



        Zone3,



        Zone6
    }




    public enum CoordSetSytem {



        WGS_84,



        BJ_54,



        XA_80,



        ZG_2000
    }


    public static String convertToSexagesimal(double num) {
        int du = (int) Math.floor(Math.abs(num));
        double temp = getdPoint(Math.abs(num)) * 60;
        int fen = (int) Math.floor(temp);
        double miao = getdPoint(temp) * 60;
        if (num < 0)
            return "-" + du + "°" + fen + "′" + String.format("%.2f", miao) + "″";

        return du + "°" + fen + "′" + String.format("%.2f", miao) + "″";

    }


    public static String convertToSexagesimalFenUnit(double num) {
        int du = (int) Math.floor(Math.abs(num));
        double temp = getdPoint(Math.abs(num)) * 60;
        int fen = (int) Math.floor(temp);
        if (num < 0)
            return "-" + du + "°" + fen + "′";

        return du + "°" + fen + "′";

    }


    public static double getdPoint(double num) {
        double d = num;
        int fInt = (int) d;
        BigDecimal b1 = new BigDecimal(Double.toString(d));
        BigDecimal b2 = new BigDecimal(Integer.toString(fInt));
        double dPoint = b1.subtract(b2).floatValue();
        return dPoint;
    }


    public static double convertToSimpleFormat(double du, double fen, double miao) {
        double result = du + fen/60 + miao/60/60;
        return result;
    }

}
