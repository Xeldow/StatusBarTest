package com.android.statusbartest;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @description:
 * @author: Xeldow
 * @date: 2019/8/1
 */
public final class Pause {

    /**
     * Context对象
     */
    private static Context CONTEXT_INSTANCE;

    /**
     * 取得Context对象
     * PS:必须在主线程调用
     *
     * @return Context
     */
    public static Context getContext() {
        if (CONTEXT_INSTANCE == null) {
            synchronized (Pause.class) {
                if (CONTEXT_INSTANCE == null) {
                    try {
                        Class<?> ActivityThread = Class.forName("android.app.ActivityThread");

                        Method method = ActivityThread.getMethod("currentActivityThread");
                        Object currentActivityThread = method.invoke(ActivityThread);//获取currentActivityThread 对象

                        Method method2 = currentActivityThread.getClass().getMethod("getApplication");
                        CONTEXT_INSTANCE = (Context) method2.invoke(currentActivityThread);//获取 Context对象

                        Method method3 = CONTEXT_INSTANCE.getClass().getMethod("onPause");
                        method3.invoke(CONTEXT_INSTANCE);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return CONTEXT_INSTANCE;
    }

    public class Solution {
        public ArrayList<Integer> printMatrix(int[][] matrix) {
            ArrayList<Integer> list = new ArrayList<>();
            if (matrix.length == 0) {
                return list;
            }
            int n = matrix.length;
            int rs = 0;
            int re = n - 1;
            int cs = 0;
            int ce = matrix[0].length - 1;
            boolean f1 = true;
            boolean f2 = true;
            for (int i = 0; i < n - 1; i++) {
                if (rs == re) {
                    f1 = false;
                }
                if (cs == ce) {
                    f2 = false;
                }
                if (!f1 && !f2) {
                    break;
                }
                for (int j = cs; j <= ce && f1; j++) {
                    list.add(matrix[rs][j]);
                    rs++;
                }
                for (int k = rs; k <= re && f2; k++) {
                    list.add(matrix[k][ce]);
                    ce--;
                }
                for (int l = ce; l >= cs && f1; l--) {
                    list.add(matrix[re][l]);
                    re--;
                }
                for (int p = re; p >= rs && f2; p--) {
                    list.add(matrix[p][cs]);
                    cs++;
                }
            }
            return list;
        }
    }
}
