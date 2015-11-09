package com.android.base;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.android.common.R;

/**
 * Created by exception on 15/10/30.
 */
public class BaseActivity extends AdjustedRequestCodeActionBarActivity implements BaseInterface {
    protected BaseViewCache viewCache;//这个是存放所有的view控件的

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setContentView(int layoutResID) {
        View layout = getLayoutInflater().inflate(layoutResID, null);
        setContentView(layout);
    }

    @Override
    public void setContentView(View view) {
        viewCache = new BaseViewCache(view);
        super.setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        viewCache = new BaseViewCache(view);
        super.setContentView(view, params);
    }

    //TODO 如果是fragmentActivity  则可以不调用init 函数 , 但是要使用viewCache 则必须对viewCache进行初始化
    protected void init() {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onStart(BaseTask task) {

    }

    @Override
    public void onFail(BaseTask task, Exception e) {
        task.listener = null;
        if (task instanceof HttpTask) {
            ((HttpTask) task).setIsComplate(true);
        }
    }

    @Override
    public void onSucess(BaseTask task, Object retObj) {
        task.listener = null;
        if (task instanceof HttpTask) {
            ((HttpTask) task).setIsComplate(true);
        }
    }

    @Override
    public void onComplate(final BaseTask task, final Exception e, final Object retObj) {
        if (task.needSyncTask) {
            if (e != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        task.listener.onFail(task, e);
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        task.listener.onSucess(task, retObj);
                    }
                });
            }
        } else {
            if (e != null) {
                task.listener.onFail(task, e);
            } else {
                task.listener.onSucess(task, retObj);
            }
        }

    }


    public void setTitle(String title) {

    }

    public void setTitle(int resid) {

    }

    @Override
    public <T extends View> T getViewById(int id) {
        return ((T) findViewById(id));
    }

    @Override
    public void onNetWorkChange(int netWorkType) {

    }


    @Override
    public <T extends View> T getViewById(View view, int id) {
        return ((T) view.findViewById(id));
    }
}
