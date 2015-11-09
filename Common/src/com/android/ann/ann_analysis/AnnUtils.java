package com.android.ann.ann_analysis;

import android.view.View;

import com.android.ann.click;
import com.android.ann.id;
import com.android.base.BaseActivity;

import java.lang.reflect.Field;

/**
 * Created by xudong on 2015/7/22.
 */
public class AnnUtils {
    /**
     * analysis id
     *
     * @param ac
     * @throws Exception
     */
    public static void doInit(BaseActivity ac)  {
        try {
            Field[] fields = ac.getClass().getDeclaredFields();
            if (fields == null)
                fields = ac.getClass().getFields();

            if (fields != null) {
                for (Field f : fields) {
                    id lid = f.getAnnotation(id.class);
                    if (lid != null) {
                        View v = ac.getViewById(lid.id());
                        if (v != null) {
                            f.setAccessible(true);
                            f.set(ac, v);
                            if (f.getAnnotation(click.class) != null)
                                v.setOnClickListener(ac);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
