package com.android.base;

/**
 * Created by exception on 15/10/30.
 */
public interface TaskListener {
    void onStart(BaseTask task);

//    void onStop(BaseTask task);

    void onFail(BaseTask task,Exception e);

    void onSucess(BaseTask task,Object retObj);

    void onComplate(BaseTask task ,Exception e,Object retObj);
}
