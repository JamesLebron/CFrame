package com.android.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;


import com.android.util.Logger;

import java.util.List;

/**
 * Created by Gerald on 15/2/2.
 * <p/>
 * 参照： https://github.com/shamanland/nested-fragment-issue
 * 问题： 解决fragment嵌套fragment，内部的fragment通过startActivityForResult收不到的问题
 * 解决思路： 重写fragmentActivity的startActivityForResult和onActivityResult，在requestCode存放指定的层次的fragment(分位存放)
 * 针对Acitivty的存放：只有低7位有值
 * 低位的高9位，分3个层次存放每一层对应的fragment对应的index值。(每一层占3位，由父到子，对应从高到底)
 */
public class AdjustedRequestCodeActionBarActivity extends AppCompatActivity {

    private static final String tag = AdjustedRequestCodeActionBarActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (requestCode > 127) {
//            throw new IllegalArgumentException("requestCode不能大于127");
            return;
        }
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
        if (requestCode <= 0) {
            Logger.w(tag, "startActivityFromFragment: requestCode <= 0, 直接调用startActivity");
            startActivity(intent);
            return;
        }
        if (requestCode > 127) {
//            throw new IllegalArgumentException("requestCode不能大于127");
            return;
        }
        // 对每一代index都+1，在onActivityResult中得到的index小于0的话，则表示直接调用的startActivityForResult
        int head = 0;
        int i = 0;
        Fragment f = fragment;
        while (f != null) {
            if (i > 2) {
//                throw new IllegalStateException("Fragment嵌套不能多于3层");
                return;
            }
            int index = getIndexOfFragment(f);
            if (index >= 7 || index < 0) {
//                throw new IllegalStateException("每一层的Fragment数量不能多于7");
                return;
            }
            // 父fragment在高位，低fragment在低位
            head += (index + 1) << (i * 3);
            i++;
            f = f.getParentFragment();
        }
        while (i < 3) {
            i++;
            head <<= 3;
        }
        int adjustedCode = (head << 7) + (requestCode & 0x7f);
        super.startActivityForResult(intent, adjustedCode);
    }

    private int getIndexOfFragment(Fragment f) {
        List<Fragment> brothers;
        Fragment parent = f.getParentFragment();
        if (parent == null) {
            brothers = getSupportFragmentManager().getFragments();
        } else {
            brothers = parent.getChildFragmentManager().getFragments();
        }
        if (brothers == null) {
            return -1;
        }
        return brothers.indexOf(f);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode <= 0) {
            Logger.w(tag, "onActivityResult: requestCode <= 0, drop this result");
            return;
        }
        // 第一层（父fragment）,对应高位的
        int index1 = (requestCode >> 13) & 0x7;
        if (index1 < 1) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        int index2 = (requestCode >> 10) & 0x7;
        int index3 = (requestCode >> 7) & 0x7;
        List<Fragment> children = getSupportFragmentManager().getFragments();
        index1--;
        if (children == null || index1 >= children.size()) {
            Logger.e(tag, "onActivityResult: error for generation 1: no Fragment in " + children + " matches index: " + index1);
            return;
        }
        Fragment f = children.get(index1);
        if (f == null) {
            Logger.e(tag, "onActivityResult: get null fragment of generation 1");
            return;
        }
        if (index2 < 1) {
            f.onActivityResult(requestCode & 127, resultCode, data);
            return;
        }
        children = f.getChildFragmentManager().getFragments();
        index2--;
        if (children == null || index2 >= children.size()) {
            Logger.e(tag, "onActivityResult: error for generation 2: no Fragment in " + children + " matches index: " + index2);
            return;
        }
        f = children.get(index2);
        if (f == null) {
            Logger.e(tag, "onActivityResult: get null fragment of generation 2");
            return;
        }
        if (index3 < 1) {
            f.onActivityResult(requestCode & 127, resultCode, data);
            return;
        }
        children = f.getChildFragmentManager().getFragments();
        index3--;
        if (children == null || index3 >= children.size()) {
            Logger.e(tag, "onActivityResult: error for generation 3: no Fragment in " + children + " matches index: " + index3);
            return;
        }
        f = children.get(index3);
        if (f == null) {
            Logger.e(tag, "onActivityResult: get null fragment of generation 3");
            return;
        }
        f.onActivityResult(requestCode & 127, resultCode, data);
    }
}
