package com.andy.lib.Base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by andy on 2018/7/11.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected static String TAG;

    protected Activity mActivity;
    protected Context mContext;

    /****************************abstract*************************************/

    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * 初始化数据
     *
     * @param savedInstanceState
     */
    protected void initData(Bundle savedInstanceState) {
    }

    /**
     * 初始化零件
     */
    protected void initViews() {
    }
    /**
     * 初始化点击事件
     */
    protected void initClick() {
    }

    /**
     * 执行逻辑
     */
    protected void processLogic() {
    }

    protected void beforeDestroy() {
    }
    protected void initToolbar() {
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());
        mActivity = this;
        mContext = this;
        // 设置 TAG
        TAG = this.getClass().getSimpleName();
        //init
        initData(savedInstanceState);
        initToolbar();
        initViews();
        initClick();
        processLogic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beforeDestroy();
    }
}
