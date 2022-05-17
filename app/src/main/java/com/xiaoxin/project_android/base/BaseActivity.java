package com.xiaoxin.project_android.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.SDKInitializer;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * BaseActivity
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    public Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        ActivityCollector.addActivity(this);

        View view = LayoutInflater.from(this).inflate(getLayoutId(), null);
        setContentView(view);

        unbinder = ButterKnife.bind(this);

        initView();
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null && null != imm) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showToast("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 显示提示  Toast
     *
     * @param msg
     */
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"-------------onRestart() invoke....-------------");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"-------------onStart() invoke....-------------");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"-------------onResume() invoke....-------------");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"-------------onPause() invoke....-------------");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"-------------onStop() invoke....-------------");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"-------------onDestroy() invoke....-------------");
        unbinder.unbind();
        ActivityCollector.removeActivity(this);
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

}