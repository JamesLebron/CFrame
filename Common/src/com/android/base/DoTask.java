package com.android.base;


import android.content.Context;

import java.lang.reflect.Method;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by exception on 15/10/30.
 */
public class DoTask {
    public boolean doTask = false;
    private Queue<BaseTask> taskQueue = new LinkedBlockingDeque<>();

    private void doTask() {
        doTask = true;
        while (!taskQueue.isEmpty()) {
            BaseTask task = taskQueue.poll();
            invoke(task);
        }
        doTask = false;
    }


    public void addTask(final BaseTask task) {
        if (task.needSyncTask) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    invoke(task);
                }
            }).start();
            return;
        }
        taskQueue.add(task);
        if (!doTask) {
            doTask();
        }
    }

    private synchronized void invoke(final BaseTask task) {
        try {
            if (task.listener != null)
                task.listener.onStart(task);
            Method method = task.invokeClass.getMethod(task.method,
                    task.getClass());
            final Object obj = method.invoke(task.invokeObj == null ? task.invokeClass.newInstance() : task.invokeObj, task);
            if (!(task instanceof HttpTask) && task.listener != null)
                task.listener.onSucess(task, obj);
        } catch (Exception e) {
            e.printStackTrace();
            if (task.listener != null)
                task.listener.onComplate(task, new Exception(e), null);
        }
    }
}
