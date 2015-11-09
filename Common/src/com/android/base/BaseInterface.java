package com.android.base;

import android.view.View;

/**
 * Created by exception on 15/10/30.
 */
public interface BaseInterface extends View.OnClickListener ,TaskListener {
    void finish();

    void runOnUiThread(Runnable run);

    <T extends  View >  T getViewById(View view,int id);

    <T extends View > T  getViewById(int id);

    public void onNetWorkChange(int netWorkType);
}
