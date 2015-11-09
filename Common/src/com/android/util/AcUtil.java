package com.android.util;

import java.util.ArrayList;
import java.util.List;


import android.os.Process;

import com.android.base.BaseInterface;

public class AcUtil {
    public static List<BaseInterface> list = new ArrayList<BaseInterface>();

    public static void add(BaseInterface ac) {
        list.add(ac);
    }

    public static void remove(BaseInterface ac) {
        list.remove(ac);
    }

    public static void finish() {
        for (BaseInterface ac : list) {
            ac.finish();
        }
        Process.killProcess(Process.myPid());
    }

}
