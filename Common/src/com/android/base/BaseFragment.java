package com.android.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by exception on 15/10/30.
 */
public class BaseFragment extends Fragment implements BaseInterface {
    protected BaseViewCache viewCache;//这个是存放所有的view控件的

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    protected void init() {
        viewCache = new BaseViewCache(getView());
    }


    @Override
    public void finish() {

    }

    @Override
    public void runOnUiThread(Runnable run) {
        getActivity().runOnUiThread(run);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onStart(BaseTask task) {

    }

    @Override
    public void onFail(BaseTask task, Exception e) {

    }

    @Override
    public void onSucess(BaseTask task, Object retObj) {

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

    @Override
    public <T extends View> T getViewById(int id) {
        return ((T) getView().findViewById(id));
    }

    @Override
    public void onNetWorkChange(int netWorkType) {

    }


    @Override
    public <T extends View> T getViewById(View view, int id) {
        return ((T) view.findViewById(id));
    }
}
