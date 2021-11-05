package com.ew.autofly.utils;





public class MatrixTool {
    public static double[][] Init(int m, int n) {
        double[][] M = new double[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                M[i][j] = 0;
        return M;
    }

    public static double[][] Ident(int rank) {
        double[][] m = new double[rank][rank];
        for (int i = 0; i < rank; i++)
            for (int j = 0; j < rank; j++) {
                if (i == j)
                    m[i][j] = 1.0;
                else
                    m[i][j] = 0;
            }
        return m;
    }

    public static void MutliConst(double[][] m1, double c) {
        for (int i = 0; i < m1[0].length; i++)
            for (int j = 0; j < m1[1].length; j++) {
                m1[i][j] *= c;
            }
    }

    public static void Copy(double[][] m1, double[][] m2) {
        for (int i = 0; i < m1.length; i++)
            for (int j = 0; j < m1[0].length; j++) {
                m2[i][j] = m1[i][j];
            }
    }

    public static void CopySub(double[][] m1, int rowStart, int colStart, int rowNum, int colNum, double[][] m2, int rowStart2, int colStart2) {
        for (int i1 = rowStart, i2 = rowStart2; i1 < rowStart + rowNum; i1++, i2++)
            for (int j1 = colStart, j2 = colStart2; j1 < colStart + colNum; j1++, j2++) {
                m2[i2][j2] = m1[i1][j1];
            }
    }

    public static void Multi(double[][] m1, double[][] m2, double[][] mout) throws Exception {
        int m1x, m1y, m2x, m2y, moutx, mouty;

        m1x = m1.length;
        m1y = m1[0].length;

        m2x = m2.length;
        m2y = m2[0].length;

        moutx = mout.length;
        mouty = mout[0].length;

        if (m1y != m2x || m1x != moutx || m2y != mouty)
            throw new Exception("Multi 输入错误!");

        double[][] mtemp = new double[moutx][mouty];
        for (int i = 0; i < m1x; i++)
            for (int j = 0; j < m2y; j++) {
                mtemp[i][j] = 0;
                for (int k = 0; k < m1y; k++)
                    mtemp[i][j] += m1[i][k] * m2[k][j];
            }
        Copy(mtemp, mout);
    }

    public static void Add(double[][] m1, double[][] m2, double[][] mout) throws Exception {
        int m1x, m1y, m2x, m2y;

        m1x = m1.length;
        m1y = m1[0].length;

        m2x = m2.length;
        m2y = m2[0].length;

        if (m1x != m2x || m1y != m2y)
            throw new Exception("Matrix.Add 输入错误!");
        if (mout.length != m1x || mout[0].length != m2y)
            throw new Exception("Matrix.Add 输入错误!");

        for (int i = 0; i < m1x; i++)
            for (int j = 0; j < m2y; j++)
                mout[i][j] = m1[i][j] + m2[i][j];
    }

    public static void Sub(double[][] m1, double[][] m2, double[][] mout) throws Exception {
        int m1x, m1y, m2x, m2y;

        m1x = m1.length;
        m1y = m1[0].length;

        m2x = m2.length;
        m2y = m2[0].length;

        if (m1x != m2x || m1y != m2y)
            throw new Exception("Matrix.Sub 输入错误!");
        if (mout[0].length != m1x || mout[1].length != m2y)
            throw new Exception("Matrix.Sub 输入错误!");

        for (int i = 0; i < m1x; i++)
            for (int j = 0; j < m2y; j++)
                mout[i][j] = m1[i][j] - m2[i][j];
    }

    public static void AT(double[][] m1, double[][] mout) {
        for (int i = 0; i < m1[0].length; i++)
            for (int j = 0; j < m1[1].length; j++) {
                mout[j][i] = m1[i][j];
            }
    }

    public void ATBA(double[][] m1, double[][] m2, double[][] mout) throws Exception {
        int M, N;
        M = m1.length;
        N = m1[0].length;
        if (mout[0].length != mout[1].length || mout[0].length != M)
            throw new Exception("ATBA 输入错误!");
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                mout[i][j] = 0;
                for (int r = 0; r < M; r++)
                    for (int k = 0; k < M; k++)
                        mout[i][j] = mout[i][j] + m1[k][i] * m2[k][r] * m1[r][j];

            }
    }

    public void ABAT(double[][] m1, double[][] m2, double[][] mout) throws Exception {
        int M, N;
        M = m1[0].length;
        N = m1[1].length;
        if (mout[0].length != mout[1].length || mout[0].length != M)
            throw new Exception("ATBA 输入错误!");
        for (int i = 0; i < M; i++)
            for (int j = 0; j < M; j++) {
                mout[i][j] = 0;
                for (int r = 0; r < N; r++)
                    for (int k = 0; k < N; k++)
                        mout[i][j] = mout[i][j] + m1[i][k] * m2[k][r] * m1[j][r];
            }
    }

    public void Inv(double[][] c) {
        double temp = 0;
        int i, j, k, N = c[0].length;


        for (i = 1; i < N; i++)
            for (j = 0; j < i; j++)
                c[i][j] = 0;
        for (i = 0; i < N; i++) {
            for (j = i; j < N; j++) {
                temp = c[i][j];
                for (k = 0; k < i; k++)
                    temp = temp - c[k][i] * c[k][j] / c[k][k];
                if (j == i)
                    c[i][j] = 1 / temp;
                else
                    c[i][j] = temp * c[i][i];
            }
        }

        for (i = 0; i < N - 1; i++) {
            for (j = i + 1; j < N; j++) {
                temp = -c[i][j];
                for (k = i + 1; k < j; k++) {
                    temp = temp - c[i][k] * c[k][j];
                }
                c[i][j] = temp;
            }
        }

        for (i = 0; i < N - 1; i++) {
            for (j = i; j < N; j++) {
                if (j == i)
                    temp = c[i][j];
                else
                    temp = c[i][j] * c[j][j];
                for (k = j + 1; k < N; k++)
                    temp = temp + c[i][k] * c[j][k] * c[k][k];
                c[i][j] = temp;
            }
        }
        for (i = 1; i < N; i++)
            for (j = 0; j < i; j++)
                c[i][j] = c[j][i];
    }

    public void DEBUG_DUMP(double[][] m1) {
        int M, N;
        String buf;

        M = m1[0].length;
        N = m1[1].length;
        for (int i = 0; i < M; i++) {
            buf = "";
            for (int j = 0; j < N; j++) {
                buf += String.format("%0.6f", m1[i][j]);
                buf += ",";
            }
        }
    }

}